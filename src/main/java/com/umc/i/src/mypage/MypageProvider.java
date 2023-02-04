package com.umc.i.src.mypage;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.mypage.model.get.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.umc.i.config.BaseResponseStatus.GET_WRITE_FEED_EMPTY;
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
    public List<GetComuWriteRes> getAllWrite(int memIdx,int page)throws BaseException{
        try {
            return mypageDao.getAllWrite(memIdx,page,false);
        }catch (BaseException exception){
            exception.printStackTrace();
            throw new BaseException(GET_WRITE_FEED_EMPTY);
        }
    }

    // 장터후기 스토리 대상 조회- 수정 필요
    public List<GetComuWriteRes> getRSWrite(int memIdx,int page)throws BaseException{
        try {
            return mypageDao.getRSWrite(memIdx,page);
        }catch (BaseException exception){
            exception.printStackTrace();
            throw new BaseException(GET_WRITE_FEED_EMPTY);
        }
    }

    //일기장 대상 조회
    public List<GetComuWriteRes> getDWrite(int memIdx,int page)throws BaseException{
        try {
            return mypageDao.getDiaryWrite(memIdx,page);
        }catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(GET_WRITE_FEED_EMPTY);
        }
    }

    //나눔장터 대상 조회
    public List<GetMarketWriteRes> getMarketWrite(int memIdx,int page)throws BaseException{
        try {
            return mypageDao.getMarketWrite(memIdx,page);
        }catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(GET_WRITE_FEED_EMPTY);
        }
    }
    //count
    public Integer getSWriteCount(int memIdx){
        return mypageDao.getSWriteCount(memIdx);
    }
    public Integer getRWriteCount(int memIdx){
        return mypageDao.getRWriteCount(memIdx);
    }
    public Integer getDWriteCount(int memIdx){
        return mypageDao.getDWriteCount(memIdx);
    }
    public Integer getMWriteCount(int memIdx){
        return mypageDao.getMWriteCount(memIdx);
    }

    //작성한 댓글 조회
    public List<GetComentWriteRes> getComentWrite(int memIdx,int page)throws BaseException{
        try {
            return mypageDao.getComentWrite(memIdx,page);
        }catch (BaseException exception){
            exception.printStackTrace();
            throw new BaseException(GET_WRITE_FEED_EMPTY);
        }
    }
    public Integer getComentWriteCount(int memIdx){
        return mypageDao.getComentWriteCount(memIdx);
    }
    //좋아요한 개시글 조회
    public List<GetComuWriteRes> getLike(int memIdx,int page)throws BaseException{
        try {
            return mypageDao.getAllWrite(memIdx,page,true);
        }catch (BaseException e){
            throw new BaseException(GET_WRITE_FEED_EMPTY);
        }
    }
    public Integer getLikeCount(int memIdx){
        return mypageDao.getLikeCount(memIdx);
    }
    //찜한 나눔장터 조회
    public List<GetWantMarketRes> getWantMarket(int memIdx,int page)throws BaseException{
        try {
            return mypageDao.getWantMarket(memIdx,page);
        }catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(GET_WRITE_FEED_EMPTY);
        }
    }
    public Integer getWantCount(int memIdx){
        return mypageDao.getWantMarketCount(memIdx);
    }
    //차단한 사용자 조회
    public List<GetBlockMemRes> getBlockMem(int memIdx)throws BaseException{
        try {
            return mypageDao.getBlockMem(memIdx);
        }catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(GET_WRITE_FEED_EMPTY);
        }
    }
    //신고한 게시글 조회
    public List<GetBlameFeedRes> getBlameFeed(int memIdx)throws BaseException{
        try {
            return mypageDao.getBlameFeed(memIdx);
        }catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(GET_WRITE_FEED_EMPTY);
        }
    }
}
