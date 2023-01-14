package com.umc.i.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @ResponseBody
    @RequestMapping("/member/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Member member) {
        Member loginMember = memberService.login(member.getEmail(), member.getPassword());

        if (loginMember == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("mem_idx", loginMember.getId());
        result.put("mem_email", loginMember.getEmail());
        result.put("mem_nickname", loginMember.getNickname());

        return new ResponseEntity<>(result,  HttpStatus.OK);
    }
}
