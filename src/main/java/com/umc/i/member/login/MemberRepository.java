package com.umc.i.member;

import java.util.Optional;

public interface MemberRepository {

    Optional<Member> findByLoginEmail(String loginEmail);
}
