package com.umc.i.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @ResponseBody
    @RequestMapping("/member/login")
    public String login(@RequestBody Member member) {
        log.info("requestEmail={}", member.getEmail());
        Member loginMember = memberService.login(member.getEmail(), member.getPassword());

        log.info("loginMember={}", loginMember);
        if (loginMember == null) {
            return "false";
        }

        return "true";
    }
}
