package com.umc.i.src.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.review.model.Review;
import com.umc.i.utils.S3Storage.UploadImageS3;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReivewProvider {
    @Autowired
    private final ReviewDao reviewDao;
    @Autowired
    private final UploadImageS3 uploadImageS3;


    // 장터 후기 하나 조회
    public Review getReview(int reviewIdx) throws BaseException {
        try {
            return reviewDao.getReview(reviewIdx);
        } catch (Exception e) {
            e.getStackTrace();
            throw new BaseException(BaseResponseStatus.GET_REVIEW_FAIL);
        }
    }
    
}
