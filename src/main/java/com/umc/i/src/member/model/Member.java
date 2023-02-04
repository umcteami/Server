package com.umc.i.src.member.model;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    private String email;
    private String phone;
    private String nickname;
}
