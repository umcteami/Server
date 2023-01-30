package com.umc.i.src.chat;

import com.umc.i.src.chat.model.get.GetChatRoomRes;
import com.umc.i.src.chat.model.get.GetChatRoomsRes;
import com.umc.i.src.chat.model.post.PostChatRoom;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {
    @Autowired
    private final com.umc.i.src.chat.ChatRoomRepository chatRoomRepository;

    // 채팅 리스트 화면
    @GetMapping("/room")
    public String rooms(Model model) {
        return "room";
    }
    // 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    @ResponseBody
    public List<GetChatRoomsRes> room() {
        return chatRoomRepository.findAllRoom();
    }
    // 채팅방 생성
    @PostMapping("/room")
    @ResponseBody
    public PostChatRoom createRoom(@ModelAttribute PostChatRoom postChatRoom) {
        return chatRoomRepository.createChatRoom(postChatRoom);
    }
    // 채팅방 입장 화면
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "roomdetail";
    }
    // 특정 채팅방 조회
    @GetMapping("/room/{roomIdx}")
    @ResponseBody
    public GetChatRoomRes roomInfo(@PathVariable int roomIdx) {
        return chatRoomRepository.getChatRoomIdx(roomIdx);
    }
}
