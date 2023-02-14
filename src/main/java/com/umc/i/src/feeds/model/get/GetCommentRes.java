package com.umc.i.src.feeds.model.get;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetCommentRes {
    private int commentIdx;     // 댓글 인덱스
    private int boardIdx;       // 1: 이야기방, 2: 일기장, 3: 장터후기
    private int feedIdx;        // 게시글 인덱스
    private int parentIdx;     // 부모 댓글 인덱스
    private int memIdx;         // 댓글 작성자 인덱스
    private String nickname;    // 작성자 닉네임
    private String comment;     // 댓글 내용
    private String createAt;        // 댓글 작성 시간
}
