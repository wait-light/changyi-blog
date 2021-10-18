package top.changyix.blog.service.impl;

import top.changyix.blog.entity.Tag;
import top.changyix.blog.mapper.TagMapper;
import top.changyix.blog.service.TagService;
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
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

}
