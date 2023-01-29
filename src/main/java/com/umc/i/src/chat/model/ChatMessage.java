package com.umc.i.src.chat.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
    public enum MessageType{
        ENTER,TALK
    }
    private MessageType type;
    private int roomId;
    private int sender;
    private String message;
}
