package com.umc.i.src.chat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {
    public enum MessageType{
        ENTER,TALK
    }
    private MessageType type;
    private int roomIdx;
    private int sender;
    private String message;

    public ChatMessage(int roomId,int sender,String message){
        this.roomIdx = roomId;
        this.sender = sender;
        this.message = message;
    }
}
