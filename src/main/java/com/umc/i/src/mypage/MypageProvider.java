package com.umc.i.src.mypage;

import com.umc.i.config.BaseException;
import com.umc.i.src.mypage.model.get.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    class feedCreateAtComparator implements Comparator<GetComentWriteRes> {
        @Override
        public int compare(GetComentWriteRes f1, GetComentWriteRes f2) {
            return f1.getFeedCreateAt().compareTo(f2.getFeedCreateAt());
        }
    }
    //전체 대상 작성 글 조회
    public List<GetComuWriteRes> getWrite(int memIdx)throws BaseException{
        try {
            List<GetComuWriteRes> RSDList = mypageDao.getDiaryWrite(memIdx,false);
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
            List<GetComuWriteRes> RSList = mypageDao.getReviewWrite(memIdx,false);
            RSList.addAll(mypageDao.getStoryWrite(memIdx,false));

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
            return mypageDao.getStoryWrite(memIdx,false);
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

    //작성한 댓글 조회
    public List<GetComentWriteRes> getCmtWrite(int memIdx)throws BaseException{
        try {
            List<GetComentWriteRes> cmtList = mypageDao.getComentSWrite(memIdx);
            cmtList.addAll(mypageDao.getComentDWrite(memIdx));
            cmtList.addAll(mypageDao.getComentRWrite(memIdx));

            Collections.sort(cmtList, new feedCreateAtComparator().reversed());
            return cmtList;
        }catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(INTERNET_ERROR);
        }
    }
    //좋아요한 개시글 조회
    public List<GetComuWriteRes> getLikeFeed(int memIdx)throws BaseException{
        try {
            List<GetComuWriteRes> cmtList = mypageDao.getReviewWrite(memIdx,true);
            cmtList.addAll(mypageDao.getStoryWrite(memIdx,true));
            cmtList.addAll(mypageDao.getDiaryWrite(memIdx,true));

            Collections.sort(cmtList, new createAtComparator().reversed());
            return cmtList;
        }catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(INTERNET_ERROR);
        }
    }
    //찜한 나눔장터 조회
    public List<GetWantMarketRes> getWantMarket(int memIdx)throws BaseException{
        try {
            return mypageDao.getWantMarket(memIdx);
        }catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(INTERNET_ERROR);
        }
    }
    //차단한 사용자 조회
    public List<GetBlockMemRes> getBlockMem(int memIdx)throws BaseException{
        try {
            return mypageDao.getBlockMem(memIdx);
        }catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(INTERNET_ERROR);
        }
    }
    //신고한 게시글 조회
    public List<GetBlameFeedRes> getBlameFeed(int memIdx)throws BaseException{
        try {
            return mypageDao.getBlameFeed(memIdx);
        }catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(INTERNET_ERROR);
        }
    }
}
