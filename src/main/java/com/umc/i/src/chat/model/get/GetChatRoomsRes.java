package com.umc.i.src.chat.model.get;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetChatRoomsRes { // 방 여러개 조회
    private int roomIdx;
    private int memIdx1;
    private int memIdx2;
}
