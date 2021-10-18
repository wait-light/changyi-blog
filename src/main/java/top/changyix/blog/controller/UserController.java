package top.changyix.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.changyix.blog.common_utils.Result;
import top.changyix.blog.entity.Comment;
import top.changyix.blog.entity.User;
import top.changyix.blog.entity.controller_entity.MakeComment;
import top.changyix.blog.service.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@Secured({"ROLE_ADMIN", "ROLE_SUPERADMIN", "ROLE_USER"})
public class UserController {
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

    @Value("${pageSetting.pageSize}")
    private int commonPageSize;
    @Value("${pageSetting.bigPageSize}")
    private int bigPageSize;
    @Value("${pageSetting.smallPageSize}")
    private int smallPageSize;



    @RequestMapping("/makeComment")
    public Result makeComment(@RequestParam int article_id, @RequestParam String content,
                              @RequestParam(required = false, defaultValue = "-1") int floor_id, @RequestParam(required = false, defaultValue = "-1") int parent_id) {
        if(content.length()<=1){
            return Result.logicError("字符长度过小");
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Comment comment = new Comment();
        Date now = new Date();
        comment.setArticleId(article_id);
        comment.setContent(content);
        comment.setCreateTime(now);
        comment.setUserid(user.getId());
        if (floor_id != -1) {
            comment.setFloorId(floor_id);
        }
        if (parent_id != -1) {
            comment.setParentId(parent_id);
        }
        if (commentService.save(comment))
        {
            if(floor_id == -1){
                comment.setFloorId(comment.getId());
                commentService.saveOrUpdate(comment);
            }
            return Result.success("评论成功");
        }

        else
            return Result.logicError("评论失败");
    }

    @RequestMapping("/deleteComment")
    public Result deleteComment(@RequestParam int id){
        Comment comment = commentService.getById(id);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User u = userService.getById(comment.getUserid());
        if(comment.getUserid() == user.getId() || user.getUserType()> u.getUserType()){
            if(commentService.removeById(id)){
                return Result.success("删除成功");
            }
            return Result.logicError("删除失败");
        }else{
            return Result.logicError("权限不足");
        }
    }
}
