package top.changyix.blog.entity.controller_entity;

import lombok.Data;

@Data
public class MakeComment {
    String content;
    int article_id,parent_id,floor_id;
}
