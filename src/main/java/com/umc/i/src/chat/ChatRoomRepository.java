package com.umc.i.src.chat;

import com.umc.i.src.chat.model.ChatImg;
import com.umc.i.src.chat.model.ChatMessage;
import com.umc.i.src.chat.model.get.GetChatRoomRes;
import com.umc.i.src.chat.model.get.GetChatRoomsRes;
import com.umc.i.src.chat.model.post.PostChatRoom;
import com.umc.i.src.chat.model.post.PostChatRoomOutReq;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepository {


    private Map<String, PostChatRoom> chatRoomMap;
    @Autowired
    private ChatDao chatDao;

    @PostConstruct
    private void init() {
        chatRoomMap = new LinkedHashMap<>();
    }

    public List<GetChatRoomsRes> findAllRoom(int memIdx) {
        return chatDao.getChatRooms(memIdx);
    }

    public List<GetChatRoomRes> getChatRoomIdx (int roomIdx,int memIdx) {
        return chatDao.getChatRoomIdx(roomIdx,memIdx);
    }

    public PostChatRoom createChatRoom(PostChatRoom postChatRoom) {
        chatDao.postChatRoom(postChatRoom);
        return postChatRoom;
    }
    //채팅 text 저장
    public void sendMsg(ChatMessage chatMessage){
        chatDao.sendMsg(chatMessage);
    }
    //채팅 이미지 저장
    public void sendImg(ChatImg img) { chatDao.sendImg(img);}
    //채팅 나감 과 동시에 읽음여부 db에 저장
    public void postRoomOut(PostChatRoomOutReq roomOut){ chatDao.postRoomOut(roomOut);}
    //채팅방 삭제
    public void delChatRoom(PostChatRoomOutReq roomOut){chatDao.delChatRoom(roomOut);}
}
