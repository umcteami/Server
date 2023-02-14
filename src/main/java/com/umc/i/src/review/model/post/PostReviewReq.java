package com.umc.i.src.review.model.post;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostReviewReq<MultipartFile> {
    private int sellerIdx;  // 파는 사람 인덱스
    private int buyerIdx;   // 사는 사람 인덱스
    private String goods;   // 구매한 물품
    private String content; // 내용
}