package com.umc.i.src.mypage;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.umc.i.config.BaseException;
import com.umc.i.src.mypage.model.get.GetComuWriteRes;
import com.umc.i.src.mypage.model.get.GetMarketWriteRes;
import com.umc.i.src.mypage.model.get.GetMypageMemRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    class createAtComparator implements Comparator<GetComuWriteRes> {
        @Override
        public int compare(GetComuWriteRes f1, GetComuWriteRes f2) {
            return f1.getCreateAt().compareTo(f2.getCreateAt());
        }
    }
    //전체 대상 작성 글 조회
    public List<GetComuWriteRes> getWrite(int memIdx)throws BaseException{
        try {
            List<GetComuWriteRes> RSDList = mypageDao.getDiaryWrite(memIdx);
            RSDList.addAll(getRSWrite(memIdx));

            Collections.sort(RSDList, new createAtComparator().reversed());

            return RSDList;
        }catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(INTERNET_ERROR);
        }
    }
    // 장터후기 스토리 대상 조회
    public List<GetComuWriteRes> getRSWrite(int memIdx)throws BaseException{
        try {
            List<GetComuWriteRes> RSList = mypageDao.getReviewWrite(memIdx);
            RSList.addAll(mypageDao.getStoryWrite(memIdx));

            Collections.sort(RSList, new createAtComparator());

            return RSList;
        }catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(INTERNET_ERROR);
        }
    }
    //일기장 대상 조회
    public List<GetComuWriteRes> getSWrite(int memIdx)throws BaseException{
        try {
            return mypageDao.getDiaryWrite(memIdx);
        }catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(INTERNET_ERROR);
        }
    }
    //나눔장터 대상 조회
    public List<GetMarketWriteRes> getMarketWrite(int memIdx)throws BaseException{
        try {
            return mypageDao.getMarketWrite(memIdx);
        }catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(INTERNET_ERROR);
        }
    }
}
