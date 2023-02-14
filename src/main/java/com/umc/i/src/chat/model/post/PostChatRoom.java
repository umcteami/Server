package com.umc.i.src.chat.model.post;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostChatRoom {
    private int roomIdx;
    @NonNull
    private int memIdx1;
    @NonNull
    private int memIdx2;
}
