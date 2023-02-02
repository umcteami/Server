package com.umc.i.src.mypage.model.get;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetBlameFeedRes {
    private int roomType;
    private String profile;
    private String nick;
    private String createAt;
}
