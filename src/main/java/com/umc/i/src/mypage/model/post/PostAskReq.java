package com.umc.i.src.mypage.model.post;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostAskReq {
    private String title;
    private String content;
    private String email;
}
