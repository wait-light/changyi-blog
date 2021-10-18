package top.changyix.blog.entity.controller_entity;

import lombok.Data;

import java.util.Date;

@Data
public class SaveArticle {
    public String title,content,pic;
    int type;
    int[] tags;
    boolean personal;
}
