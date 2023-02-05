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

    // 2000 : Request 오류

    // [POST] /member/join
    POST_MEMBER_EMPTY_EMAIL(false, 2010, "이메일을 입력해주세요."),
    POST_MEMBER_INVALID_EMAIL(false, 2011, "이메일 형식을 확인해주세요."),
    POST_MEMBER_EXISTS_EMAIL(false,2012,"중복된 이메일입니다."),

    POST_MEMBER_EMPTY_PHONE(false, 2013, "전화번호를 입력해주세요."),
    POST_MEMBER_INVALID_PHONE(false, 2014, "전화번호 형식을 확인해주세요."),
    POST_MEMBER_EXISTS_PHONE(false,2015,"중복된 전화번호입니다."),

    POST_AUTH_INVALID_TYPE(false, 2016, "인증 타입을 확인해주세요"),
    POST_AUTH_SEND_FAIL(false, 2017, "인증번호 발송 실패"),


    // [POST] /feeds
    POST_FEEDS_INVALID_TYPE(false, 2020, "타입을 확인해주세요"),
    


    // 3000 : 서버 오류

    // image
    POST_FEEDS_UPLOAD_FAIL(false, 3001, "게시글 저장에 실패했습니다."),
    POST_UPLOAD_IMAGE_FAIL(false, 3002, "파일 업로드에 실패했습니다"),
    DELETE_IMAGE_FAIL(false, 3003, "이미지 삭제에 실패했습니다."),

    PATCH_EDIT_FEEDS_FAIL(false, 3010, "수정을 실패했습니다"),
    PATCH_DELETE_FEEDS_FAIL(false, 3011, "게시물 삭제를 실패했습니다"),
    
    
    
    
    GET_REVIEW_FAIL(false, 3080, "게시물 조회에 실패했습니다");




    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
