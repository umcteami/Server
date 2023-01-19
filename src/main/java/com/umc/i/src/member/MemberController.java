package com.umc.i.src.member;

import static com.umc.i.utils.ValidationRegex.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.umc.i.src.member.model.Member;
import com.umc.i.src.member.model.get.GetMemRes;
import com.umc.i.src.member.model.patch.PatchMemReq;
import com.umc.i.src.member.model.post.PostJoinReq;
import com.umc.i.src.member.model.post.PostJoinRes;
import com.umc.i.utils.ValidationRegex;
import lombok.Getter;
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

    //회원가입
    @ResponseBody
    @PostMapping("/join")
    public BaseResponse<String> createMem(@RequestPart("request") PostJoinReq postJoinReq,
                                           @RequestPart("profile") MultipartFile profile){
        try {
            String result = "";

            if (postJoinReq.getNick().length() > 10){
                result = "닉네임 길이 제한";
            } else if(ValidationRegex.isRegexNick(postJoinReq.getNick())) {
                result = "닉네임 특수문자 포함";
            } else if(postJoinReq.getIntro().length() > 50){
                result = "한줄소개 제한";
            } else if(postJoinReq.getPw().length() > 15 || postJoinReq.getPw().length() < 7){
                result = "비밀번호 길이 제한";
            } else if(ValidationRegex.isRegexPw(postJoinReq.getPw())){
                result = "비밀번호 형식 제한";
            }else{
                result = memberService.createMem(postJoinReq, profile);
            }

            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    //회원 정보 수정
    @ResponseBody
    @PatchMapping("/{memIdx}")
    public BaseResponse<String> editMem(@PathVariable("memIdx") int memIdx, @RequestPart("request") PatchMemReq patchMemReq,
                                        @RequestPart("profile") MultipartFile profile) {
        try {
            String result = "";

            if (patchMemReq.getNick().length() > 10){
                result = "닉네임 길이 제한";
            } else if(ValidationRegex.isRegexNick(patchMemReq.getNick())) {
                result = "닉네임 특수문자 포함";
            } else if(patchMemReq.getIntro().length() > 50){
                result = "한줄소개 제한";
            } else{
                result = memberService.editMem(memIdx,patchMemReq, profile);
            }

            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //비밀 번호 수정
    @ResponseBody
    @PatchMapping("/{memIdx}/pw")
    public BaseResponse<String> editPw(@PathVariable("memIdx") int memIdx,String pw) throws BaseException {
        try {
            String result = "";
            if(pw.length() > 15 || pw.length() < 7){
                result = "비밀번호 길이 제한";
            }else if(ValidationRegex.isRegexPw(pw)){
                result = "비밀번호 형식 제한";
            } else{
                result = memberService.editPw(memIdx,pw);
            }
            return new BaseResponse<>(result);
        }catch (BaseException e){
            return new BaseResponse<>((e.getStatus()));
        }
    }

    //유저 조회
    @ResponseBody
    @GetMapping("/{memIdx}")
    public BaseResponse<GetMemRes> getMem(@PathVariable("memIdx") int memIdx) throws BaseException{
        try {
            GetMemRes getMemRes = memberService.getMem(memIdx);
            return new BaseResponse<>(getMemRes);
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
                    if(postAuthRes == null) return new BaseResponse<>(BaseResponseStatus.POST_MEMBER_EXISTS_EMAIL);
                    return new BaseResponse<>(postAuthRes);
                } catch (BaseException e) {
                    return new BaseResponse<>(e.getStatus());
                }
            case 2: //핸드폰 인증
                if(postJoinAuthReq.getAuth() == null) return new BaseResponse<>(BaseResponseStatus.POST_MEMBER_EMPTY_PHONE);
                if(!isRegexPhone(postJoinAuthReq.getAuth())) return new BaseResponse<>(BaseResponseStatus.POST_MEMBER_INVALID_PHONE);
                try {
                    PostAuthRes postAuthRes = memberService.send_msg(postJoinAuthReq.getAuth());
                    if(postAuthRes == null) return new BaseResponse<>(BaseResponseStatus.POST_MEMBER_EXISTS_PHONE);
                    return new BaseResponse<>(postAuthRes);
                } catch (BaseException e) {
                    return new BaseResponse<>(e.getStatus());
                }
        }
        return new BaseResponse<>(BaseResponseStatus.POST_AUTH_INVALID_TYPE);
    }
}