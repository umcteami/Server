package com.umc.i.src.review;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.review.model.post.PostReviewReq;
import com.umc.i.src.review.model.post.PostReviewRes;
import com.umc.i.utils.S3Storage.Image;
import com.umc.i.utils.S3Storage.UploadImageS3;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {
    @Autowired
    private final ReviewDao reviewDao;
    @Autowired
    private final UploadImageS3 uploadImageS3;

    public PostReviewRes writeReviews(PostReviewReq postReviewReq, List<MultipartFile> file) throws BaseException {
        try {
            int reviewIdx;
            if(file.get(0).isEmpty()) {      // 이미지 업로드를 하지 않을 경우
                reviewIdx = reviewDao.createReviews(postReviewReq);
            } else {
                List<Image> img = new ArrayList<Image>();
                String fileName = "image" + File.separator + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));

                for(int i = 0; i < file.size(); i++) {
                    img.add(createAndUploadFile(file.get(i), fileName, 3, i));
                }
                reviewIdx = reviewDao.createReviews(postReviewReq);
                reviewDao.createReviewsImage(img, reviewIdx);
            }

            return new PostReviewRes(reviewIdx);
        } catch(BaseException e) {
            e.printStackTrace();
            throw e;
        }
    }


    // 파일 업로드
    public Image createAndUploadFile(MultipartFile mf, String filePath, int category, int order) throws BaseException {
        long time = System.currentTimeMillis();
        String originalFilename = mf.getOriginalFilename();
        String saveFileName = String.format("%d_%s", time, originalFilename.replaceAll(" ", ""));
        try {
            //파일 업로드
            String saveFilePath = uploadImageS3.upload(mf, filePath, saveFileName);
            return new Image(originalFilename, saveFilePath, category, order);
        } catch (IOException e) {
            // 파일 업로드 오류
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.POST_UPLOAD_IMAGE_FAIL);
        }
    }
    
}
