package com.umc.i.member.auth;

import java.util.Optional;

public interface SignUpAuthRepository {

    Optional<SignAuthNumber> findByAuthIdx(int key);
}
