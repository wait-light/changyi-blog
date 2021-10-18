package top.changyix.blog.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class PureUser {
    public static final Integer 普通用户 = 0;
    public static final Integer 管理员 = 1;
    public static final Integer 超级管理员 = 2;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "头像base64数据")
    private String avatar;

    @ApiModelProperty(value = "注册时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "用户类型： 0、普通用户 （只能评论、留言、浏览文章） 1、管理员 （可以管理用户信息、管理文章、留言） 2、超级管理员 （拥有所有权限）")
    private Integer userType;

    @JsonIgnore
    public static PureUser conversion(User user) {
        PureUser pureUser = new PureUser();
        pureUser.setUsername(user.getUsername());
        pureUser.setUserType(user.getUserType());
        pureUser.setNickname(user.getNickname());
        pureUser.setId(user.getId());
        pureUser.setAvatar(user.getAvatar());
        pureUser.setCreateTime(user.getCreateTime());
        return pureUser;
    }
    @JsonIgnore
    public static PureUser conversionNoTitle(User user) {
        PureUser pureUser = new PureUser();
        pureUser.setUsername(user.getUsername());
        pureUser.setUserType(user.getUserType());
        pureUser.setNickname(user.getNickname());
        pureUser.setId(user.getId());
        pureUser.setCreateTime(user.getCreateTime());
        return pureUser;
    }
    @JsonIgnore
    public static List<PureUser> conversion(List<User> user) {
        if(user == null || user.size()==0){
            return null;
        }
        ArrayList<PureUser> pureUsers = new ArrayList<>();
        for (User u : user) {
            pureUsers.add(conversion(u));
        }
        return pureUsers;
    }
}
