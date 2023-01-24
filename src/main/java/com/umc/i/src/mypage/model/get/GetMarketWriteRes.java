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
    private int goods;
    private int countReserve;


    //나눔장터 용
    public GetMarketWriteRes(int boarIdx,int comuIdx,String title,int soldout,int goods){
        this.boarIdx = boarIdx;
        this.comuIdx = comuIdx;
        this.title = title;
        this.soldout = soldout;
        this.goods = goods; // 파는 상품
    }
}
