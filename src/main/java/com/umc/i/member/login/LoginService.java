package com.umc.i.member.login;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final JdbcTemplateLoginRepository loginRepository;

    public Member login(String loginEmail, String password) {
        return loginRepository.findByLoginEmail(loginEmail)
                .filter(m -> m.getPassword().equals(passwordCrypto(password)))
                .orElse(null);
    }

    public String passwordCrypto(String password) {
        return UserSha256.encrypt(password);
    }
}
