package com.umc.i.src.member.model.post;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostMemblockReq {
    private int memIdx;
    private int blockmemIdx;
}
