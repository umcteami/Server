package com.umc.i.src.member.join.auth;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.umc.i.src.member.join.model.PostJoinReq;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/member/join/auth")
@RequiredArgsConstructor
public class AuthController {
    private final MailAuthService mailService;
    private final PhoneAuthService phoneService;
    private final AuthDao authDao;

    @ResponseBody
    @PostMapping("")
    public String checkType(@RequestBody PostJoinReq postJoinReq) throws MessagingException, UnsupportedEncodingException {
        switch(postJoinReq.getType()) {
            case 1: 
                return authDao.createAuth(postJoinReq, mailConfirm(postJoinReq.getAuth()));
            case 2: 
                return authDao.createAuth(postJoinReq, phoneConfirm(postJoinReq.getAuth()));
        }
        return "type error";
    }

    public String mailConfirm(String email) throws MessagingException, UnsupportedEncodingException {
        String authCode = mailService.sendEmail(email);
        return authCode;
    }

    public String phoneConfirm(String tel) {
        String authCode = phoneService.send_msg(tel);
        return authCode;
    }
}