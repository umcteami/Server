package com.umc.i.src.feeds.model.post;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostBlameReq {
    private int memIdx;
    private int boardIdx;
    private int comuIdx;
}
