package com.umc.i.src.feeds;

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
import com.umc.i.src.feeds.model.patch.PatchFeedsReq;
import com.umc.i.src.feeds.model.patch.PatchFeedsRes;
import com.umc.i.src.feeds.model.post.PostFeedsReq;
import com.umc.i.src.feeds.model.post.PostFeedsRes;
import com.umc.i.utils.S3Storage.Image;
import com.umc.i.utils.S3Storage.UploadImageS3;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedsService {
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
                for(int i = 0; i < file.size(); i++) {
                    img.add(createAndUploadFile(file.get(i), fileName, boardType, i));
                }
                int feedsIdx = feedsDao.createFeeds(boardType, postFeedsReq);
                feedsDao.createFeedsImage(img, feedsIdx);
    
                return new PostFeedsRes(feedsIdx);
            } catch (Exception e) {
                // throw new BaseException(BaseResponseStatus.POST_UPLOAD_IMAGE_FAIL);
                e.printStackTrace();
            }
        }
        return null;
        
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

    // 이야기방, 일기장 게시글 수정
    public PatchFeedsRes editFeeds(int boardType, PatchFeedsReq patchFeedsReq, List<MultipartFile> file) throws BaseException {
        int feedsIdx = feedsDao.editFeeds(boardType, patchFeedsReq);

        try {
            if(file.get(0) != null) {  // 이미지 수정
                List<Image> img = feedsDao.getFeedsImage(boardType, feedsIdx);
                String fileName = "image" + File.separator + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
                List<Image> newImg = new ArrayList<Image>();
                for(int i = 0; i < img.size(); i++) {   
                    uploadImageS3.remove(img.get(i).getUploadFilePath());       // s3에 있는 기존 이미지 삭제
                }
                for(int i = 0; i < file.size(); i++) {
                    if(file.get(i).getOriginalFilename().equals("")) break;
                    newImg.add(createAndUploadFile(file.get(i), fileName, boardType, i));      // s3에 새 이미지 업로드
                }
                feedsDao.editFeedsImage(newImg, boardType, feedsIdx);
            } 
        } catch(Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.PATCH_EDIT_FEEDS_FAIL);
        }
        
        
        return new PatchFeedsRes(feedsIdx);
    }
}
