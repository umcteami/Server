package com.umc.i.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final JdbcTemplateMemberRepository memberRepository;

    public Member login(String loginEmail, String password) {
        return memberRepository.findByLoginEmail(loginEmail)
                .filter(m -> m.getPassword().equals(passwordCrypto(password)))
                .orElse(null);
    }

    public String passwordCrypto(String password) {
        System.out.println("UserSha256.encrypt(password) = " + UserSha256.encrypt(password));
        return UserSha256.encrypt(password);
    }
}
