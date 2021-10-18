package top.changyix.blog.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import top.changyix.blog.handler.MyAuthenticationFailHandler;
import top.changyix.blog.handler.MyAuthenticationSuccessHandler;
import top.changyix.blog.handler.MyLogoutSuccessHandler;
import top.changyix.blog.service.MyUserDetailService;

import javax.sql.DataSource;


@Configuration
@EnableWebSecurity  //开启springSecurity配置
@EnableGlobalMethodSecurity(securedEnabled = true)//开启方法权限注解
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private MyUserDetailService myUserDetailService;
    //处理成功认证成功后返回的信息
    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
    //处理成功认证失败后返回的信息
    @Autowired
    private MyAuthenticationFailHandler myAuthenticationFailHandler;
    @Autowired
    private MyLogoutSuccessHandler logoutSuccessHandler;

    /*跨域原*/
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfigurationSource source =   new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");    //同源配置，*表示任何请求都视为同源，若需指定ip和端口可以改为如“localhost：8080”，多个以“，”分隔；
        corsConfiguration.addAllowedHeader("*");//header，允许哪些header，本案中使用的是token，此处可将*替换为token；
        corsConfiguration.addAllowedMethod("*");    //允许的请求方法，PSOT、GET等
        corsConfiguration.setAllowCredentials(true);

        ((UrlBasedCorsConfigurationSource) source).registerCorsConfiguration("/**",corsConfiguration); //配置允许跨域访问的url
        return source;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

                http
                .cors()
                .configurationSource(corsConfigurationSource())
                .and()
                .authorizeRequests() //处理权限
                .antMatchers("/api/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginProcessingUrl("/api/login")//登录表单提交地址
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(myAuthenticationSuccessHandler)//登录成功返回数据
                .failureHandler(myAuthenticationFailHandler)//登录失败返回数据
                .and()
                .logout()
                .logoutUrl("/api/logout")//退出登录提交表单地址
                .logoutSuccessHandler(logoutSuccessHandler)//退出登录成功处理返回数据
                .invalidateHttpSession(true)
                .and()
                .rememberMe()//开启记住我功能
                .tokenRepository(persistentTokenRepository())
                .and()
                .csrf()
                .disable();//关闭token认证;

        //(废除此方案)前后端分离，使用ajax json格式发送数据，因此要重写账号密码获取，使用Qs.stringify()发送的数据就不用重写了
//    .addFilterAt(new UserAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //使用自定义的UserDetailServicce
        auth.userDetailsService(myUserDetailService).passwordEncoder(new BCryptPasswordEncoder());
    }

    //配置token保存到数据库
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl persistentTokenRepository = new JdbcTokenRepositoryImpl();
        // 将 DataSource 设置到 PersistentTokenRepository
        persistentTokenRepository.setDataSource(dataSource);
        // 第一次启动的时候自动建表（可以不用这句话，自己手动建表，源码中有语句的）
        // persistentTokenRepository.setCreateTableOnStartup(true);
        return persistentTokenRepository;
    }
}
