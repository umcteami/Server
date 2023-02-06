package com.umc.i.src.mypage.model.get;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMarketWriteRes {
    private int boarIdx;
    private int comuIdx;
    private String title; //boarIdx 가 장터후기면 필터링
    private String feedImg;
    private int soldout;
    private int countReserve;
}
