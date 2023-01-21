package com.umc.i.src.mypage;

import com.umc.i.src.member.model.get.GetMemRes;
import com.umc.i.src.mypage.model.MypageFeed;
import com.umc.i.src.mypage.model.get.GetComuWriteRes;
import com.umc.i.src.mypage.model.get.GetMypageMemRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
@Slf4j
public class MypageDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //마이 홈페이지 시작창 조회
    public GetMypageMemRes getMyPMem (int memIdx){
        //유저 정보
        String getMyPMemQuery = "select mem_nickname,mem_profile_content,mem_profile_url,mem_noti from Member where mem_idx = ?";
        return this.jdbcTemplate.queryForObject(getMyPMemQuery,
                (rs, rowNum) -> new GetMypageMemRes(
                        rs.getString("mem_nickname"),
                        rs.getString("mem_profile_content"),
                        rs.getString("mem_profile_url"),
                        rs.getString("mem_noti")),
                memIdx);

    }
    //작성한 글 수
    public List<Integer> getMyPCountMem(int memIdx){
        List<Integer> getMyPCountMemList = new ArrayList<>();
        //장터 후기 수
        String countMarketReQuery = "select count(*) from Market_review where buy_mem_idx = ?";
        int countMarketRe = this.jdbcTemplate.queryForObject(countMarketReQuery, int.class, memIdx);
        //이야기방 작성 글 수
        String countStoryQuery = "select count(*) from Story_feed where mem_idx = ?";
        int countStory = this.jdbcTemplate.queryForObject(countStoryQuery,int.class,memIdx);

        int countComu = countMarketRe+countStory;
        getMyPCountMemList.add(countComu);
        //일기장 작성 글 수
        String countDiaryQuery = "select count(*) from Diary_feed where mem_idx = ?";
        getMyPCountMemList.add(this.jdbcTemplate.queryForObject(countDiaryQuery,int.class,memIdx));
        //나눔장터 작성 글 수
        String countMarketQuery = "select count(*) from Market where mem_idx = ?";
        getMyPCountMemList.add(this.jdbcTemplate.queryForObject(countMarketQuery,int.class,memIdx));

        return getMyPCountMemList;
    }
    // 전체 대상 작성한 글 조회
    public List<GetComuWriteRes> getDiaryWrite(int memIdx){

        try {
            String getDiaryWriteQuery = "select board_idx,diary_roomType,diary_idx,diary_title,diary_hit,diary_created_at,diary_image from Diary_feed where mem_idx = ?";
            return this.jdbcTemplate.query(getDiaryWriteQuery,
                    (rs, rowNum) -> {
                        GetComuWriteRes getDiaryWriteRes = new GetComuWriteRes(
                                rs.getInt("board_idx"),
                                rs.getInt("diary_roomType"),
                                rs.getInt("diary_idx"),
                                rs.getString("diary_title"),
                                rs.getInt("diary_hit"),
                                rs.getString("diary_created_at"),
                                rs.getInt("diary_image")
                        );
                        //이미지 없을 경우
                        if(getDiaryWriteRes.getImageCount() != 0){
                            String getDiaryImgQuery = "select image_url from Image_url where content_category = ? and content_idx = ? and image_order = 0";
                            String diaryImg = this.jdbcTemplate.queryForObject(getDiaryImgQuery,String.class,getDiaryWriteRes.getBoarIdx(),getDiaryWriteRes.getComuIdx());
                            getDiaryWriteRes.setFeedImg(diaryImg);
                        }else{
                            getDiaryWriteRes.setFeedImg(null);
                        }
                        //count
                        String getCountLikeQuery = "select count(*) from Diary_feed_like where diary_idx = ?";
                        int countLike = this.jdbcTemplate.queryForObject(getCountLikeQuery,int.class,getDiaryWriteRes.getComuIdx());
                        String getCountComment = "select count(*) from Diary_comment where diary_idx = ?";
                        int countComment = this.jdbcTemplate.queryForObject(getCountComment,int.class,getDiaryWriteRes.getComuIdx());

                        getDiaryWriteRes.setCountComment(countComment);
                        getDiaryWriteRes.setCountLike(countLike);
                        return getDiaryWriteRes;
                    },
                    memIdx);

        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return null;
        }

    }
    public List<GetComuWriteRes> getReviewWrite(int memIdx){
        try{
            String getReviewWriteQuery = "select board_idx,review_idx,review_hit,review_created_at,review_image from Market_review where buy_mem_idx = ?";
            return this.jdbcTemplate.query(getReviewWriteQuery,
                    (rs, rowNum) -> {
                        GetComuWriteRes getReviewWriteRes = new GetComuWriteRes(
                                rs.getInt("board_idx"),
                                rs.getInt("review_idx"),
                                rs.getInt("review_hit"),
                                rs.getString("review_created_at"),
                                rs.getInt("review_image")
                        );
                        //roomType set
                        getReviewWriteRes.setRoomType(0);
                        //title - 굿즈 차우 변경예정
                        String getReviewIdxQuery = "select review_goods,m.mem_nickname from Market_review join Member as m where board_idx = ? and review_idx = ? and sell_mem_idx = m.mem_idx and buy_mem_idx = ?";

                        MypageFeed mypageFeed = this.jdbcTemplate.queryForObject(getReviewIdxQuery,
                                (myrs, myrowNum) -> new MypageFeed(
                                        myrs.getString("review_goods"),
                                        myrs.getString("mem_nickname")), getReviewWriteRes.getBoarIdx(),getReviewWriteRes.getComuIdx(),memIdx);
                        String diaryTitle = mypageFeed.getNick() + "님과" + mypageFeed.getGoods() + "을 거래했습니다";

                        getReviewWriteRes.setTitle(diaryTitle);
                        //feedImg(1)
                        if (getReviewWriteRes.getImageCount() != 0) {
                            String getDiaryImgQuery = "select image_url from Image_url where content_category = ? and content_idx = ? and image_order = 0";
                            String diaryImg = this.jdbcTemplate.queryForObject(getDiaryImgQuery, String.class, getReviewWriteRes.getBoarIdx(), getReviewWriteRes.getComuIdx());
                            getReviewWriteRes.setFeedImg(diaryImg);
                        } else {
                            getReviewWriteRes.setFeedImg(null);
                        }
                        //CountLike Comment
                        String getCountLikeQuery = "select count(*) from Diary_feed_like where diary_idx = ?";
                        int countLike = this.jdbcTemplate.queryForObject(getCountLikeQuery, int.class, getReviewWriteRes.getComuIdx());
                        String getCountComment = "select count(*) from Diary_comment where diary_idx = ?";
                        int countComment = this.jdbcTemplate.queryForObject(getCountComment, int.class, getReviewWriteRes.getComuIdx());

                        getReviewWriteRes.setCountComment(countComment);
                        getReviewWriteRes.setCountLike(countLike);
                        return getReviewWriteRes;
                    },
                    memIdx);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
    //이야기방 조회
    public List<GetComuWriteRes> getStoryWrite(int memIdx){
        try{
            String getStoryWriteQuery = "select board_idx,story_roomType,story_idx,story_title,story_hit,story_created_at,story_image from Story_feed where mem_idx = ?";

            return this.jdbcTemplate.query(getStoryWriteQuery,
                    (rs, rowNum) -> {
                        GetComuWriteRes getStoryWriteRes = new GetComuWriteRes(
                                rs.getInt("board_idx"),
                                rs.getInt("story_roomType"),
                                rs.getInt("review_idx"),
                                rs.getString("story_title"),
                                rs.getInt("review_hit"),
                                rs.getString("review_created_at"),
                                rs.getInt("review_image")
                        );
                        //feedImg(1)
                        if(getStoryWriteRes.getImageCount() != 0){
                            String getStoryImgQuery = "select image_url from Image_url where content_category = ? and content_idx = ? and image_order = 0";
                            String diaryImg = this.jdbcTemplate.queryForObject(getStoryImgQuery,String.class,getStoryWriteRes.getBoarIdx(),getStoryWriteRes.getComuIdx());
                            getStoryWriteRes.setFeedImg(diaryImg);
                        }else{
                            getStoryWriteRes.setFeedImg(null);
                        }
                        //CountLike Comment
                        String getCountLikeQuery = "select count(*) from Story_feed_like where story_idx = ?";
                        int countLike = this.jdbcTemplate.queryForObject(getCountLikeQuery,int.class,getStoryWriteRes.getComuIdx());
                        String getCountComment = "select count(*) from Story_feed_comment where story_idx = ?";
                        int countComment = this.jdbcTemplate.queryForObject(getCountComment,int.class,getStoryWriteRes.getComuIdx());

                        getStoryWriteRes.setCountComment(countComment);
                        getStoryWriteRes.setCountLike(countLike);
                        return getStoryWriteRes;
                    },
                    memIdx);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
