package com.umc.i.src.chat;

import com.amazonaws.services.s3.AmazonS3;
import com.umc.i.src.chat.model.ChatImg;
import com.umc.i.src.chat.model.ChatMessage;
import com.umc.i.utils.S3Storage.UploadImageS3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Controller
@Slf4j
public class ChatController {

    private final com.umc.i.src.chat.ChatRoomRepository chatRoomRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    @Autowired
    private final UploadImageS3 uploadImageS3;

    private final AmazonS3 amazonS3;

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

    //이미지 전송-unchecked
    @MessageMapping("/chat/img")
    public void sendImg(ChatImg img, List<MultipartFile> files) throws IOException {

        if (ChatImg.MessageType.TALK.equals(img.getType())){
            List<String> filePath = new ArrayList<>();
            String saveFilePath = null;

            for(MultipartFile file : files){
                if(!file.getOriginalFilename().equals("basic.jpg")) {  //기본 프로필이 아니면 + 기본 프로필 사진 이름으로 변경하기
                    String fileName = "image" + File.separator + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));

                    // 저장할 새 이름
                    long time = System.currentTimeMillis();
                    String originalFilename = file.getOriginalFilename();
                    String saveFileName = String.format("%d_%s", time, originalFilename.replaceAll(" ", ""));

                    // 이미지 업로드
                    saveFilePath = File.separator + uploadImageS3.upload(file, fileName, saveFileName);
                }
                filePath.add(saveFilePath);
            }
            img.setFiles(filePath);
            chatRoomRepository.sendImg(img);
        }

        messagingTemplate.convertAndSend("/sub/chat/room/" + img.getRoomIdx(), img);
    }
}