package com.umc.i.src.feeds;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.umc.i.src.feeds.model.get.GetAllFeedsRes;

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

    // 이야기방 전체 조회
    public List<GetAllFeedsRes> getAllStories() {
        return feedsDao.getAllStories();
    }
    
}
