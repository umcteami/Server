package com.umc.i.src.member.model.post;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostWithdrawReq {
    private int memIdx;
}
