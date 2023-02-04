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

}
