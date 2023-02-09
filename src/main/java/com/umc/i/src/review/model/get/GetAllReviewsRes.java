package com.umc.i.src.review.model.get;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetAllReviewsRes {
    private int reviewIdx;      // 장터 후기 인덱스
        private int buyerIdx;       // 구매자 인덱스
        private int sellerIdx;          // 판매자 인덱스
        private String buyerNick;       // 구매자 닉네임
        private String sellerNick;      // 판매자 닉네임
        private String goods;       // 구매 물품
        private int hit;        // 조회수
        private String createAt;      // 작성일
        private String image;       // 대표 이미지
}


