package com.umc.i.member.login;

import java.util.Optional;

public interface MemberRepository {

    Optional<Member> findByLoginEmail(String loginEmail);
}
