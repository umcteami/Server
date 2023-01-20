package com.umc.i.src.mypage.model.get;

import lombok.*;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.support.SimpleTriggerContext;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class GetComuWriteRes {
    @NonNull
    private int boarIdx;
    private int roomType; //장터후기 nullexception없애기
    @NonNull
    private int comuIdx;
    private String title; //boarIdx 가 장터후기면 필터링
    private String feedImg;
    @NonNull
    private int hit;       //Dao단에서 분리하고 provider에서 합치기
    private int countLike;
    private int countComment;
    @NonNull
    private String createAt;
    @NonNull
    private int imageCount;

    public GetComuWriteRes(int boarIdx, int roomType, int comuIdx,String title,int hit,String createAt,int imageCount){
        this.boarIdx = boarIdx;
        this.roomType = roomType;
        this.comuIdx = comuIdx;
        this.title = title;
        this.hit = hit;
        this.createAt = createAt;
        this.imageCount = imageCount;
    }
}
