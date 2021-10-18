package top.changyix.blog.domain;

import org.springframework.security.core.GrantedAuthority;

public class RoleAdmin implements GrantedAuthority {
    @Override
    public String getAuthority() {
        return "ROLE_ADMIN";
    }
}
