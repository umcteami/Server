package com.umc.i.src.member;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.umc.i.src.member.model.post.PostJoinAuthReq;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/member/join/auth")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final MemberDao memberDao;

    @ResponseBody
    @PostMapping("")
    public String checkType(@RequestBody PostJoinAuthReq postJoinAuthReq) throws MessagingException, UnsupportedEncodingException {
        switch(postJoinAuthReq.getType()) {
            case 1: //메일 인증
                if(memberDao.checkEmail(postJoinAuthReq.getAuth()) == 1) return "이미 가입한 회원입니다.";
                else return memberDao.createAuth(postJoinAuthReq, mailConfirm(postJoinAuthReq.getAuth()));
            case 2: //핸드폰 인증
                if(memberDao.checkPhone(postJoinAuthReq.getAuth()) == 1) return "이미 가입한 회원입니다.";
                else return memberDao.createAuth(postJoinAuthReq, phoneConfirm(postJoinAuthReq.getAuth()));
        }
        return "type error";
    }

    public String mailConfirm(String email) throws MessagingException, UnsupportedEncodingException {
        String authCode = memberService.sendEmail(email);
        return authCode;
    }

    public String phoneConfirm(String tel) {
        String authCode = memberService.send_msg(tel);
        return authCode;
    }
}