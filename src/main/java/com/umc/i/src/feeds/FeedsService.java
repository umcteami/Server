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
import com.umc.i.config.BaseResponse;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.feeds.model.patch.PatchFeedsReq;
import com.umc.i.src.feeds.model.patch.PatchFeedsRes;
import com.umc.i.src.feeds.model.post.PostCommentReq;
import com.umc.i.src.feeds.model.post.PostFeedsLikeReq;
import com.umc.i.src.feeds.model.post.PostFeedsReq;
import com.umc.i.src.feeds.model.post.PostFeedsRes;
import com.umc.i.utils.S3Storage.Image;
import com.umc.i.utils.S3Storage.UploadImageS3;

import lombok.RequiredArgsConstructor;

import com.umc.i.src.feeds.model.post.PostBlameReq;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedsService {
    @Autowired
    private final UploadImageS3 uploadImageS3;
    @Autowired
    private FeedsDao feedsDao;

    public PostFeedsRes writeFeeds(PostFeedsReq postFeedsReq, List<MultipartFile> file) throws BaseException {
        try {
            int feedsIdx;
            if(file.get(0).isEmpty()) { //이미지 업로드를 안할 경우
                feedsIdx = feedsDao.createFeeds(postFeedsReq, null);
            }
            else {
                List<Image> img = new ArrayList<Image>();
                String fileName = "image" + File.separator + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
      
                for(int i = 0; i < file.size(); i++) {
                    img.add(createAndUploadFile(file.get(i), fileName, postFeedsReq.getBoardIdx(), i));
                }
                feedsIdx = feedsDao.createFeeds(postFeedsReq, img);
            }
            return new PostFeedsRes(postFeedsReq.getBoardIdx(), feedsIdx);
        } catch (BaseException e) {
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
            return new Image(originalFilename, uploadImageS3.getS3(saveFilePath), category, order);
        } catch (IOException e) {
            // 파일 업로드 오류
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.POST_UPLOAD_IMAGE_FAIL);
        }
    }

    // 이야기방, 일기장 게시글 수정
    public PatchFeedsRes editFeeds(PatchFeedsReq patchFeedsReq, List<MultipartFile> file) throws BaseException {
        int feedsIdx = feedsDao.editFeeds(patchFeedsReq);

        try {
            if(file != null) {  // 이미지 수정
                List<Image> img = feedsDao.getFeedsImage(patchFeedsReq.getBoardIdx(), feedsIdx);
                String fileName = "image" + File.separator + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
                List<Image> newImg = new ArrayList<Image>();
                for(int i = 0; i < img.size(); i++) {   
                    uploadImageS3.remove(img.get(i).getUploadFilePath());       // s3에 있는 기존 이미지 삭제
                }
                for(int i = 0; i < file.size(); i++) {
                    if(file.get(i).getOriginalFilename().equals("")) break;
                    newImg.add(createAndUploadFile(file.get(i), fileName, patchFeedsReq.getBoardIdx(), i));      // s3에 새 이미지 업로드
                }
                feedsDao.editFeedsImage(newImg, patchFeedsReq.getBoardIdx(), feedsIdx);
            } 
        } catch(Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.PATCH_EDIT_FEEDS_FAIL);
        }
        
        return new PatchFeedsRes(patchFeedsReq.getBoardIdx(), feedsIdx);
    }

    // 이야기방, 일기장 게시글 삭제
    public void deleteFeeds(int boardType, int feedsIdx) throws BaseException{
        try {
            List<Image> img = feedsDao.getFeedsImage(boardType, feedsIdx);
            for(int i = 0; i < img.size(); i++) {   
                uploadImageS3.remove(img.get(i).getUploadFilePath());       // s3에 있는 이미지 삭제
            }
            
            feedsDao.deleteFeeds(boardType, feedsIdx);
        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        }
        return;
    }


    // 좋아요 변경
    public boolean changeLike(PostFeedsLikeReq postFeedsLikeReq) throws BaseException{
        switch(postFeedsLikeReq.getBoardType()) {
            case 1: // 이야기방
                feedsDao.changeLikeStory(postFeedsLikeReq.getMemIdx(), postFeedsLikeReq.getFeedIdx());
                return true;
            case 2: // 일기장
                feedsDao.changeLikeDiary(postFeedsLikeReq.getMemIdx(), postFeedsLikeReq.getFeedIdx());
                return true;
            case 3: // 장터후기
                feedsDao.changeLikeReview(postFeedsLikeReq.getMemIdx(), postFeedsLikeReq.getFeedIdx());
                return true;
        }
        throw new BaseException(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
    }

    // 댓글 작성
    public void writeComment(PostCommentReq postCommentReq) throws BaseException{
        try {
            switch(postCommentReq.getBoardType()) {
                case 1: // 이야기방
                    feedsDao.writeStoryComment(postCommentReq);
                    return;
                case 2: // 일기장
                    feedsDao.writeDiaryComment(postCommentReq);
                    return;
                case 3: // 장터후기
                    feedsDao.writeReviewComment(postCommentReq);
                    return;
            }
        } catch(BaseException e) {
            throw e;
        }

    }

    public void postBlame(PostBlameReq postBlameReq)throws BaseException{
        try {
            int result = feedsDao.postBlame(postBlameReq);
            if(result != 0){
                throw new BaseException(BaseResponseStatus.POST_FEED_BLAME_DOUBLE);
            }
        } catch (BaseException e){
            throw new BaseException(BaseResponseStatus.POST_FEED_BLAME_DOUBLE);
        }
    }
}
