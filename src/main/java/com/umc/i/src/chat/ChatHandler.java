package com.umc.i.src.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    //댓글 보내면 서버에서 채팅 보냄
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage msg)throws Exception{
        String payload = msg.getPayload();
        log.info("payload {}", payload);
        //TextMessage textMessage = new TextMessage("open server");
        //session.sendMessage(textMessage);
        ChatMessage chatMessage = objectMapper.readValue(payload,ChatMessage.class);
        ChatRoom room = chatService.findRoomById(chatMessage.getRoomId());
        room.handleActions(session,chatMessage,chatService);
    }
}
