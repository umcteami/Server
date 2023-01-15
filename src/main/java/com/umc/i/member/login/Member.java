package com.umc.i.member.login;

import lombok.Data;

@Data
public class Member {
    private Long id;
    private String email;
    private String password;
    private String nickname;
}
