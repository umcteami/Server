package com.umc.i.src.chat;

import com.umc.i.src.chat.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ChatController {
    @Autowired
    private final com.umc.i.src.chat.ChatRoomRepository chatRoomRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    //서버에서 보내는 메세지
    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        if (ChatMessage.MessageType.TALK.equals(message.getType())){
            chatRoomRepository.sendMsg(message);
        }
        if (ChatMessage.MessageType.ENTER.equals(message.getType()))
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomIdx(), message);
    }
}