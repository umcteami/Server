package com.umc.i.src.chat.model.get;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class GetChatRoomsRes { // 방 여러개 조회
    @NonNull
    private int roomIdx;
    @NonNull
    private int sender;
    @NonNull
    private String profile;
    @NonNull
    private String nick;
    private String recentChat;
    private String recentTime;
    private int noReadNum;
}
