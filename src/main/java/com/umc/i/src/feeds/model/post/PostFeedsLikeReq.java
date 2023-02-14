package com.umc.i.src.feeds.model.post;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostFeedsLikeReq {
    private int memIdx;         // 사용자 인덱스
    private int boardType;      // 1: 이야기방, 2: 일기장, 3: 장터후기
    private int feedIdx;        // 게시글인덱스
}
