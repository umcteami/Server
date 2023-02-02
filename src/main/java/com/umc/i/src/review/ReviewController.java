package com.umc.i.src.review;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponse;
import com.umc.i.src.review.model.patch.PatchReviewsReq;
import com.umc.i.src.review.model.patch.PatchReviewsRes;
import com.umc.i.src.review.model.post.PostReviewReq;
import com.umc.i.src.review.model.post.PostReviewRes;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    @Autowired
    private final ReviewService reviewService;

    @ResponseBody
    @PostMapping("/write")     // 장터 후기 작성
    public BaseResponse<PostReviewRes> createFeeds(@RequestPart("request") PostReviewReq postReviewReq, 
    @RequestPart("img") List<MultipartFile> file) throws BaseException {
        try {
            return new BaseResponse<>(reviewService.writeReviews(postReviewReq, file));
        } catch(BaseException e) {
            throw e;
        }
    }


    @ResponseBody
    @PatchMapping("/edit")      // 장터 후기 수정
    public BaseResponse<PatchReviewsRes> editReviews(@RequestPart("request") PatchReviewsReq patchReviewsReq, 
    @RequestPart("img") List<MultipartFile> file) throws BaseException {
        try {
            return new BaseResponse<> (reviewService.editReviews(patchReviewsReq, file));
        } catch(BaseException e) {
            throw e;
        }
    }
}
