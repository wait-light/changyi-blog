package top.changyix.blog.service.impl;

import top.changyix.blog.entity.Comment;
import top.changyix.blog.mapper.CommentMapper;
import top.changyix.blog.service.CommentService;
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
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

}
