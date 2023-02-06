package com.umc.i.src.chat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {
    public enum MessageType{
        ENTER,TALK,LEAVE
    }
    private MessageType type;
    private int roomIdx;
    private int sender;
    private String message;
}
