package com.umc.i.src.feeds.model.post;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostFeedsReq<MultipartFile> {
    private int userIdx;  // 글쓴이 인덱스
    private int roomType;   // 1: 수다방(간호 일기), 2: 질문방(무지개 일기), 3: 정보방
    private String title;   // 제목
    private String content; // 내용
    private List<MultipartFile> feedImg;    // 이미지 파일 리스트    
    private int imgCnt;     // 이미지 개수
}
