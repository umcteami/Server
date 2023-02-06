package com.umc.i.src.feeds;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.umc.i.src.feeds.model.get.GetAllFeedsRes;
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
    public List<GetAllFeedsRes> getAllStories() {
        return feedsDao.getAllStories();
    }

    // 일기장 전체 조회
    public List<GetAllFeedsRes> getAllDiaries() {
        return feedsDao.getAllDiaries();
    }

    // 이야기방 카테고리별 조회
    public List<GetAllFeedsRes> getStoryByRoomType(int roomType) {
        return feedsDao.getStoryRoomType(roomType);
    }

    // 일기장 카테고리별 조회
    public List<GetAllFeedsRes> getDiariesByRoomType(int roomType) {
        return feedsDao.getDiariesByRoomType(roomType);
    }

    // 이야기방 상세 조회
    public GetFeedRes getStory(int storyIdx) {

            List<Image> img = feedsDao.getFeedsImage(1, storyIdx);
            List<String> filePath = new ArrayList();
            
            if(img != null) {   // 이미지가 있으면
                for(int i = 0; i < img.size(); i++) {
                    filePath.add(uploadImageS3.getS3(img.get(i).getUploadFilePath()));
                }
            }
            
            return new GetFeedRes(feedsDao.getStory(storyIdx), filePath);

    }

    // 일기장 상세 조회
    public GetFeedRes getDiary(int diaryIdx) {

        List<Image> img = feedsDao.getFeedsImage(2, diaryIdx);
        List<String> filePath = new ArrayList();
        
        if(img != null) {   // 이미지가 있으면
            for(int i = 0; i < img.size(); i++) {
                filePath.add(uploadImageS3.getS3(img.get(i).getUploadFilePath()));
            }
        }
        
        return new GetFeedRes(feedsDao.getDiary(diaryIdx), filePath);

}
    
}
