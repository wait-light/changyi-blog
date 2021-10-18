package top.changyix.blog.domain;

import org.springframework.security.core.GrantedAuthority;

public class RoleUser implements GrantedAuthority {
    @Override
    public String getAuthority() {
        return "ROLE_USER";
    }
}
