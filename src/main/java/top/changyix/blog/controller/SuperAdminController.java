package top.changyix.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.changyix.blog.common_utils.Result;
import top.changyix.blog.entity.ArticleType;
import top.changyix.blog.entity.FriendshipChain;
import top.changyix.blog.entity.Map;
import top.changyix.blog.entity.Tag;
import top.changyix.blog.service.ArticleTypeService;
import top.changyix.blog.service.FriendshipChainService;
import top.changyix.blog.service.MapService;
import top.changyix.blog.service.TagService;

@RestController
@RequestMapping("/api/superAdmin")
@Secured({"ROLE_SUPERADMIN"})
public class SuperAdminController {
    @Autowired
    MapService mapService;
    @Autowired
    FriendshipChainService friendshipChainService;
    @Autowired
    TagService tagService;
    @Autowired
    ArticleTypeService articleTypeService;
    @RequestMapping("/setMap")
    public Result setMap(@RequestParam String key,@RequestParam String value){
        Map map = new Map();
        map.setKey(key);
        map.setValues(value);
        if(mapService.update(map,new UpdateWrapper<Map>().eq("`key`",key))){
            return Result.success("设置成功");
        }else {
            return Result.logicError("设置失败");
        }
    }
    @RequestMapping("/deleteFriend")
    public Result deleteFriend(@RequestParam int id){
        if(friendshipChainService.removeById(id)){
            return Result.success("删除成功");
        }else{
            return Result.logicError("删除失败");
        }
    }
    @RequestMapping("/modifyFriend")
    public Result modifyFriend(@RequestParam int id,
                               @RequestParam String title,
                               @RequestParam String url,
                               @RequestParam String content,
                               @RequestParam int order){
        FriendshipChain friendshipChain = friendshipChainService.getById(id);
        if(friendshipChain!=null){
            friendshipChain.setTitle(title);
            friendshipChain.setUrl(url);
            friendshipChain.setOrder(order);
            friendshipChain.setContent(content);
            if(friendshipChainService.saveOrUpdate(friendshipChain)){
                return Result.success("更新成功");
            }else{
                return Result.logicError("更新失败");
            }
        }else {
            return Result.logicError("无此友链接");
        }
    }
    @RequestMapping("/addFriend")
    public Result addFriend(@RequestParam String title,
                            @RequestParam String url,
                            @RequestParam String content,
                            @RequestParam int order){
        FriendshipChain friendshipChain = new FriendshipChain();
        friendshipChain.setContent(content);
        friendshipChain.setOrder(order);
        friendshipChain.setUrl(url);
        friendshipChain.setTitle(title);
        if(friendshipChainService.save(friendshipChain)){
            return Result.success("添加成功");
        }else{
            return Result.logicError("添加失败");
        }
    }



    @RequestMapping("/deleteTag")
    public Result deleteTag(@RequestParam int id) {
        return tagService.removeById(id) ? Result.success().setMessage("删除成功") : Result.error().setMessage("删除失败");
    }

    @RequestMapping("/deleteType")
    public Result deleteType(@RequestParam int id) {
        return articleTypeService.removeById(id) ? Result.success().setMessage("删除成功") : Result.error().setMessage("删除失败");
    }
}
