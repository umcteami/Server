package com.umc.i.src.mypage.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Blame {

    private int boardIdx;
    private int comuIdx;
    private String createAt;
}
