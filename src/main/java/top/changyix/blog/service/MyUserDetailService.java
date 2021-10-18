package top.changyix.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import top.changyix.blog.entity.User;
import top.changyix.blog.handler.BasicHandler;

@Service
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    UserService userService;
    /*
    从数据库中查询相关的用户认证信息
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userService.getOne(new QueryWrapper<User>().eq("username",s));
        if(null == user){
            throw new BasicHandler(404,"用户不存在");
        }
        return user;
    }
}
