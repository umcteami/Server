package com.umc.i.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),
    INTERNET_ERROR(false,2000,"인터넷오류"),
    // 2000 : Request 오류

    // [POST] /member/join
    POST_MEMBER_EMPTY_EMAIL(false, 2010, "이메일을 입력해주세요."),
    POST_MEMBER_INVALID_EMAIL(false, 2011, "이메일 형식을 확인해주세요."),
    POST_MEMBER_EXISTS_EMAIL(false, 2012, "중복된 이메일입니다."),

    POST_MEMBER_EMPTY_PHONE(false, 2013, "전화번호를 입력해주세요."),
    POST_MEMBER_INVALID_PHONE(false, 2014, "전화번호 형식을 확인해주세요."),
    POST_MEMBER_EXISTS_PHONE(false, 2015, "중복된 전화번호입니다."),

    POST_AUTH_INVALID_TYPE(false, 2016, "인증 타입을 확인해주세요"),
    POST_AUTH_SEND_FAIL(false, 2017, "인증번호 발송 실패"),
    POST_MEMBER_JOIN_NICKLEN(false,2030,"닉네임 길이 제한"),
    POST_MEMBER_ISREGEX_NICK(false,2031,"닉네임 특수문자 포함"),
    POST_MEMBER_JOIN_INTROLEN(false,2032,"한줄소개 길이 제한"),
    POST_MEMBER_JOIN_PWLEN(false,2033,"비밀번호 길이 제한"),
    POST_MEMBER_ISREGEX_PW(false,2034,"비밀번호 형식 제한"),
    POST_MEMBER_JOIN(false,2018,"회원가입 실패"),

    //[PATCH] /member
    PATCH_MEMBER_NICKNUM_OVER(false,2040,"닉네임 변경 횟수 초과"),
    PATCH_MEMBER_NICK_DOUBLE(false,2041,"닉네임 중복"),
    POST_NUMBER_AUTH_TIME_FAILED(false, 2019, "시간 초과"),

    POST_AUTH_JWT_TOKEN_INVALID(false, 2020, "jwt 토큰 만료"),

    POST_AUTH_MEMBER_NOT_EXIST(false, 2021, "회원 정보가 존재하지 않음"),

    POST_MARKET_FEED_FAILED(false, 2022, "나눔 거래 게시물 작성 실패"),
    POST_FEED_BLAME_DOUBLE(false,2023,"이미 신고한 게시글 입니다"),
    POST_NEMBER_BLOCK_DOUBLE(false,2027,"이미 차단한 멤버입니다."),
    POST_MEMBER_WITHDRAW(false,2028,"유저가 존재하지 않음"),
    //[GET] /mypage
    GET_WRITE_FEED_FAILED(false,2024,"조회 실패"),
    GET_WRITE_FEED_EMPTY(false,2025,"조회 대상 없음"),
    GET_WRITE_FEED_TYPERROR(false,2026,"path 오류"),




    GET_MARKET_FEED_BY_PARAM_FAILED(false, 2100, "나눔 거래 게시물 조회 카테고리 설정 오류"),

    FEED_UNAUTHORIZED(false, 2111, "게시물 수정 및 삭제 권한 없음"),;

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
    }
