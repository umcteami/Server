package com.umc.i.src.chat;

import com.umc.i.src.chat.model.ChatMessage;
import com.umc.i.src.chat.model.get.GetChatRoomRes;
import com.umc.i.src.chat.model.get.GetChatRoomsRes;
import com.umc.i.src.chat.model.post.PostChatRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;

@Repository
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

    public List<GetChatRoomRes> getChatRoomIdx (int id) {
        return chatDao.getChatRoomIdx(id);
    }

    public PostChatRoom createChatRoom(PostChatRoom postChatRoom) {
        chatDao.postChatRoom(postChatRoom);
        return postChatRoom;
    }
    public void sendMsg(ChatMessage chatMessage){
        chatDao.sendMsg(chatMessage);
    }
}
