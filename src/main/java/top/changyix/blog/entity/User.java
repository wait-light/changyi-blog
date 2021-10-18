package top.changyix.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import top.changyix.blog.domain.RoleAdmin;
import top.changyix.blog.domain.RoleSuperAdmin;
import top.changyix.blog.domain.RoleUser;

/**
 * <p>
 * 
 * </p>
 *
 * @author 徜逸
 * @since 2020-08-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="User对象", description="")
public class User implements Serializable, UserDetails {

    private static final long serialVersionUID=1L;

    public static final Integer 普通用户 = 0;
    public static final Integer 管理员 = 1;
    public static final Integer 超级管理员 = 2;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "头像base64数据")
    private String avatar;

    @ApiModelProperty(value = "是否启用")
    private String locked;

    @ApiModelProperty(value = "注册时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "用户类型： 0、普通用户 （只能评论、留言、浏览文章） 1、管理员 （可以管理用户信息、管理文章、留言） 2、超级管理员 （拥有所有权限）")
    private Integer userType;

    @ApiModelProperty(value = "用户设置")
    private String setting;

    @JsonIgnore
    @TableField(exist = false)
    private List<? extends GrantedAuthority> authorities;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        switch (userType){
            case 0:grantedAuthorities.add(new RoleUser());break;
            case 1:grantedAuthorities.add(new RoleAdmin());break;
            case 2:grantedAuthorities.add(new RoleSuperAdmin());break;
        }
        return grantedAuthorities;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return "F".equals(locked);
    }
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return "F".equals(locked);
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return "F".equals(locked);
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return "F".equals(locked);
    }
}
