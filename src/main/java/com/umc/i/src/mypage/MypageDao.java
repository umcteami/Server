package com.umc.i.src.mypage;

import com.umc.i.src.member.model.get.GetMemRes;
import com.umc.i.src.mypage.model.MypageFeed;
import com.umc.i.src.mypage.model.get.GetComuWriteRes;
import com.umc.i.src.mypage.model.get.GetMypageMemRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class MypageDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private List<GetComuWriteRes> getComuWriteList = new ArrayList<>();

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
            getComuWriteList.add(this.jdbcTemplate.queryForObject(getDiaryWriteQuery,
                    (rs, rowNum) -> new GetComuWriteRes(
                            rs.getInt("board_idx"),
                            rs.getInt("diary_roomType"),
                            rs.getInt("diary_idx"),
                            rs.getString("diary_title"),
                            rs.getInt("diary_hit"),
                            rs.getString("diary_created_at"),
                            rs.getInt("diary_image")),
                    memIdx));
            //feedImg(1)
            for(int i =0;i<=getComuWriteList.size();i++){
                if(getComuWriteList.get(i).getImageCount() != 0){
                    String getDiaryImgQuery = "select image_url from Image_url where content_category = ? and content_idx = ? and image_order = 0";
                    String diaryImg = this.jdbcTemplate.queryForObject(getDiaryImgQuery,String.class,getComuWriteList.get(i).getBoarIdx(),getComuWriteList.get(i).getComuIdx());
                    getComuWriteList.get(i).setFeedImg(diaryImg);
                }else{
                    getComuWriteList.get(i).setFeedImg(null);
                }
                //CountLike Comment
                String getCountLikeQuery = "select count(*) from Diary_feed_like where diary_idx = ?";
                int countLike = this.jdbcTemplate.queryForObject(getCountLikeQuery,int.class,getComuWriteList.get(i).getComuIdx());
                String getCountComment = "select count(*) from Diary_comment where diary_idx = ?";
                int countComment = this.jdbcTemplate.queryForObject(getCountComment,int.class,getComuWriteList.get(i).getComuIdx());

                getComuWriteList.get(i).setCountComment(countComment);
                getComuWriteList.get(i).setCountLike(countLike);
            }
            //getComuWriteList.add(getDiaryWriteRes);
            log.info("{}","test");
            return getComuWriteList;

        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return null;
        }

    }
    public List<GetComuWriteRes> getReviewWrite(int memIdx){
        String getReviewWriteQuery = "select board_idx,review_idx,review_hit,review_created_at,review_image from Market_review where buy_mem_idx = ?";
        GetComuWriteRes getReviewWriteRes = this.jdbcTemplate.queryForObject(getReviewWriteQuery,
                (rs, rowNum) -> new GetComuWriteRes(
                        rs.getInt("board_idx"),
                        rs.getInt("diary_idx"),
                        rs.getInt("diary_hit"),
                        rs.getString("diary_created_at"),
                        rs.getInt("review_image")),
                memIdx);
        //roomType set
        getReviewWriteRes.setRoomType(0);
        //title - 굿즈 차우 변경예정
        String getReviewIdxQuery = "select review_goods,m.mem_nickname from Market_review join Member as m where sell_mem_idx = m.mem_idx and buy_mem_idx = ? limit 1";
        
        MypageFeed mypageFeed = this.jdbcTemplate.queryForObject(getReviewIdxQuery,
                (rs, rowNum) -> new MypageFeed(
                        rs.getString("review_goods"),
                        rs.getString("mem_nickname")),memIdx);
        String diaryTitle = mypageFeed.getNick()+"님과"+mypageFeed.getGoods()+"을 거래했습니다";

        getReviewWriteRes.setTitle(diaryTitle);
        //feedImg(1)
        if(getReviewWriteRes.getImageCount()!=0){
            String getDiaryImgQuery = "select image_url from Image_url where content_category = ? and content_idx = ? and image_order = 0";
            String diaryImg = this.jdbcTemplate.queryForObject(getDiaryImgQuery,String.class,getReviewWriteRes.getBoarIdx(),getReviewWriteRes.getComuIdx());
            getReviewWriteRes.setFeedImg(diaryImg);
        }else{
            getReviewWriteRes.setFeedImg(null);
        }
        //CountLike Comment
        String getCountLikeQuery = "select count(*) from Diary_feed_like where diary_idx = ?";
        int countLike = this.jdbcTemplate.queryForObject(getCountLikeQuery,int.class,getReviewWriteRes.getComuIdx());
        String getCountComment = "select count(*) from Diary_comment where diary_idx = ?";
        int countComment = this.jdbcTemplate.queryForObject(getCountComment,int.class,getReviewWriteRes.getComuIdx());

        getReviewWriteRes.setCountComment(countComment);
        getReviewWriteRes.setCountLike(countLike);

        getComuWriteList.add(getReviewWriteRes);
        return getComuWriteList;
    }
    //이야기방 조회
    public List<GetComuWriteRes> getStoryWrite(int memIdx){
        String getStoryWriteQuery = "select board_idx,story_idx,story_hit,story_created_at,story_image from Story_feed where mem_idx = ?";
        GetComuWriteRes getStoryWriteRes = this.jdbcTemplate.queryForObject(getStoryWriteQuery,
                (rs, rowNum) -> new GetComuWriteRes(
                        rs.getInt("board_idx"),
                        rs.getInt("story_idx"),
                        rs.getInt("story_hit"),
                        rs.getString("story_created_at"),
                        rs.getInt("story_image")),
                memIdx);
        //roomType set
        String getStoryRoomTypeQuery = "select story_roomType from Story_feed where mem_idx = ?";
        int storyRoomType = this.jdbcTemplate.queryForObject(getStoryRoomTypeQuery, int.class,memIdx);
        getStoryWriteRes.setRoomType(storyRoomType);
        //title
        String getStoryTitleQuery = "select story_title from Story_feed where mem_idx = ?";
        String storyTitle = this.jdbcTemplate.queryForObject(getStoryTitleQuery,String.class,memIdx);
        getStoryWriteRes.setTitle(storyTitle);
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
        getComuWriteList.add(getStoryWriteRes);
        return getComuWriteList;
    }

}
