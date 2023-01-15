package com.umc.i.member.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostAuthNumberReq {
    private int type;
    private int authIdx;
    private int authNumber;
}
