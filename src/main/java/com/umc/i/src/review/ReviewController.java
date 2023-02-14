package com.umc.i.src.review;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonSerializable.Base;
import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponse;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.review.model.get.GetAllReviewsRes;
import com.umc.i.src.review.model.patch.PatchReviewsDeleteReq;
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
    @Autowired
    private final ReivewProvider reivewProvider;

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

    @ResponseBody
    @PatchMapping("/delete")     // 장터 후기 삭제
    public BaseResponse deleteReviews(@RequestBody PatchReviewsDeleteReq patchReviewsDeleteReq) throws BaseException{
        int reviewIdx = patchReviewsDeleteReq.getReviewIdx();
        try {
            reviewService.deleteReview(reviewIdx);
            return new BaseResponse<>("장터 후기를 삭제했습니다");
        } catch (BaseException e) {
            throw e;
        }
    }

    @ResponseBody
    @GetMapping("/{reviewIdx}")     // 장터 후기 조회
    public BaseResponse getReview(@PathVariable("reviewIdx") int reviewIdx, @RequestParam("memIdx") int memIdx) throws BaseException{
        try {
            return new BaseResponse<>(reivewProvider.getReview(reviewIdx, memIdx));
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("")     // 장터 후기 전체 조회
    public BaseResponse<List<GetAllReviewsRes>> getAllReviews(@RequestParam(defaultValue = "0") int page) throws BaseException {
        try {
            return new BaseResponse<>(reivewProvider.getAllReviews(page));
        } catch (Exception e) {
            e.getStackTrace();
            return new BaseResponse<> (BaseResponseStatus.GET_REVIEW_FAIL);
        }
    }


}
