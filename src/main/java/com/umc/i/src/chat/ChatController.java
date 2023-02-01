package com.umc.i.src.chat;

import com.umc.i.src.chat.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ChatController {

    private final com.umc.i.src.chat.ChatRoomRepository chatRoomRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    /*@EventListener
    public void handleWebSocketConnectListener(SessionDisconnectEvent event){
        log.info("{}","나감");
        log.info("{}",event);
    }*/
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