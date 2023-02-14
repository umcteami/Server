package com.umc.i.src.chat.model.get;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetChatRoomRes { //방하나 조회
    private int sender;
    private String message;
    private List<String> chatImg;
    private String chatTime;
    private String senderProfile;
    private String senderNick;
    public GetChatRoomRes(int sender,String meesage,String chatTime,String senderProfile,String senderNick){
        this.sender = sender;
        this.message = meesage;
        this.chatTime = chatTime;
        this.senderProfile = senderProfile;
        this.senderNick = senderNick;
    }
    public GetChatRoomRes(int sender,String chatTime,String senderProfile,String senderNick){
        this.sender = sender;
        this.chatTime = chatTime;
        this.senderProfile = senderProfile;
        this.senderNick = senderNick;
    }
}
