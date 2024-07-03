package org.cs304.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Reply", description = "")
public class Reply {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer commentId;

    private String publisherId;

    private LocalDateTime publishDate;

    private String content;

    private Integer upVote;

    private Integer downVote;
}
