package com.umc.i.src.feeds;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.feeds.model.get.GetAllFeedsRes;
import com.umc.i.src.feeds.model.get.GetCommentRes;
import com.umc.i.src.feeds.model.get.GetFeedRes;
import com.umc.i.utils.S3Storage.Image;
import com.umc.i.utils.S3Storage.UploadImageS3;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Service
@RequiredArgsConstructor
public class FeedsProvider {
    @Autowired
    private final FeedsDao feedsDao;
    @Autowired
    private final UploadImageS3 uploadImageS3;

    // 이야기방 전체 조회
    public List<GetAllFeedsRes> getAllStories(int page) throws BaseException {
        try {
            return feedsDao.getAllStories(page);
        } catch (BaseException e) {
            e.printStackTrace();
            throw e;
        } 
    }

    // 일기장 전체 조회
    public List<GetAllFeedsRes> getAllDiaries(int page) {
        return feedsDao.getAllDiaries(page);
    }

    // 이야기방 카테고리별 조회
    public List<GetAllFeedsRes> getStoryByRoomType(int roomType, int page) {
        return feedsDao.getStoryRoomType(roomType, page);
    }

    // 일기장 카테고리별 조회
    public List<GetAllFeedsRes> getDiariesByRoomType(int roomType, int page) {
        return feedsDao.getDiariesByRoomType(roomType, page);
    }

    // 이야기방 상세 조회
    public GetFeedRes getStory(int storyIdx, int memIdx) {

            List<Image> img = feedsDao.getFeedsImage(1, storyIdx);
            List<String> filePath = new ArrayList();
            
            if(img != null) {   // 이미지가 있으면
                for(int i = 0; i < img.size(); i++) {
                    filePath.add(uploadImageS3.getS3(img.get(i).getUploadFilePath()));
                }
            }
            
            return new GetFeedRes(feedsDao.getStory(storyIdx, memIdx), filePath);

    }

    // 일기장 상세 조회
    public GetFeedRes getDiary(int diaryIdx, int memIdx) {

        List<Image> img = feedsDao.getFeedsImage(2, diaryIdx);
        List<String> filePath = new ArrayList();
        
        if(img != null) {   // 이미지가 있으면
            for(int i = 0; i < img.size(); i++) {
                filePath.add(uploadImageS3.getS3(img.get(i).getUploadFilePath()));
            }
        }
        
        return new GetFeedRes(feedsDao.getDiary(diaryIdx, memIdx), filePath);

    }
    
    // 댓글 조회
    public List<GetCommentRes> getComments(int boardType, int feedIdx) throws BaseException {
        try {
            return feedsDao.getComments(boardType, feedIdx);
        } catch (BaseException e) {
            throw e;
        }
    }

    // 아이홈 통합 조회
    public List<GetAllFeedsRes> getFeeds(int page){
        return feedsDao.getAllFeeds(page);
    }
}
