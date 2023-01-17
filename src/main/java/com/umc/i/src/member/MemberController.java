package com.umc.i.src.member;

import static com.umc.i.utils.ValidationRegex.isRegexEmail;
import static com.umc.i.utils.ValidationRegex.isRegexPhone;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import com.umc.i.src.member.model.post.PostAuthNumberReq;
import com.umc.i.src.member.model.post.PostAuthNumberRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponse;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.member.model.post.PostAuthReq;
import com.umc.i.src.member.model.post.PostAuthRes;

import lombok.RequiredArgsConstructor;

@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    @Autowired
    private final MemberService memberService;

    @ResponseBody
    @PostMapping("/join/auth")  
    // 본인인증
    public BaseResponse<PostAuthRes> checkType(@RequestBody PostAuthReq postJoinAuthReq) throws MessagingException, UnsupportedEncodingException {
        switch(postJoinAuthReq.getType()) {
            case 1: //메일 인증
                if(postJoinAuthReq.getAuth() == null) return new BaseResponse<>(BaseResponseStatus.POST_MEMBER_EMPTY_EMAIL);
                if(!isRegexEmail(postJoinAuthReq.getAuth())) return new BaseResponse<>(BaseResponseStatus.POST_MEMBER_INVALID_EMAIL);
                try {
                    PostAuthRes postAuthRes = memberService.sendEmail(postJoinAuthReq.getAuth());
                    return new BaseResponse<>(postAuthRes);
                } catch (BaseException e) {
                    return new BaseResponse<>(e.getStatus());
                }
            case 2: //핸드폰 인증
                if(postJoinAuthReq.getAuth() == null) return new BaseResponse<>(BaseResponseStatus.POST_MEMBER_EMPTY_PHONE);
                if(!isRegexPhone(postJoinAuthReq.getAuth())) return new BaseResponse<>(BaseResponseStatus.POST_MEMBER_INVALID_PHONE);
                try {
                    PostAuthRes postAuthRes = memberService.send_msg(postJoinAuthReq.getAuth());
                    return new BaseResponse<>(postAuthRes);
                } catch (BaseException e) {
                    return new BaseResponse<>(e.getStatus());
                }
        }
        return new BaseResponse<>(BaseResponseStatus.POST_AUTH_INVALID_TYPE);
    }

    @GetMapping("/join/auth")
    public BaseResponse<PostAuthNumberRes> checkAuthNumber(@RequestBody PostAuthNumberReq postAuthNumberReq) {
        int authIdx = postAuthNumberReq.getAuthIdx();

        PostAuthNumberReq res = memberService.getSignAuthNumberObject(authIdx);
        log.info("res={}, {}", res, res.getAuthNumber());
        if (res == null) {
            return new BaseResponse<>(BaseResponseStatus.POST_NUMBER_AUTH_FAILED);
        }

        if (memberService.isExpired(res)) {
            PostAuthNumberRes postAuthNumberRes = new PostAuthNumberRes(res.getAuthNumber());
            return new BaseResponse<>(postAuthNumberRes);
        }

        return new BaseResponse<>(BaseResponseStatus.POST_NUMBER_AUTH_TIME_FAILED);
    }
}