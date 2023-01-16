package com.umc.i.src.member;

import static com.umc.i.utils.ValidationRegex.isRegexEmail;
import static com.umc.i.utils.ValidationRegex.isRegexPhone;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.umc.i.src.member.model.post.PostJoinReq;
import com.umc.i.src.member.model.post.PostJoinRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponse;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.member.model.post.PostAuthReq;
import com.umc.i.src.member.model.post.PostAuthRes;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    @Autowired
    private final MemberService memberService;

    //이미지 업로드 미완
    /*
    @PostMapping("/file")
    public BaseResponse<List<String>> uploadFile(@RequestPart List<MultipartFile> multipartFile) {
        memberService.uploadFile(multipartFile);
        return null;
    }*/
    //회원가입
    @ResponseBody
    @PostMapping("/join/")
    public BaseResponse<PostJoinRes> createMem(@ModelAttribute PostJoinReq postJoinReq){
        try {
            memberService.uploadFile(postJoinReq.getProfileImg());
            PostJoinRes postJoinRes = memberService.createMem(postJoinReq);
            return new BaseResponse<>(postJoinRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

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
}