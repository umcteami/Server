package com.umc.i.member.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostAuthNumberReq {
    private int type;
    private int authIdx;
    private int authNumber;
}
