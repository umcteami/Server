package com.umc.i.src.member.join.auth;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailAuthService {
    //의존성 주입 -> 필요한 객체 가져오기
    private final JavaMailSender eMailSender;
    // 타임리프를 사용하기 위한 객체
    private final SpringTemplateEngine templateEngine;
    private String authCode; //인증코드

    // 랜덤 인증코드 생성
    public void createCode() {
        Random random = new Random();
        int checkNum = random.nextInt(899999) + 100000; // 인증번호 범위: 100000 ~ 999999
        System.out.println("인증번호: " + checkNum);
        authCode = Integer.toString(checkNum);
    }

    // 메일 양식 작성
    public MimeMessage createEmailForm(String email) throws MessagingException, UnsupportedEncodingException {
        createCode();   // 인증코드 생성
        String setFrom = "amanda010926@gmail.com";  // 보내는 사람 이메일
        String toEmail = email; //받는 사람
        String title = "아이 - 아름답게 이별하는 법 본인 인증 코드";    // 메일 제목

        MimeMessage message = eMailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, toEmail);     //보낼 이메일 설정
        message.setSubject(title);      // 제목 설정   
        message.setFrom(setFrom);       // 보내는 이메일
        message.setText(setContext(authCode), "utf-8", "html");

        return message;
    }

    // 메일 전송
    public String sendEmail(String toEmail) throws MessagingException, UnsupportedEncodingException {
        // 메일 전송에 필요한 정보 설정
        MimeMessage emailForm = createEmailForm(toEmail);
        // 실제 메일 전송
        eMailSender.send(emailForm);

        return authCode;
    }

    // 타임리프를 이용한 context 설정
    public String setContext(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process("signup", context);     // signup.html
    }
    
}
