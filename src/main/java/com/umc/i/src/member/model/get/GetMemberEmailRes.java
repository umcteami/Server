package com.umc.i.src.member.model.get;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMemberEmailRes {
    private int memIdx;     // 유저 인덱스
    private String email;   // 
    
}
