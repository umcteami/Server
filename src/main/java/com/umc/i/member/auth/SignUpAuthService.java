package com.umc.i.member.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignUpAuthService {

    private final JdbcTemplateSignUpRepository signUpRepository;

    public SignAuthNumber getSignAuthNumberObject(int authIdx) {
        return signUpRepository.findByAuthIdx(authIdx)
                .filter(o -> o.getAuthIdx() == authIdx)
                .orElse(null);
    }

    public boolean isEqual(SignAuthNumber signAuthNumber, int maKey) {
        int key = signAuthNumber.getKey();

        return key == maKey;
    }

    public boolean isExpired(SignAuthNumber signAuthNumber) {
        Date createdAt = signAuthNumber.getCreatedAt();
        Date date = new Date();

        long diffSec = (date.getTime() - createdAt.getTime()) / (1000);
        log.info("sec={}", diffSec);
        return diffSec <= 600;
    }
}
