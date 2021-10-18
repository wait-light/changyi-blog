package top.changyix.blog.init;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import top.changyix.blog.entity.Map;
import top.changyix.blog.service.MapService;

import java.util.List;

@Component
public class DataBaseInit implements ApplicationRunner {

    @Value("${config.maps}")
    List<String> maps;
    @Autowired
    MapService mapService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (String key : maps){
            List map = mapService.list(new QueryWrapper<Map>().eq("`key`",key));
            if(map.size()==0){
                Map m = new Map();
                m.setKey(key);
                mapService.save(m);
            }
        }
        mapService.list();
    }
}
