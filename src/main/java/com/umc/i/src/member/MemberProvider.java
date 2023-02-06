package com.umc.i.src.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.umc.i.config.BaseException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberProvider {
    @Autowired
    private final MemberDao memberDao;

    // 이메일 찾기
    public String findEmail(String phone) throws BaseException {
        try {
            return memberDao.findEmail(phone);
        } catch (BaseException e) {
            throw e;
        }
    }
}
