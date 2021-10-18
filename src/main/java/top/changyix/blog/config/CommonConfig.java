package top.changyix.blog.config;

import org.springframework.beans.factory.annotation.Value;

public class CommonConfig {
    private CommonConfig(){};

    @Value("${pageSetting.pageSize}")
    public static int PageSize;

}
