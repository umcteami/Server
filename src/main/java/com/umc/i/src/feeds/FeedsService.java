package com.umc.i.src.feeds;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.feeds.model.post.PostBlameReq;
import com.umc.i.utils.S3Storage.UploadImageS3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedsService {
    @Autowired
    private final UploadImageS3 uploadImageS3;
    @Autowired
    private FeedsDao feedsDao;

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
