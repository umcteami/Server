package com.umc.i.src.chat;

import com.umc.i.src.chat.model.ChatMessage;
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

    public List<GetChatRoomsRes> findAllRoom() {
        return chatDao.getChatRooms();
    }

    public PostChatRoom findRoomById(String id) {
        return chatRoomMap.get(id);
    }

    public PostChatRoom createChatRoom(PostChatRoom postChatRoom) {
        chatDao.postChatRoom(postChatRoom);
        return postChatRoom;
    }
    public void sendMsg(ChatMessage chatMessage){
        chatDao.sendMsg(chatMessage);
    }
}
