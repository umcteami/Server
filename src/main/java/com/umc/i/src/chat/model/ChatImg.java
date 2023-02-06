package com.umc.i.src.chat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChatImg {
    public enum MessageType{
       TALK
    }
    private MessageType type;
    private int roomIdx;
    private int sender;
    private List<String> files;
    public ChatImg(int roomId,int sender,List<String> files){
        this.roomIdx = roomId;
        this.sender = sender;
        this.files = files;
    }
}
