package com.umc.i.src.review;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.review.model.Review;
import com.umc.i.src.review.model.get.GetAllReviewsRes;
import com.umc.i.src.review.model.get.GetReviewRes;
import com.umc.i.utils.S3Storage.Image;
import com.umc.i.utils.S3Storage.UploadImageS3;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReivewProvider {
    @Autowired
    private final ReviewDao reviewDao;
    @Autowired
    private final UploadImageS3 uploadImageS3;


    // 장터 후기 상세 조회
    public GetReviewRes getReview(int reviewIdx) throws BaseException {
        try {
            List<Image> img = reviewDao.getReviewsImage(reviewIdx);
            List<String> filePath = new ArrayList();
            if(img != null) {   // 이미지가 있으면
                for(int i = 0; i < img.size(); i++) {
                    filePath.add(img.get(i).getUploadFilePath());
                }
            }
            
            return new GetReviewRes(reviewDao.getReview(reviewIdx), filePath);
        } catch (Exception e) {
            e.getStackTrace();
            throw new BaseException(BaseResponseStatus.GET_REVIEW_FAIL);
        }
    }

    // 장터후기 전체 조회
    public List<GetAllReviewsRes> getAllReviews(int page) {
        return reviewDao.getAllReviews(page);

    }

    
}
