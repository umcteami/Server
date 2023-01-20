package com.umc.i.src.mypage;

import com.umc.i.config.BaseException;
import com.umc.i.src.mypage.model.get.GetComuWriteRes;
import com.umc.i.src.mypage.model.get.GetMypageMemRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.umc.i.config.BaseResponseStatus.INTERNET_ERROR;

@Service
@Slf4j
public class MypageProvider {
    @Autowired
    private MypageDao mypageDao;

    public GetMypageMemRes getMyPMem(int memIdx)throws BaseException {
        try {
            GetMypageMemRes getMypageMemRes = mypageDao.getMyPMem(memIdx);
            getMypageMemRes.setFeedCount(mypageDao.getMyPCountMem(memIdx).get(0));
            getMypageMemRes.setDiaryCount(mypageDao.getMyPCountMem(memIdx).get(1));
            getMypageMemRes.setMarketCount(mypageDao.getMyPCountMem(memIdx).get(2));

            return getMypageMemRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(INTERNET_ERROR);
        }
    }
    //전체 대상 작성 글 조회
    public GetComuWriteRes getComuWrite(int memIdx)throws BaseException{
        try {


        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(INTERNET_ERROR);
        }
    }
}
