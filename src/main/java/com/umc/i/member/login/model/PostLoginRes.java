package com.umc.i.member.login.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostLoginRes {
    private Long id;
    private String email;
    private String nickname;
    private String accessToken;
    private String refreshToken;
    private boolean isSuccess;

    public PostLoginRes(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}
