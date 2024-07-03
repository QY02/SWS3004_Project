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
@Schema(name = "Comment", description = "")
public class Comment {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer eventId;

    private String publisherId;

    private LocalDateTime publishDate;

    private String content;

    private Integer upVote;

    private Integer downVote;

    private Double score;//活动评论的评星

    private Integer type;

    private String title;

    private Boolean mediaType;//false: pic true: video
}
