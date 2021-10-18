package top.changyix.blog.schedule;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.changyix.blog.common_utils.BitUtils;
import top.changyix.blog.entity.Article;
import top.changyix.blog.service.ArticleService;
import java.util.Date;
import java.util.List;

@Component
@EnableScheduling //开启定时任务
public class ScheduleHandle {
    @Autowired
    ArticleService articleService;
    //每5分钟检查一次
    @Scheduled(cron = "0 0/5 * * * ?")
    private void publishArticle(){
        //已经可以发布的文章
        List<Article> articles = articleService
                .list(
                        new QueryWrapper<Article>()
                                .eq("status&1",0)
                                .le("publish_time",new Date()));
        for (Article a:articles) {
            a.setStatus(BitUtils.or(a.getStatus(),Article.STATE_PUBLISHED));//发布
            articleService.saveOrUpdate(a);
        }
    }
}
