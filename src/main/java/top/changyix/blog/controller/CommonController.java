package top.changyix.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.changyix.blog.common_utils.BitUtils;
import top.changyix.blog.common_utils.Result;
import top.changyix.blog.config.CommonConfig;
import top.changyix.blog.entity.*;
import top.changyix.blog.service.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;
import java.util.Map;

@RestController
@RequestMapping("/api/common")
public class CommonController {
    @Value("${pageSetting.pageSize}")
    private int commonPageSize;
    @Value("${pageSetting.bigPageSize}")
    private int bigPageSize;
    @Value("${pageSetting.smallPageSize}")
    private int smallPageSize;
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
    @RequestMapping("/indexArticlePageByTag")
    public Result indexArticlePageByTag(@RequestParam int page,@RequestParam int tagid){
        Page<Article> pages = new Page<>(page, commonPageSize);

        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> params=new HashMap<String, Object> ();
        params.put("status&1",1);//??????????????????
        params.put("status&2",0);//???????????????
        if(user instanceof User){
            Map<String,Object> personParams = new HashMap<>();
            personParams.put("status&1",1);
            if (((User) user).getUserType()<User.???????????????){ //????????????
                personParams.put("userid",((User) user).getId());
            }
            pages = articleService
                    .page(pages,
                            new QueryWrapper<Article>()
                                    .inSql("id","select articleid from article_tag where tagid = "+tagid + " ")
                                    .and( wrapper -> wrapper
                                            .allEq(params)
                                            .or( wra -> wra.allEq(personParams) ))
                                    .orderByDesc("update_time")); //????????????????????????
        }else {
            pages = articleService.page(pages,
                    new QueryWrapper<Article>()
                            .inSql("id","select articleid from article_tag where tagid = "+tagid+ " ")
                            .allEq(params)
                            .orderByDesc("update_time")); //????????????????????????
        }

        //?????????????????????????????????tagid?????????
        List<Article> articles = pages.getRecords();
        ArrayList<PureUser> pureUsers = new ArrayList<>();
        ArrayList<ArrayList<Integer>> tagsID = new ArrayList<>();
        for (Article article : articles){//???????????????????????????
            article.setContent("");//????????????????????????,???????????????????????????
            pureUsers.add(PureUser.conversionNoTitle(userService.getById(article.getUserid())));//??????????????????
            List<ArticleTag> articleTags  = articleTagService.query().eq("articleid",article.getId()).list();
            ArrayList<Integer> tid = new ArrayList<>();
            for (ArticleTag articleTag : articleTags){
                tid.add(articleTag.getTagid());
            }
            tagsID.add(tid);
        }
        return Result.success()
                .append("articles",pages)
                .append("tags",tagsID)
                .append("authors",pureUsers);
    }

    //?????????????????????????????????typeid?????????
    @RequestMapping("/indexArticlePageByType")
    public Result indexArticlePageByType(@RequestParam int page,@RequestParam int type){
        Page<Article> pages = new Page<>(page, commonPageSize);
        //?????????????????????????????????tagid?????????
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> params=new HashMap<String, Object> ();
        params.put("status&1",1);//??????????????????
        params.put("status&2",0);//???????????????
        params.put("typeid",type);//??????type
        if(user instanceof User){
            Map<String,Object> personParams = new HashMap<>();
            personParams.put("status&1",1);
            personParams.put("typeid",type);
            if (((User) user).getUserType()<User.???????????????){ //????????????
                personParams.put("userid",((User) user).getId());
            }
            pages = articleService
                    .page(pages,
                            new QueryWrapper<Article>()
                                    .allEq(params)
                                    .or(wrapper -> wrapper
                                            .allEq(personParams))
                                    .orderByDesc("update_time")); //????????????????????????
        }else {
            pages = articleService.page(pages,new QueryWrapper<Article>().allEq(params).orderByDesc("update_time")); //????????????????????????
        }
        List<Article> articles = pages.getRecords();
        ArrayList<PureUser> pureUsers = new ArrayList<>();
        ArrayList<ArrayList<Integer>> tagsID = new ArrayList<>();
        for (Article article : articles){//???????????????????????????
            article.setContent("");//????????????????????????,???????????????????????????
            pureUsers.add(PureUser.conversionNoTitle(userService.getById(article.getUserid())));//??????????????????
            List<ArticleTag> articleTags  = articleTagService.query().eq("articleid",article.getId()).list();
            ArrayList<Integer> tid = new ArrayList<>();
            for (ArticleTag articleTag : articleTags){
                tid.add(articleTag.getTagid());
            }
            tagsID.add(tid);
        }
        return Result.success()
                .append("articles",pages)
                .append("tags",tagsID)
                .append("authors",pureUsers);
    }

    @RequestMapping("/indexArticlePageSearch")
    public Result indexArticlePageSearch(@RequestParam int page,@RequestParam String searchString){
        Page<Article> pages = new Page<>(page, commonPageSize);
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> params=new HashMap<String, Object> ();
        if(user instanceof User){
            params.put("status&1",1);//??????????????????
            params.put("status&2",0);//???????????????
            Map<String,Object> personParams = new HashMap<>();
            personParams.put("status&1",1);
            if (((User) user).getUserType()<User.???????????????){ //????????????
                personParams.put("userid",((User) user).getId());
            }
            pages = articleService
                    .page(pages,
                            new QueryWrapper<Article>()
                                    .and(articleQueryWrapper -> articleQueryWrapper
                                                    .allEq(params)
                                                    .or(wrapper -> wrapper
                                                            .allEq(personParams))
                                            )
                                    .and( articleQueryWrapper -> articleQueryWrapper
                                            .like("title",searchString)
                                            .or()
                                            .like("content",searchString))
                                    .orderByDesc("update_time")); //????????????????????????
        }else {
            params.put("status&1",1);//??????????????????
            params.put("status&2",0);//???????????????
            pages = articleService
                    .page(pages,
                            new QueryWrapper<Article>()
                                    .allEq(params)
                                    .and(articleQueryWrapper -> articleQueryWrapper
                                                    .like("title",searchString)
                                                    .or()
                                                    .like("content",searchString)
                                    )
                                    .orderByDesc("update_time")); //????????????????????????
        }//select * from article where ( status&1 == 1 and status&2 == 0 ) and ( title like %[searchString]% or content %[searchString]% orderBy update_time desc )
        List<Article> articles = pages.getRecords();
        ArrayList<PureUser> pureUsers = new ArrayList<>();
        ArrayList<ArrayList<Integer>> tagsID = new ArrayList<>();
        for (Article article : articles){//???????????????????????????
            article.setContent("");//????????????????????????,???????????????????????????
            pureUsers.add(PureUser.conversionNoTitle(userService.getById(article.getUserid())));//??????????????????
            List<ArticleTag> articleTags  = articleTagService.query().eq("articleid",article.getId()).list();
            ArrayList<Integer> tagid = new ArrayList<>();
            for (ArticleTag articleTag : articleTags){
                tagid.add(articleTag.getTagid());
            }
            tagsID.add(tagid);
        }
        return Result.success()
                .append("articles",pages)
                .append("tags",tagsID)
                .append("authors",pureUsers);
    }

    @RequestMapping("/indexArticlePage")
    public Result indexArticlePage(@RequestParam int page){
        Page<Article> pages = new Page<>(page, commonPageSize);
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> params=new HashMap<String, Object> ();
        if(user instanceof User){
            params.put("status&1",1);//??????????????????
            params.put("status&2",0);//???????????????
            Map<String,Object> personParams = new HashMap<>();
            personParams.put("status&1",1);
            if (((User) user).getUserType()<User.???????????????){ //????????????
                personParams.put("userid",((User) user).getId());
            }
            pages = articleService
                    .page(pages,
                            new QueryWrapper<Article>()
                                    .allEq(params)
                                    .or(wrapper -> wrapper
                                            .allEq(personParams))
                                    .orderByDesc("update_time")); //????????????????????????
        }else {
            params.put("status&1",1);//??????????????????
            params.put("status&2",0);//???????????????
            pages = articleService.page(pages,new QueryWrapper<Article>().allEq(params).orderByDesc("update_time")); //????????????????????????
        }
        List<Article> articles = pages.getRecords();
        ArrayList<PureUser> pureUsers = new ArrayList<>();
        ArrayList<ArrayList<Integer>> tagsID = new ArrayList<>();
        for (Article article : articles){//???????????????????????????
            article.setContent("");//????????????????????????,???????????????????????????
            pureUsers.add(PureUser.conversionNoTitle(userService.getById(article.getUserid())));//??????????????????
            List<ArticleTag> articleTags  = articleTagService.query().eq("articleid",article.getId()).list();
            ArrayList<Integer> tagid = new ArrayList<>();
            for (ArticleTag articleTag : articleTags){
                tagid.add(articleTag.getTagid());
            }
            tagsID.add(tagid);
        }
        return Result.success()
                .append("articles",pages)
                .append("tags",tagsID)
                .append("authors",pureUsers);
    }
    @RequestMapping("/basicMessage")
    public Result basicMessage(){
        List<ArticleType> articleTypes = articleTypeService.list();
        List<Tag> tags = tagService.list();
        Result result = Result.success().setMessage("??????????????????");
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user instanceof  User){
            user = PureUser.conversion((User) user);
        }
        List<top.changyix.blog.entity.Map> list = mapService.list();
        return result
                .append("types",articleTypes)
                .append("tags",tags)
                .append("user",user)
                .append("map",list);
    }
    /*
    ??????????????????
     */
    @RequestMapping("/getArticleDetail")
    public Result getArticleDetail(@RequestParam int aritlceID){
        Article article = articleService.getById(aritlceID);
        if( article==null || BitUtils.and(article.getStatus(),Article.STATE_PUBLISHED)!=1){
            return Result.logicError("????????????");
        }else{
            User author = userService.getById(article.getUserid());
            /**
             * ???????????????????????????????????????????????????????????????
             */
            if( BitUtils.and(article.getStatus(),Article.STATE_PERSONAL) == Article.STATE_PERSONAL ){
                User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if(user.getId() == author.getId()){

                }else if (user.getUserType()<=author.getUserType()){
                    return Result.logicError("????????????");
                }
            }

            ArticleType articleType = articleTypeService.getById(article.getTypeid());
            Result result = Result.success();
            List<ArticleTag> articleTags = articleTagService.query().eq("articleid",aritlceID).list();
            ArrayList<Tag> tags = new ArrayList<>();//?????????????????????
            for (ArticleTag a:articleTags) {
                tags.add(tagService.getById(a.getTagid()));
            }
            article.setPageviews(article.getPageviews()+1);
            articleService.saveOrUpdate(article);
            result.append("author",PureUser.conversion(author));
            result.append("tags",tags);
            result.append("article",article);
            result.append("type",articleType);
            return result;
        }
    }
    /*
    ????????????????????????
     */
    public Result getArticleComment(@RequestParam int articleID,@RequestParam(required = false) int page){
        Map<Integer,PureUser> pureUserMap = new HashMap();
        Page<Comment> commentPage = new Page<>(page, commonPageSize);
        commentPage = commentService.page(commentPage
                , new QueryWrapper<Comment>()
                        .eq("articleID", articleID));
        List<Comment> comments = commentPage.getRecords();
        for (Comment comment:comments) {
            pureUserMap.put( comment.getUserid() , (PureUser) pureUserMessage(comment.getUserid()).getData().get("userMassage"));
        }
        return Result.success().append("comments",commentPage)
                               .append("users",pureUserMap);
    }

    /*
    ????????????id????????????????????????????????????????????????
     */
    public Result pureUserMessage(int userID){
        User user = userService.getById(userID);
        if(user!=null){
            PureUser pureUser = PureUser.conversion(user);
            return Result.success().append("userMassage",pureUser);
        }else{
            return Result.error().setMessage("??????????????????");
        }
    }
    /*
    ??????????????????????????????
     */
    public Result ArticlesByTag(@RequestParam int tagid){
        List<ArticleTag> articleTags = articleTagService.list(new QueryWrapper<ArticleTag>().eq("tagid",tagid));
        ArrayList<Article> articles = new ArrayList<>();
        for (ArticleTag t:articleTags) {
            articles.add(articleService.getById(t.getArticleid()));
        }
        return Result.success().append("articles",articles);
    }
    //??????????????????
    @RequestMapping("/register")
    public Result register(@RequestParam("avatar") MultipartFile avatar,
                           @RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam("mobile") String mobile,
                           @RequestParam("nickname") String nickname,
                           HttpServletRequest req
                           ){
        //?????????????????????????????????????????????
        User user = new User();
        Result result = saveAvatar(req,avatar);
        if(result.isSuccess()){
            user.setAvatar(result.getData().get("base64").toString());
        }else{
            return result;
        }
        user.setCreateTime(new Date());
        user.setLocked("F");
        user.setMobile(mobile);
        user.setPassword(bCryptPasswordEncoder.encode(password)); //????????????
        user.setUsername(username);
        user.setUserType(User.????????????);
        user.setNickname(nickname);
        if(userService.count()==0){
            user.setUserType(User.???????????????);
        }
        if (userService.lambdaQuery().eq(User::getUsername,username).count()!=0){
            return Result.error().setMessage("??????????????????");
        }
        return userService.save(user)?Result.success():Result.error();
    }

    //???????????????Base64?????????????????????
    private Result saveAvatar(HttpServletRequest req, MultipartFile multipartFile){
        String[] strings = multipartFile.getContentType().split("/");
        //??????????????????
        if( !(strings.length==2 && "image".equals(strings[0]))){
            return Result.error().setMessage("????????????");
        }
        String base64 = null;
        try {
            File file = new File("file.png");
            if(file.exists()){
                file.delete();
                file.createNewFile();
            }else{
                file.createNewFile();
            }
            //????????????
            Thumbnails.of(multipartFile.getInputStream()).size(48,48).toFile(file);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bytes = new byte[fileInputStream.available()];
            fileInputStream.read(bytes);
            base64 = new String(Base64.getEncoder().encode(bytes));
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error().setMessage(e.getMessage());
        }
        return Result.success("base64",base64);
    }
    /*
    arrayList?????????????????????,
    page????????????
    pageSize????????????
     */
    private <T> List<T> getPage(ArrayList<T> arrayList, int page, int pageSize){
        if(arrayList==null || arrayList.size()==0){
            return new ArrayList<T>();
        }
        //????????????
        if(page<=0){
            page = 1;
        }
        if(pageSize<=0 || pageSize> CommonConfig.PageSize){
            pageSize = CommonConfig.PageSize;
        }
        int totalSize = arrayList.size();
        if(totalSize/(page*pageSize)==0){
            page = 1;
        }
        int beginIndex = pageSize *(page -1),endIndex = pageSize *(pageSize)<=totalSize
                ?pageSize *(pageSize):beginIndex + totalSize%pageSize;
        return arrayList.subList(beginIndex,endIndex);
    }

    @RequestMapping("/articleComments")
    public Result articleComments(@RequestParam int id, @RequestParam int page) {
        ArrayList<List<Comment>> comments = new ArrayList<>();
        Page<Comment> commentPage = new Page<>(page, commonPageSize);
        commentPage = commentService.page(commentPage, new QueryWrapper<Comment>().isNull("parent_id").eq("article_id",id));
        ArrayList<Integer> userids = new ArrayList<>();
        List<Comment> commen = commentPage.getRecords();
        for (Comment comment : commen) {
            List<Comment> com = commentService.list(new QueryWrapper<Comment>().eq("floor_id", comment.getFloorId()));
            //???????????????????????????
            for (Comment c : com) {
                userids.add(c.getUserid());
            }
            comments.add(com);
        }
        List<PureUser> pureUsers = null;
        if(userids.size()!=0){
            pureUsers = PureUser.conversion(userService.listByIds(userids));
        }
        commentPage.setRecords(null);
        return Result
                .success()
                .append("comments", comments)
                .append("users", pureUsers)
                .append("pageMessage", commentPage);
    }

    //????????????
    @RequestMapping("/dateArticles")
    public Result dateArticles(){
        ArrayList<ArrayList<Article>> arrayLists = new ArrayList<>();
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Article> articles = null;
        Map<String, Object> params=new HashMap<String, Object> ();
        params.put("status&1",1);//??????????????????
        params.put("status&2",0);//???????????????
        if(user instanceof User){
            Map<String,Object> personParams = new HashMap<>();
            personParams.put("status&1",1);
            if (((User) user).getUserType()<User.???????????????){ //????????????
                personParams.put("userid",((User) user).getId());
            }
            articles = articleService.list(new QueryWrapper<Article>()
                    .allEq(params)
                    .or( wrapper -> wrapper.allEq(personParams))
                    .orderByDesc("update_time"));
        }else {
            articles = articleService.list(new QueryWrapper<Article>()
                    .allEq(params)
                    .orderByDesc("update_time")); //?????????????????????????????????????????????
        }
        List<Date> dates = new ArrayList<>();
        if(articles.size()!=0){
            Date firstDate = articles.get(0).getUpdateTime();
            Date date = new Date(firstDate.getYear(),firstDate.getMonth(),1);//???????????????????????????????????????
            ArrayList<Article> articleArrayList = new ArrayList<>();
            dates.add(date);
            for (int i=0;i<articles.size();i++){
                Article a = articles.get(i);
                a.setContent("");
                a.setPureString("");
                if(articles.get(i).getUpdateTime().after(date)){
                    articleArrayList.add(a);
                }else{
                    arrayLists.add(articleArrayList);
                    articleArrayList = new ArrayList<>();
                    firstDate = a.getUpdateTime();
                    date = new Date(firstDate.getYear(),firstDate.getMonth(),1);
                    dates.add(date);
                    articleArrayList.add(a);
                }
            }
            arrayLists.add(articleArrayList);
        }
        return Result
                .success()
                .append("articles",arrayLists)
                .append("dates",dates);
    }
    @RequestMapping("/modify_password")
    public Result modify_password(@RequestParam String username,@RequestParam String password,@RequestParam String newpassword){
        User user = userService.getOne(new QueryWrapper<User>().eq("username",username));
        if(!bCryptPasswordEncoder.matches(password,user.getPassword())){
            return Result.logicError("?????????????????????");
        }else{
            user.setPassword(bCryptPasswordEncoder.encode(newpassword));
            if (userService.saveOrUpdate(user)){
                return Result.success("????????????");
            }else{
                return Result.logicError("????????????");
            }
        }
    }
    @RequestMapping("/allFriends")
    public Result allFriends(){
        List<FriendshipChain> friendshipChains = friendshipChainService.list(new QueryWrapper<FriendshipChain>().orderByDesc("`order`"));
        return Result
                .success()
                .append("friends",friendshipChains);
    }

    @RequestMapping("/friends")
    public Result friends(@RequestParam int page){
        Page<FriendshipChain> friendshipChainPage = new Page<>(page,bigPageSize);
        friendshipChainService.page(friendshipChainPage,new QueryWrapper<FriendshipChain>().orderByDesc("`order`"));
        return Result
                .success()
                .append("friends",friendshipChainPage);
    }
}
