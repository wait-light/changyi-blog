package top.changyix.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.changyix.blog.common_utils.BitUtils;
import top.changyix.blog.common_utils.Markdown2Text;
import top.changyix.blog.common_utils.Result;
import top.changyix.blog.entity.*;
import top.changyix.blog.entity.controller_entity.AddArticle;
import top.changyix.blog.entity.controller_entity.EditArticle;
import top.changyix.blog.entity.controller_entity.SaveArticle;
import top.changyix.blog.service.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequestMapping("/api/admin")
@RestController
@Secured({"ROLE_ADMIN", "ROLE_SUPERADMIN"})
public class AdminController {
    @Value("${pageSetting.pageSize}")
    private int commonPageSize;
    @Value("${pageSetting.bigPageSize}")
    private int bigPageSize;
    @Value("${pageSetting.smallPageSize}")
    private int smallPageSize;
    @Value("${pureStringSize}")
    public int pureStringSize;

    @Autowired
    UserService userService;
    @Autowired
    ArticleService articleService;
    @Autowired
    CommentService commentService;
    @Autowired
    TagService tagService;
    @Autowired
    FriendshipChainService friendshipChainService;
    @Autowired
    MapService mapService;
    @Autowired
    HistoryArticleService historyArticleService;
    @Autowired
    ArticleTypeService articleTypeService;
    @Autowired
    ArticleTagService articleTagService;
    @Autowired
    FeedbackService feedbackService;
    @Autowired
    MessageWaitingService messageWaitingService;
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @RequestMapping("/addUser")
    public Result Register() {
        return Result.deny();
    }

    @RequestMapping("/saveArticle")
    public Result saveArticle(@RequestBody SaveArticle addArticle) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //??????????????????
        Article article = new Article();
        article.setContent(addArticle.content);
        article.setPageviews(0);
        article.setTitle(addArticle.title);
        article.setTypeid(addArticle.getType());
        article.setPic(addArticle.pic);
        if(addArticle.isPersonal()){
            article.setStatus(BitUtils.allOr(article.getStatus(),Article.STATE_PERSONAL));
        }
        String pureString = Markdown2Text.convert(addArticle.content);//??????markdown??????????????????
        if (pureString.length() > pureStringSize) {
            pureString = pureString.substring(0, pureStringSize);
        }
        article.setPureString(pureString);
        Date now = new Date();
        article.setCreateTime(now);
        article.setStatus(BitUtils.allAnd(article.getStatus(),~Article.STATE_PUBLISHED));//????????????(???????????????)
        article.setUpdateTime(now);
        article.setUserid(user.getId());//????????????
        articleService.save(article);
        for (int tagID : addArticle.getTags()) {//????????????
            ArticleTag articleTag = new ArticleTag();
            articleTag.setTagid(tagID);
            articleTag.setArticleid(article.getId());
            articleTagService.save(articleTag);
        }
        return Result.success().setMessage("????????????");
    }

    @RequestMapping("/addArticle")
    public Result addArticle(@RequestBody AddArticle addArticle) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //??????????????????
        Article article = new Article();
        article.setContent(addArticle.content);
        article.setPageviews(0);
        article.setTitle(addArticle.title);
        article.setTypeid(addArticle.getType());
        article.setPic(addArticle.getPic());
        if(addArticle.isPersonal()){
            article.setStatus(Article.STATE_PERSONAL);
        }
        String pureString = Markdown2Text.convert(addArticle.content);//??????markdown??????????????????
        if (pureString.length() > pureStringSize) {
            pureString = pureString.substring(0, pureStringSize);
        }
        article.setPureString(pureString);
        Date now = new Date();
        article.setCreateTime(now);

        if (addArticle.getTime() == null || (addArticle.getTime() != null && addArticle.getTime().before(now))) {//????????????????????????,??????????????????????????????????????????????????????????????????
            article.setPublishTime(now);
            article.setUpdateTime(now);
            article.setStatus(BitUtils.allOr(article.getStatus(),Article.STATE_PUBLISHED));
        } else {
            article.setStatus(BitUtils.allAnd(article.getStatus(),~Article.STATE_PUBLISHED));
            article.setUpdateTime(now);
            article.setPublishTime(addArticle.getTime());//???????????????????????????????????????
        }
        article.setUserid(user.getId());//????????????
        articleService.save(article);
        for (int tagID : addArticle.getTags()) {//????????????
            ArticleTag articleTag = new ArticleTag();
            articleTag.setTagid(tagID);
            articleTag.setArticleid(article.getId());
            articleTagService.save(articleTag);
        }
        return Result.success().setMessage("??????????????????");
    }
    @RequestMapping("/changeArticlePersonal")
    public Result changeArticlePersonal(@RequestParam int id){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Article article = articleService.getById(id);
        if (  !(article.getUserid() == user.getId() || user.getUserType()>= User.???????????????)){
            return Result.logicError("????????????");
        }
        article.setStatus(BitUtils.allXor(article.getStatus(),Article.STATE_PERSONAL));
        if (articleService.saveOrUpdate(article)){
            return Result.success("????????????");
        }else{
            return Result.logicError("????????????");
        }
    }

    @RequestMapping("/articleList")
    public Result articleList(@RequestParam int page) {
        Page<Article> pages = new Page<>(page, commonPageSize);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //??????????????????????????????????????????????????????????????????????????????
        if (user.getUserType() == User.??????????????? ){
            pages = articleService.page(pages, new QueryWrapper<Article>().orderByDesc("update_time")); //????????????????????????
        }else{
            pages = articleService.page(pages,new QueryWrapper<Article>().eq("userid",user.getId()).orderByDesc("update_time"));
        }
        List<Article> articles = pages.getRecords();
        ArrayList<PureUser> pureUsers = new ArrayList<>();
        ArrayList<ArrayList<Integer>> tagsID = new ArrayList<>();
        for (Article article : articles) {//???????????????????????????
            article.setContent("");//????????????????????????,???????????????????????????
            pureUsers.add(PureUser.conversion(userService.getById(article.getUserid())));//??????????????????
            List<ArticleTag> articleTags = articleTagService.query().eq("articleid", article.getId()).list();
            ArrayList<Integer> tagid = new ArrayList<>();
            for (ArticleTag articleTag : articleTags) {
                tagid.add(articleTag.getTagid());
            }
            tagsID.add(tagid);
        }
        return Result.success()
                .append("articles", pages)
                .append("authors", pureUsers);
    }

    @RequestMapping("/articleDetail")
    public Result articleDetail(@RequestParam int articleID) {
        Article article = articleService.getById(articleID);
        if (article == null) {
            return Result.logicError("????????????");
        }
        User me = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User author = userService.getById(article.getUserid());
        //????????????????????????????????????????????????
        if( !(me.getId() == author.getId() || me.getUserType() > author.getUserType())){
            return Result.logicError("????????????");
        }
        ArticleType articleType = articleTypeService.getById(article.getTypeid());
        Result result = Result.success();
        List<ArticleTag> articleTags = articleTagService.query().eq("articleid", articleID).list();
        ArrayList<Integer> tags = new ArrayList<>();//?????????????????????
        for (ArticleTag a : articleTags) {
            tags.add(a.getTagid());//??????tagid ?????????
        }
        result.append("author", PureUser.conversion(author));
        result.append("tags", tags);
        result.append("article", article);
        result.append("type", articleType);
        return result;
    }

    @RequestMapping("/articleDelete")
    public Result articleDelete(@RequestParam int id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Article article = articleService.getById(id);
        if (article == null || !(article.getUserid() == user.getId() || user.getUserType() == 2)) {
            return Result.logicError("????????????/???????????????");
        }
        articleService.removeById(id);//????????????
        historyArticleService.remove(new QueryWrapper<HistoryArticle>().eq("article_id", id));//????????????????????????
        return Result.success("??????????????????");
    }

    @RequestMapping("/editArticle")
    public Result editArticle(@RequestBody EditArticle editArticle) {
        Article thisArticle = articleService.getById(editArticle.getId());
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(thisArticle.getUserid() == user.getId() || user.getUserType() == 2)) { //????????????????????????????????????????????????
            return Result.logicError("????????????");
        }
        Date now = new Date();
        HistoryArticle historyArticle = new HistoryArticle();
        historyArticle.setArticleId(thisArticle.getId());
        historyArticle.setContent(thisArticle.getContent());
        historyArticle.setPureString(thisArticle.getPureString());
        historyArticle.setTitle(thisArticle.getTitle());
        historyArticle.setUpdateTime(thisArticle.getUpdateTime());
        historyArticle.setTypeid(thisArticle.getTypeid());
        articleTagService.remove(new QueryWrapper<ArticleTag>().eq("articleid", thisArticle.getId()));//?????????????????????
        ArrayList<ArticleTag> articleTags = new ArrayList<>();
        for (int tagid : editArticle.getTags()) { //????????????
            ArticleTag articleTag = new ArticleTag();
            articleTag.setArticleid(thisArticle.getId());
            articleTag.setTagid(tagid);
            articleTags.add(articleTag);
        }
        articleTagService.saveBatch(articleTags);
        String pureString = Markdown2Text.convert(editArticle.getContent());
        if (pureString.length()>=pureStringSize){
            pureString = pureString.substring(0,pureStringSize);
        }
        thisArticle.setContent(editArticle.content);
        thisArticle.setTypeid(editArticle.getType());
        thisArticle.setTitle(editArticle.getTitle());
        thisArticle.setPureString(pureString);
        thisArticle.setUpdateTime(now);
        if (editArticle.getTime() != null && editArticle.getTime().after(now)) { //?????????????????????????????????
            thisArticle.setPublishTime(editArticle.getTime());
            thisArticle.setStatus(BitUtils.and(thisArticle.getStatus(),~Article.STATE_PUBLISHED));//?????????????????????
        } else {
            thisArticle.setUpdateTime(now);
        }
        //????????????
        historyArticleService.save(historyArticle);
        articleService.saveOrUpdate(thisArticle);
        return Result.success().setMessage("??????????????????");
    }

    @RequestMapping("/users")
    public Result users(@RequestParam(required = false, defaultValue = "1") int page) {
        Page<User> pager = new Page<>(page, bigPageSize);
        pager = userService.page(pager);
        return Result
                .success()
                .append("users", pager);
    }

    @RequestMapping("/resetPassword")
    public Result resetPassword(@RequestParam int id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();//?????????
        User u = userService.getById(id);
        if (u.getId() == user.getId()) {
            u.setPassword(bCryptPasswordEncoder.encode("123456"));
            if (userService.saveOrUpdate(u)) {
                return Result.success("??????????????????");
            } else {
                return Result.logicError("??????????????????");
            }
        }
        if (user.getUserType() <= u.getUserType()) { //?????????????????????????????????
            return Result.logicError("????????????");
        }
        u.setPassword(bCryptPasswordEncoder.encode("123456"));
        if (userService.saveOrUpdate(u)) {
            return Result.success("??????????????????");
        } else {
            return Result.logicError("??????????????????");
        }
    }

    @RequestMapping("/deleteUser")
    public Result deleteUser(@RequestParam int id) {
        User user = userService.getById(id);
        User ControllerUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getUserType() == 2) {
            return Result.logicError("???????????????");
        }
        if (user.getUserType() >= ControllerUser.getUserType()) { //????????????????????????
            return Result.logicError("????????????");
        }
        if (userService.removeById(id)) {
            return Result.success("????????????");
        } else {
            return Result.logicError("????????????");
        }
    }

    @RequestMapping("/upUser")
    public Result upUser(@RequestParam int id) {
        User ControllerUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getById(id);
        if (user.getUserType() == 0 && ControllerUser.getUserType() == 2) {
            user.setUserType(1);
            if (userService.saveOrUpdate(user)) {
                return Result.success("????????????");
            } else {
                return Result.logicError("????????????");
            }
        } else {
            return Result.logicError("???????????????");
        }
    }

    @RequestMapping("/downUser")
    public Result downUser(@RequestParam int id) {
        User ControllerUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getById(id);
        if (user.getUserType() == 1 && ControllerUser.getUserType() == 2) {
            user.setUserType(0);
            if (userService.saveOrUpdate(user)) {
                return Result.success("????????????");
            } else {
                return Result.logicError("????????????");
            }
        } else {
            return Result.logicError("???????????????");
        }
    }
        @RequestMapping("/lockOrUnlockUser")
    public Result lockOrUnlockUser(@RequestParam int id) {
        User ControllerUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getById(id);
        if (user.getUserType()<ControllerUser.getUserType()) { //????????????????????????????????????????????????
            user.setLocked(user.getLocked().equals("T")?"F":"T");
            if(userService.saveOrUpdate(user)){
                return Result.success("??????????????????");
            }else{
                return Result.success("??????????????????");
            }
        }
        return Result.logicError("???????????????");
    }

    @RequestMapping("/articleHistory")
    public Result articleHistory(@RequestParam int id){
        User controllerUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Article article = articleService.getById(id);
        if(!(controllerUser.getId()==article.getUserid() || controllerUser.getUserType()==2)){
            return Result.logicError("????????????");
        }
        List<HistoryArticle> historyArticles = historyArticleService.list(new QueryWrapper<HistoryArticle>().eq("article_id",id));
        return Result
                .success()
                .append("historys",historyArticles);
    }
    @RequestMapping("/historyToArticle")
    public Result historyToArticle(@RequestParam int historyId,@RequestParam int articleID){
        User controllerUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Article article = articleService.getById(articleID);
        if(!(controllerUser.getId()==article.getUserid() || controllerUser.getUserType()==2)){
            return Result.logicError("????????????");
        }
        HistoryArticle historyArticle = historyArticleService.getById(historyId);
        article.setPureString(historyArticle.getPureString());
        article.setContent(historyArticle.getContent());
        article.setTitle(historyArticle.getTitle());
        article.setUpdateTime(new Date());
        article.setTypeid(historyArticle.getTypeid());
        if(articleService.saveOrUpdate(article)){
            return Result.success("????????????");
        }else {
            return Result.logicError("????????????");
        }
    }
    @RequestMapping("/downArticle")
    public Result downArticle(@RequestParam int id){
        User controllerUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Article article = articleService.getById(id);
        if(!(controllerUser.getId()==article.getUserid() || controllerUser.getUserType()==2)){
            return Result.logicError("????????????");
        }
        article.setStatus(BitUtils.and(article.getStatus(),~ Article.STATE_PUBLISHED));//?????????????????????
        if(articleService.saveOrUpdate(article)){
            return Result.success("??????????????????");
        }else{
            return Result.logicError("??????????????????");
        }
    }
    @RequestMapping("/upArticle")
    public Result upArticle(@RequestParam int id){
        User controllerUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Article article = articleService.getById(id);
        if(!(controllerUser.getId()==article.getUserid() || controllerUser.getUserType()==2)){
            return Result.logicError("????????????");
        }
        article.setStatus(BitUtils.or(article.getStatus(),Article.STATE_PUBLISHED)); //???????????????????????????
        article.setPublishTime(new Date());
        if(articleService.saveOrUpdate(article)){
            return Result.success("????????????");
        }else{
            return Result.logicError("????????????");
        }
    }
    @RequestMapping("/comments")
    public Result comments(@RequestParam int page){
        Page<Comment> pager = new Page<>(page,bigPageSize);
        commentService.page(pager);
        return Result
                .success()
                .append("comments",pager);
    }



    @RequestMapping("/adminHomeMessage")
    public Result adminHomeMessage(){
        int userNumber = userService.count();
        int articleNumber = articleService.count(new QueryWrapper<Article>().eq("status&1","1"));
        int commentNumber = commentService.count();
        int friendNumber = friendshipChainService.count();
        return Result
                .success()
                .append("userNumber",userNumber)
                .append("articleNumber",articleNumber)
                .append("commentNumber",commentNumber)
                .append("friendNumber",friendNumber);
    }

    @RequestMapping("/addTag")
    public Result addTag(@RequestParam String name) {
        if (tagService.getOne(new QueryWrapper<Tag>().eq("name", name)) != null) {
            return Result.logicError("??????????????????");
        }
        Tag tag = new Tag();
        tag.setName(name);
        return tagService.save(tag) ? Result.success().setMessage("????????????") : Result.error().setMessage("????????????");
    }

    @RequestMapping("/addType")
    public Result addType(@RequestParam String name) {
        if (articleTypeService.getOne(new QueryWrapper<ArticleType>().eq("name", name)) != null) {
            return Result.logicError("??????????????????");
        }
        ArticleType type = new ArticleType();
        type.setName(name);
        return articleTypeService.save(type) ? Result.success().setMessage("????????????") : Result.error().setMessage("????????????");
    }
}
