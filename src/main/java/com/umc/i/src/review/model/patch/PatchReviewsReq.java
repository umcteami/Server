package com.umc.i.src.review.model.patch;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchReviewsReq {
    private int reviewIdx;      // 장터후기 인덱스
    private String goods;       // 구매 물품
    private String content;     // 내용
}
