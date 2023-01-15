package com.umc.i.member.jwt;

import com.umc.i.member.login.Member;


public interface JwtService {
    public String createAccessToken(Member loginMember);

    public String createRefreshToken(Member loginMember);

    public void updateRefreshToken(String memIdx, String refreshToken);

    public Boolean isValidToken(String token);

    public Boolean isExpiredToken(String token);

    public String getMemberEmail(String token);

    public String getMemberId(String token);
}
