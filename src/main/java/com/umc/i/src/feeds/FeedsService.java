package com.umc.i.src.feeds;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.feeds.model.post.PostFeedsReq;
import com.umc.i.src.feeds.model.post.PostFeedsRes;
import com.umc.i.utils.S3Storage.Image;
import com.umc.i.utils.S3Storage.UploadImageS3;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedsService {
    @Autowired
    private final com.umc.i.utils.S3Storage.FileManager fileManager;
    @Autowired
    private final UploadImageS3 uploadImageS3;
    @Autowired
    private FeedsDao feedsDao;

    public PostFeedsRes writeFeeds(int boardType, PostFeedsReq postFeedsReq, List<MultipartFile> file) throws BaseException {
        if(postFeedsReq.getImgCnt() == 0) { //이미지 업로드를 안할 경우
            int feedsIdx = feedsDao.createFeeds(boardType, postFeedsReq);
            return new PostFeedsRes(feedsIdx);
        }
        else {
            List<Image> img = new ArrayList<Image>();
            String fileName = "image" + File.separator + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
            try {
                for(int i = 0; i < img.size(); i++) {
                    img.add(createAndUploadFile(file.get(i), fileName, boardType, i));
                }
                int feedsIdx = feedsDao.createFeeds(boardType, postFeedsReq);
                feedsDao.createFeedsImage(img, feedsIdx);
    
                return new PostFeedsRes(feedsIdx);
            } catch (Exception e) {
                throw new BaseException(BaseResponseStatus.POST_UPLOAD_IMAGE_FAIL);
            }
        }
        
    }

    //임시 파일 생성 -> 업데이트 -> 임시 파일 삭제
    public Image createAndUploadFile(MultipartFile mf, String filePath, int category, int order) throws BaseException {
        long time = System.currentTimeMillis();
        String originalFilename = mf.getOriginalFilename();
        String saveFileName = String.format("%d_%s", time, originalFilename.replaceAll(" ", ""));

        // 파일 생성
        File uploadFile = null;
        try {
            Optional<File> uploadFileOpt = fileManager.convertMultipartFileToFile(mf);
            if(uploadFileOpt.isEmpty()) {
                // 파일 변환 실패
                throw new BaseException(BaseResponseStatus.POST_UPLOAD_IMAGE_FAIL);
            }
            uploadFile = uploadFileOpt.get();

            //파일 업로드
            String saveFilePath = uploadImageS3.upload(uploadFile, filePath, saveFileName);

            return new Image(originalFilename, File.separator + saveFilePath, category, order);
        } catch (IOException e) {
            // 파일 업로드 오류
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.POST_UPLOAD_IMAGE_FAIL);
        } finally {
            // 파일 삭제
            if(uploadFile != null) {
                uploadFile.delete();
            }
        }
    }
}
