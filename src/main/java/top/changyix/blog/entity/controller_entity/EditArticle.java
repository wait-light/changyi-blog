package top.changyix.blog.entity.controller_entity;

import lombok.Data;

import java.util.Date;

@Data
public class EditArticle {
    public String title,content;
    int type;
    int[] tags;
    Date time;
    int id;
}
