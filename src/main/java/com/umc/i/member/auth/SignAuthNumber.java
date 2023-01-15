package com.umc.i.member.auth;

import lombok.Data;

import java.util.Date;

@Data
public class SignAuthNumber {
    private int authIdx;
    private int key;
    private String type;
    private Date createdAt;
}
