package top.changyix.blog.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicHandler extends RuntimeException  {
    private int code;
    private String message;
}
