package com.umc.i.member.jwt;

import lombok.Data;

import java.time.Instant;

@Data
public class Jwt {

    public static String SECRET_KEY = "ThisIsIProjectJwtPrivateKey";

    private String uid;
    private String email;
}
