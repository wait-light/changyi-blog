package top.changyix.blog.service.impl;

import top.changyix.blog.entity.Article;
import top.changyix.blog.mapper.ArticleMapper;
import top.changyix.blog.service.ArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 徜逸
 * @since 2020-08-11
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

}
