package com.umc.i.src.review.model.get;

import java.util.Date;
import java.util.List;

import com.umc.i.src.review.model.Review;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetReviewRes {
    private Review review;  // 장터 후기
    private List<String> img;   // 이미지
}


