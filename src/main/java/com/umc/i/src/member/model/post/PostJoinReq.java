package com.umc.i.src.member.model.post;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostJoinReq {
    private String email;
    private String pw;
    private String phone;
    private String nick;
    private String intro;
    private MultipartFile profileImg;
    private String birth;
    private String adresCode;
    private String adres;
    private String adresPlus;
}

