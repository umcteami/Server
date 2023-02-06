package com.umc.i.src.mypage;

import com.umc.i.config.BaseResponse;
import com.umc.i.src.mypage.model.post.PostAskReq;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MypageService {
    @Autowired
    private MypageDao mypageDao;
    public void postAsk(PostAskReq postAskReq){
        mypageDao.postAsk(postAskReq);
    }
}
