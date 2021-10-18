package top.changyix.blog.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
@Configuration
@ConfigurationProperties("properties")
@Getter
@Setter
public class MapConfig {
    List<String> ymlArray;
}
