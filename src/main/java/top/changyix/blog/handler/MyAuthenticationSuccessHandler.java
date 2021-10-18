package top.changyix.blog.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import top.changyix.blog.common_utils.Result;
import top.changyix.blog.entity.PureUser;
import top.changyix.blog.entity.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("MyAuthenticationSuccessHandler")
public class MyAuthenticationSuccessHandler  extends SimpleUrlAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        ObjectMapper objectMapper = new ObjectMapper();
        Result result = Result.success().setMessage("登录成功").append("user",PureUser.conversion((User) authentication.getPrincipal()));
        response.getWriter().append(objectMapper.writeValueAsString(result));
    }
}
