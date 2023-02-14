package com.umc.i.src.chat.model.post;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostChatRoomOutReq {
    private int roomIdx;
    private int memIdx;
}
