package com.umc.i.src.member;

import static com.umc.i.utils.ValidationRegex.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import com.umc.i.src.member.model.get.GetMemRes;
import com.umc.i.src.member.model.get.GetMemberEmailReq;
import com.umc.i.src.member.model.patch.PatchMemReq;
import com.umc.i.src.member.model.post.*;
import com.umc.i.utils.ValidationRegex;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponse;
import com.umc.i.config.BaseResponseStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    @Autowired
    private final MemberService memberService;
    @Autowired
    private final MemberProvider memberProvider;

    //회원가입-clear
    @ResponseBody
    @PostMapping("/join")
    public BaseResponse<BaseResponseStatus> createMem(@RequestPart("request") PostJoinReq postJoinReq,
                                           @RequestPart("profile") MultipartFile profile){
        try {
            BaseResponseStatus baseResponseStatus = null;

            if (postJoinReq.getNick().length() > 10){
                baseResponseStatus = BaseResponseStatus.POST_MEMBER_JOIN_NICKLEN;
            } else if(ValidationRegex.isRegexNick(postJoinReq.getNick())) {
                baseResponseStatus = BaseResponseStatus.POST_MEMBER_ISREGEX_NICK;
            } else if(postJoinReq.getIntro().length() > 50){
                baseResponseStatus = BaseResponseStatus.POST_MEMBER_JOIN_INTROLEN;
            } else if(postJoinReq.getPw().length() > 15 || postJoinReq.getPw().length() < 7){
                baseResponseStatus = BaseResponseStatus.POST_MEMBER_JOIN_PWLEN;
            } else if(ValidationRegex.isRegexPw(postJoinReq.getPw())){
                baseResponseStatus = BaseResponseStatus.POST_MEMBER_ISREGEX_PW;
            }else{
                baseResponseStatus = memberService.createMem(postJoinReq, profile);
            }

            return new BaseResponse<>(baseResponseStatus);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    //회원 정보 수정-clear
    @ResponseBody
    @PatchMapping("/{memIdx}")
    public BaseResponse<BaseResponseStatus> editMem(@PathVariable("memIdx") int memIdx, @RequestPart("request") PatchMemReq patchMemReq,
                                        @RequestPart("profile") MultipartFile profile) {
        try {
            BaseResponseStatus baseResponseStatus = null;

            if (patchMemReq.getNick().length() > 10){
                baseResponseStatus = BaseResponseStatus.POST_MEMBER_JOIN_NICKLEN;
            } else if(ValidationRegex.isRegexNick(patchMemReq.getNick())) {
                baseResponseStatus = BaseResponseStatus.POST_MEMBER_ISREGEX_NICK;
            } else if(patchMemReq.getIntro().length() > 50){
                baseResponseStatus = BaseResponseStatus.POST_MEMBER_JOIN_INTROLEN;
            } else{
                baseResponseStatus = memberService.editMem(memIdx,patchMemReq, profile);
            }

            return new BaseResponse<>(baseResponseStatus);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //비밀 번호 수정-clear
    @ResponseBody
    @PatchMapping("/{memIdx}/pw")
    public BaseResponse<BaseResponseStatus> editPw(@PathVariable("memIdx") int memIdx,String pw) throws BaseException {
        try {
            BaseResponseStatus baseResponseStatus = null;
            if(pw.length() > 15 || pw.length() < 7){
                baseResponseStatus = BaseResponseStatus.POST_MEMBER_JOIN_PWLEN;
            }else if(ValidationRegex.isRegexPw(pw)){
                baseResponseStatus = BaseResponseStatus.POST_MEMBER_ISREGEX_PW;
            } else{
                baseResponseStatus = memberService.editPw(memIdx,pw);
            }
            return new BaseResponse<>(baseResponseStatus);
        }catch (BaseException e){
            return new BaseResponse<>((e.getStatus()));
        }
    }
    //유저 조회-clear
    @ResponseBody
    @GetMapping("/{memIdx}")
    public BaseResponse<GetMemRes> getMem(@PathVariable("memIdx") int memIdx){
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


    @GetMapping("/join/auth")
    public BaseResponse<PostAuthNumberRes> checkAuthNumber(@RequestBody PostAuthNumberReq postAuthNumberReq) {
        int authIdx = postAuthNumberReq.getAuthIdx();

        PostAuthNumberReq res = memberService.getSignAuthNumberObject(authIdx);

        if (res == null) {
            return new BaseResponse<>(BaseResponseStatus.POST_NUMBER_AUTH_FAILED);
        }

        if (memberService.isExpired(res)) {
            PostAuthNumberRes postAuthNumberRes = new PostAuthNumberRes(res.getAuthNumber());
            return new BaseResponse<>(postAuthNumberRes);
        }

        return new BaseResponse<>(BaseResponseStatus.POST_NUMBER_AUTH_TIME_FAILED);
    }

    //유저 차단-clear
    @ResponseBody
    @PostMapping("/withdraw")
    public BaseResponse<BaseException> postWithdraw(@RequestPart("memIdx") int memIdx) throws BaseException {
        try {
            memberService.postWithdraw(memIdx);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        }catch (BaseException e){
            return new BaseResponse<>(BaseResponseStatus.POST_MEMBER_WITHDRAW);
        } catch (Exception e){
            return new BaseResponse<>(BaseResponseStatus.INTERNET_ERROR);
        }
    }
    //유저 탈퇴 - clear
    @ResponseBody
    @PostMapping("/block")
    public BaseResponse<BaseException> postBlock(@RequestBody PostMemblockReq postMemblockReq)throws BaseException{
        try {
            memberService.postMemblock(postMemblockReq);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        }catch (BaseException e){
            return new BaseResponse<>(BaseResponseStatus.POST_NEMBER_BLOCK_DOUBLE);
        }catch (Exception e){
            return new BaseResponse<>(BaseResponseStatus.INTERNET_ERROR);
        }
    }


    // 이메일 찾기
    @ResponseBody
    @GetMapping("find/email")
    public BaseResponse findEmail(@RequestBody GetMemberEmailReq getMemberEmailReq) throws BaseException {
        try {
            return new BaseResponse<>(memberProvider.findEmail(getMemberEmailReq.getPhone()));
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}