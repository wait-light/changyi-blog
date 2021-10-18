package top.changyix.blog.config;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan({"top/changyix/blog/mapper","com.baomidou.mybatisplus.core.mapper"})
public class MapperConfiguration {
}
