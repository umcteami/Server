package com.umc.i.member.jwt;

import com.umc.i.member.login.Member;

import java.util.Optional;

public interface JwtRepository {
    public Optional<Member> findByLoginId(String refreshToken);

    public void deleteRefreshToken(String memIdx);

    public void insertRefreshToken(String memIdx, String refreshToken);
}
