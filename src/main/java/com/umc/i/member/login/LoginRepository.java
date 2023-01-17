package com.umc.i.member.login;

import java.util.Optional;

public interface LoginRepository {

    Optional<Member> findByLoginEmail(String loginEmail);
}
