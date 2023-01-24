package com.umc.i.src.mypage.model.get;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetComentWriteRes {
    private int boarIdx;
    private int comuIdx;
    private String coment;
    private String feedTitle;
    private String writeNick;
    private String feedCreateAt;
    private int feedHit;

    public GetComentWriteRes(int boarIdx,int comuIdx,String coment,String writeNick,String feedCreateAt,int feedHit){
        this.boarIdx = boarIdx;
        this.comuIdx = comuIdx;
        this.coment = coment;
        this.writeNick = writeNick;
        this.feedCreateAt = feedCreateAt;
        this.feedHit = feedHit;
    }
}
