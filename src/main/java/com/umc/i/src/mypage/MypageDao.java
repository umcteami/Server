package com.umc.i.src.mypage;

import com.umc.i.src.mypage.model.Blame;
import com.umc.i.src.mypage.model.MypageFeed;
import com.umc.i.src.mypage.model.get.*;
import com.umc.i.src.mypage.model.post.PostAskReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    public List<GetComuWriteRes> getDiaryWrite(int memIdx,boolean like){
        try {
            String getDiaryWriteQuery = "";
            if(like){
                getDiaryWriteQuery = "select df.board_idx,diary_roomType,df.diary_idx,diary_title,diary_hit,diary_created_at,diary_image from Diary_feed df join Diary_feed_like dl on df.diary_idx = dl.diary_idx where dl.mem_idx = ?";
            }else{
                getDiaryWriteQuery = "select board_idx,diary_roomType,diary_idx,diary_title,diary_hit,diary_created_at,diary_image from Diary_feed where mem_idx = ?";
            }
            return this.jdbcTemplate.query(getDiaryWriteQuery,
                    (rs, rowNum) -> {
                        GetComuWriteRes getDiaryWriteRes = new GetComuWriteRes(
                                rs.getInt("board_idx"),
                                rs.getInt("diary_roomType"),
                                rs.getInt("diary_idx"),
                                rs.getString("diary_title"),
                                rs.getInt("diary_hit"),
                                rs.getString("diary_created_at")
                        );
                        //이미지 없을 경우
                        if(rs.getInt("diary_image") != 0){
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
    public List<GetComuWriteRes> getReviewWrite(int memIdx,boolean like){
        try{
            String getReviewWriteQuery="";
            if(like){
                getReviewWriteQuery = "select board_idx,review_idx,review_hit,review_created_at,review_image from Market_review mr join Market_review_like mrl on mr.review_idx = mrl.market_re_idx where buy_mem_idx = ?";
            }else{
                getReviewWriteQuery = "select board_idx,review_idx,review_hit,review_created_at,review_image from Market_review where buy_mem_idx = ?";
            }
            return this.jdbcTemplate.query(getReviewWriteQuery,
                    (rs, rowNum) -> {
                        GetComuWriteRes getReviewWriteRes = new GetComuWriteRes(
                                rs.getInt("board_idx"),
                                rs.getInt("review_idx"),
                                rs.getInt("review_hit"),
                                rs.getString("review_created_at")
                        );
                        //title - 굿즈 차우 변경예정
                        String getReviewIdxQuery = "select review_goods,m.mem_nickname from Market_review join Member as m where board_idx = ? and review_idx = ? and sell_mem_idx = m.mem_idx and buy_mem_idx = ?";

                        MypageFeed mypageFeed = this.jdbcTemplate.queryForObject(getReviewIdxQuery,
                                (myrs, myrowNum) -> new MypageFeed(
                                        myrs.getString("review_goods"),
                                        myrs.getString("mem_nickname")), getReviewWriteRes.getBoarIdx(),getReviewWriteRes.getComuIdx(),memIdx);
                        String diaryTitle = mypageFeed.getNick() + "님과" + mypageFeed.getGoods() + "을 거래했습니다";

                        getReviewWriteRes.setTitle(diaryTitle);
                        //feedImg(1)
                        if (rs.getInt("review_image") != 0) {
                            String getDiaryImgQuery = "select image_url from Image_url where content_category = ? and content_idx = ? and image_order = 0";
                            String diaryImg = this.jdbcTemplate.queryForObject(getDiaryImgQuery, String.class, getReviewWriteRes.getBoarIdx(), getReviewWriteRes.getComuIdx());
                            getReviewWriteRes.setFeedImg(diaryImg);
                        } else {
                            getReviewWriteRes.setFeedImg(null);
                        }
                        String getCountLikeQuery = "select count(*) from Market_review_like where market_re_idx = ?";
                        int countLike = this.jdbcTemplate.queryForObject(getCountLikeQuery, int.class, getReviewWriteRes.getComuIdx());
                        String getCountComment = "select count(*) from Market_review_comment where review_idx = ?";
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
    public List<GetComuWriteRes> getStoryWrite(int memIdx,boolean like){
        try{
            String getStoryWriteQuery="";
            if(like){
                getStoryWriteQuery = "select board_idx,story_roomType,sf.story_idx,story_title,story_hit,story_created_at,story_image from Story_feed sf join Story_feed_like sl on sf.story_idx = sl.story_idx where sl.mem_idx = ?";
            }else{
                getStoryWriteQuery = "select board_idx,story_roomType,story_idx,story_title,story_hit,story_created_at,story_image from Story_feed where mem_idx = ?";
            }
            return this.jdbcTemplate.query(getStoryWriteQuery,
                    (rs, rowNum) -> {
                        GetComuWriteRes getStoryWriteRes = new GetComuWriteRes(
                                rs.getInt("board_idx"),
                                rs.getInt("story_roomType"),
                                rs.getInt("story_idx"),
                                rs.getString("story_title"),
                                rs.getInt("story_hit"),
                                rs.getString("story_created_at")
                        );
                        //feedImg(1)
                        if(rs.getInt("story_image") != 0){
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
    //나눔장터 조회
    public List<GetMarketWriteRes> getMarketWrite(int memIdx){
        try {
            String getMarketWriteQuery = "select board_idx,market_idx,market_title,market_soldout,market_goods,market_like_count,market_image " +
                    "from Market where mem_idx = ?";

            return this.jdbcTemplate.query(getMarketWriteQuery,
                    (rs, rowNum) -> {
                        GetMarketWriteRes getMarketWriteRes = new GetMarketWriteRes(
                                rs.getInt("board_idx"),
                                rs.getInt("market_idx"),
                                rs.getString("market_title"),
                                rs.getInt("market_soldout"),
                                rs.getInt("market_goods")
                        );
                        String getStoryImgQuery = "select image_url from Image_url where content_category = ? and content_idx = ? and image_order = 0";
                        String marketImg = this.jdbcTemplate.queryForObject(getStoryImgQuery,String.class,getMarketWriteRes.getBoarIdx(),getMarketWriteRes.getComuIdx());
                        getMarketWriteRes.setFeedImg(marketImg);

                        String getCountReserve = "select count(*) from Market_like where market_idx = ? and ml_status = 1 ";
                        int marketCountReserve = this.jdbcTemplate.queryForObject(getCountReserve,int.class,getMarketWriteRes.getComuIdx());
                        getMarketWriteRes.setCountReserve(marketCountReserve);

                        return getMarketWriteRes;
                    },
                    memIdx);
        }catch (EmptyResultDataAccessException e){
            e.printStackTrace();
            return null;
        }
    }
    /** ===================inquiryRecord================================= **/
    //작성한 댓글 조회-일기장
    public List<GetComentWriteRes> getComentDWrite(int memIdx){
        try {
            String getComentWriteQuery ="select df.board_idx,df.diary_idx,diary_title,df.mem_idx,diary_cmt_created_at,diary_hit,diary_cmt_content,m.mem_nickname " +
                    "from Diary_feed as df join Diary_comment as dc " +
                    "on df.diary_idx = dc.diary_idx left join Member as m on m.mem_idx = df.mem_idx " +
                    "where df.mem_idx = ?";

            return this.jdbcTemplate.query(getComentWriteQuery,
                    (rs, rowNum) -> {
                        GetComentWriteRes getComentWriteRes = new GetComentWriteRes(
                                rs.getInt("df.board_idx"),
                                rs.getInt("df.diary_idx"),
                                rs.getString("diary_cmt_content"),
                                rs.getString("diary_title"),
                                rs.getString("m.mem_nickname"),
                                rs.getString("diary_cmt_created_at"),
                                rs.getInt("diary_hit")
                        );

                        return getComentWriteRes;
                    },memIdx);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }
    //작성한 댓글 조회-이야기방
    public List<GetComentWriteRes> getComentSWrite(int memIdx){
        try {
            String getComentSWriteQuery ="select sf.board_idx,sf.story_idx,story_title,sf.mem_idx,story_cmt_created_at,story_hit,story_cmt_content,m.mem_nickname " +
                    "from Story_feed as sf join Story_feed_comment as sc " +
                    "on sf.story_idx = sc.story_idx left join Member as m on m.mem_idx = sf.mem_idx "+
                    "where sc.mem_idx = ?";

            return this.jdbcTemplate.query(getComentSWriteQuery,
                    (rs, rowNum) -> new GetComentWriteRes(
                                rs.getInt("sf.board_idx"),
                                rs.getInt("sf.story_idx"),
                                rs.getString("story_cmt_content"),
                                rs.getString("story_title"),
                                rs.getString("m.mem_nickname"),
                                rs.getString("story_cmt_created_at"),
                                rs.getInt("story_hit")
                        )
                    ,memIdx);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }
    //작성한 댓글 조회 - 장터후기
    public List<GetComentWriteRes> getComentRWrite(int memIdx){
        try {
            String getComentRWriteQuery ="select mr.board_idx,mr.review_idx,sell_mem_idx,buy_mem_idx,m.mem_nickname,review_created_at,review_hit,market_re_cmt_content,review_goods\n" +
                    "from Market_review as mr join Market_review_comment as mrc\n" +
                    "    on mr.review_idx = mrc.review_idx\n" +
                    "                         left join Member as m\n" +
                    "                             on buy_mem_idx = m.mem_idx\n" +
                    "where mrc.mem_idx = ?;";
            String getsellNickQuery = "select mem_nickname from Member where mem_idx = ?";
            return this.jdbcTemplate.query(getComentRWriteQuery,
                    (rs, rowNum) -> {
                        GetComentWriteRes getComentWriteRes = new GetComentWriteRes(
                                rs.getInt("mr.board_idx"),
                                rs.getInt("mr.review_idx"),
                                rs.getString("market_re_cmt_content"),
                                rs.getString("mem_nickname"),
                                rs.getString("review_created_at"),
                                rs.getInt("review_hit")
                        );
                        String sellNickParam = this.jdbcTemplate.queryForObject(getsellNickQuery,String.class,rs.getInt("sell_mem_idx"));

                        String title = sellNickParam+"님과 "+rs.getString("review_goods")+"을 거래했습니다";
                        getComentWriteRes.setFeedTitle(title);
                        return getComentWriteRes;
                    },memIdx);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    //나눔장터 - 찜한 게시글 조회
    public List<GetWantMarketRes> getWantMarket(int memIdx){
        try {
            String getWantFeedQuery = "select board_idx,m.market_idx,market_price,market_goods,TIMEDIFF(market_created_at,CURRENT_TIMESTAMP()) as createAt ,market_hit,market_soldout,\n" +
                    "       image_url " +
                    "from Market m join Image_url i\n" +
                    "    on m.board_idx = i.content_category and market_idx = content_idx\n" +
                    "    join Market_like ml\n" +
                    "        on ml.market_idx = m.market_idx\n" +
                    "where ml.mem_idx = ? and ml_status = 1";
            String getWantCountQuery = "select count(*) from Market_like where market_idx = ?";

            return this.jdbcTemplate.query(getWantFeedQuery,
                    (rs, rowNum) ->{
                        GetWantMarketRes getWantMarketRes = new GetWantMarketRes(
                                rs.getInt("board_idx"),
                                rs.getInt("m.market_idx"),
                                rs.getString("image_url"),
                                rs.getInt("market_price"),
                                rs.getInt("market_goods"),
                                rs.getString("createAt"),
                                rs.getInt("market_hit"),
                                rs.getInt("market_soldout")
                        );
                        int wantCount = this.jdbcTemplate.queryForObject(getWantCountQuery,int.class,getWantMarketRes.getComuIdx());
                        getWantMarketRes.setWantCount(wantCount);
                        return getWantMarketRes;
                    },memIdx);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }
    //신고한 게시글 조회
    //차단한 사용자 조회
    public List<GetBlockMemRes> getBlockMem(int memIdx){
        try {
            String getBlockMemQuery = "select blocked_mem_idx,mem_nickname,mem_profile_content,mem_profile_url\n" +
                    "from Member join Member_block Mb on Member.mem_idx = blocked_mem_idx where Mb.mem_idx = ?";
            return this.jdbcTemplate.query(getBlockMemQuery,
                    (rs, rowNum) -> new GetBlockMemRes(
                            rs.getInt("blocked_mem_idx"),
                            rs.getString("mem_profile_url"),
                            rs.getString("mem_nickname"),
                            rs.getString("mem_profile_content")
                    ),memIdx);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }
    //문의 하기
    public void postAsk(PostAskReq postAskReq){
        String postAskQuery = "insert into Complain(complain_title,complain_content,complain_email) VALUES (?,?,?)";
        this.jdbcTemplate.update(postAskQuery,postAskReq.getTitle(),postAskReq.getContent(),postAskReq.getEmail());
    }
    //내가 신고한 게시글 조회하기
    public List<GetBlameFeedRes> getBlameFeed(int memIdx){
        String getBlameQuery = "select target_type,target_idx,TIMEDIFF(blame_time,CURRENT_TIMESTAMP())as createAt from Blame where mem_idx = ?";

        String getBlameStoryQuery = "select story_roomType,mem_profile_url,mem_nickname from Story_feed S join Member M " +
                "    on S.mem_idx = M.mem_idx " +
                "where S.board_idx = 1 and S.story_idx = ?";
        String getBlameDiaryQuery = "select diary_roomType,mem_profile_url,M.mem_nickname from Diary_feed D join Member M" +
                "    on D.mem_idx = M.mem_idx\n" +
                "where D.board_idx = 2 and D.diary_idx = ?";

        String getBlameReviewQuery = "select mem_profile_url,mem_nickname from Market_review MR join Member M" +
                "    on MR.buy_mem_idx = M.mem_idx\n" +
                "where MR.board_idx = 3 and MR.review_idx = ?";

        /**룸타입 뭘로 표시할지 ? 일단 marekt_group 으로 표시**/
        String getBlameMarketQuery = "select market_group,mem_profile_url,mem_nickname from Market join Member M " +
                "    on Market.mem_idx = M.mem_idx\n" +
                "where Market.board_idx = 4 and Market.market_idx = ?";

        return this.jdbcTemplate.query(getBlameQuery,
                (rs, rowNum) -> {
                    Blame blame = new Blame(
                            rs.getInt("target_type"),
                            rs.getInt("target_idx"),
                            rs.getString("createAt")
                    );
                    GetBlameFeedRes getBlameFeedRes = null;
                    if(blame.getBoardIdx() == 1){
                       getBlameFeedRes = this.jdbcTemplate.queryForObject(getBlameStoryQuery,
                               (rs2,rowNum2) -> new GetBlameFeedRes(
                                       rs2.getInt("story_roomType"),
                                       rs2.getString("mem_profile_url"),
                                       rs2.getString("mem_nickname"),
                                       blame.getCreateAt()
                               )
                               ,blame.getComuIdx());
                    } else if (blame.getBoardIdx() == 2) {
                        getBlameFeedRes = this.jdbcTemplate.queryForObject(getBlameDiaryQuery,
                                (rs2,rowNum2) -> new GetBlameFeedRes(
                                        rs2.getInt("diary_roomType"),
                                        rs2.getString("mem_profile_url"),
                                        rs2.getString("mem_nickname"),
                                        blame.getCreateAt()
                                )
                                ,blame.getComuIdx());
                    } else if(blame.getBoardIdx() == 3){
                        getBlameFeedRes = this.jdbcTemplate.queryForObject(getBlameReviewQuery,
                            (rs2,rowNum2) -> new GetBlameFeedRes(
                                    rs2.getString("mem_profile_url"),
                                    rs2.getString("mem_nickname"),
                                    blame.getCreateAt()
                            )
                            ,blame.getComuIdx());
                    } else if(blame.getBoardIdx() == 4){
                        getBlameFeedRes = this.jdbcTemplate.queryForObject(getBlameMarketQuery,
                                (rs2,rowNum2) -> new GetBlameFeedRes(
                                        rs2.getInt("market_group"),
                                        rs2.getString("mem_profile_url"),
                                        rs2.getString("mem_nickname"),
                                        blame.getCreateAt()
                                )
                                ,blame.getComuIdx());
                    }

                    return getBlameFeedRes;
                },memIdx);
    }
}
