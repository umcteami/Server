package com.umc.i.src.feeds.model.get;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAllFeedsRes {
    private int boardType;      // 1: 이야기방, 2: 일기장
    private int roomType;       // 1: 수다방/간호일기, 2: 질문방/무지개일기, 3:정보방
    private int feedIdx;        // 게시물 인덱스
    private int memIdx;         // 작성자 인덱스
    private String memNick;     // 작성자 닉네임
    private String title;       // 제목
    private String img;         // 대표 이미지
    private int hit;            // 조회수
    private int commentCnt;     // 댓글수
    private int likeCnt;        // 좋아요 수
    private String createAt;    // 작성일

}
