package com.umc.i.member.login;

import com.umc.i.member.jwt.JwtService;
import com.umc.i.member.login.model.PostLoginRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    private final JwtService jwtService;

    @PostMapping(value = "/login")
    public ResponseEntity<PostLoginRes> login(@RequestHeader("Auth-Refresh") String existRefreshToken,
                                              @RequestBody Member member) {
        /**
         * jwt refreshToken 이 전달된 경우 확인
         * jwtService isValid, isExpire 후에 모두 true면
         * uid, email 추출해서 밑에 새로운 토큰 발행 후 전달
         */
//        String existRefreshToken = header.get("Auth-Refresh");
        log.info("existRefreshToken={} {}", existRefreshToken, existRefreshToken == null);
        if (existRefreshToken.length() > 0) {
            if (!(jwtService.isExpiredToken(existRefreshToken) && jwtService.isValidToken(existRefreshToken))) {
                PostLoginRes res = new PostLoginRes(false);
                return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
            }
            else {
                PostLoginRes res = new PostLoginRes();
                Member loginMember = new Member();
                res.setId(Long.valueOf(jwtService.getMemberId(existRefreshToken)));
                res.setEmail(jwtService.getMemberEmail(existRefreshToken));
                loginMember.setEmail(res.getEmail());
                loginMember.setId(res.getId());
                res.setAccessToken(jwtService.createAccessToken(loginMember));
                res.setRefreshToken(jwtService.createRefreshToken(loginMember));
                res.setSuccess(true);
                jwtService.updateRefreshToken(String.valueOf(loginMember.getId()), res.getRefreshToken());
                return new ResponseEntity<>(res, HttpStatus.OK);
            }
        }

        Member loginMember = memberService.login(member.getEmail(), member.getPassword());

        if (loginMember == null) {
            return ResponseEntity.notFound().build();
        }

        String accessToken = jwtService.createAccessToken(loginMember);
        String refreshToken = jwtService.createRefreshToken(loginMember);

        jwtService.updateRefreshToken(String.valueOf(loginMember.getId()), refreshToken);

        PostLoginRes res = new PostLoginRes(loginMember.getId(), loginMember.getEmail(), loginMember.getNickname(), accessToken, refreshToken, true);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request) {
        return null;
    }
}
