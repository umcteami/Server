package com.umc.i.member.auth;

import com.umc.i.config.BaseResponse;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.member.auth.model.PostAuthNumberReq;
import com.umc.i.member.auth.model.PostAuthNumberRes;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Controller
@AllArgsConstructor
public class SignUpAuthController {

    private final SignUpAuthService signUpAuthService;

    @PostMapping("/auth/validation")
    public ResponseEntity<PostAuthNumberRes> checkAuthNumber(@RequestBody PostAuthNumberReq postAuthNumberReq) {
        int authIdx = postAuthNumberReq.getAuthIdx();
        int maKey = postAuthNumberReq.getAuthNumber();

        SignAuthNumber signAuthNumberObject = signUpAuthService.getSignAuthNumberObject(authIdx);

        if (signAuthNumberObject == null) {
            return ResponseEntity.notFound().build();
        }

        if (signUpAuthService.isExpired(signAuthNumberObject) && signUpAuthService.isEqual(signAuthNumberObject, maKey)) {
            PostAuthNumberRes res = new PostAuthNumberRes(true);

            return new ResponseEntity<>(res, HttpStatus.OK);
        }

        return ResponseEntity.notFound().build();
    }
}
