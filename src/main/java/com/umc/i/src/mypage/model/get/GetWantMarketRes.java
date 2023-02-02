package com.umc.i.src.mypage.model.get;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetWantMarketRes {
    private int boardIdx;
    private int comuIdx;
    private String image;
    private int price;
    private String title;
    private String createAt;
    private int hits;
    private int wantCount;
    private int soldout;

    public GetWantMarketRes(int boardIdx,int comuIdx,String image,int price,String title,String createAt,int hits,int soldout){
        this.boardIdx = boardIdx;
        this.comuIdx = comuIdx;
        this.image = image;
        this.price = price;
        this.title = title;
        this.createAt = createAt;
        this.hits = hits;
        this.soldout = soldout;
    }
}
