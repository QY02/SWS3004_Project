package org.cs304.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String content;

    private String senderId;

    private String receiverId;

    private LocalDateTime sendTime;

    private Boolean hasRead;

}