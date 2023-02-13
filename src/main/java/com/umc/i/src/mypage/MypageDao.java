package com.umc.i.src.mypage;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.mypage.model.Blame;
import com.umc.i.src.mypage.model.Count;
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
    // 일기장 조회
    public List<GetComuWriteRes> getDiaryWrite(int memIdx,int page){
        try {
            String getDiaryWriteQuery = "select D.board_idx, diary_idx as idx,diary_roomType as roomType,diary_title as title,diary_image as countImg,diary_hit as hit,diary_created_at as createAt\n" +
                    ",(select count(*) from Diary_feed_like Dfl where D.diary_idx = Dfl.diary_idx) as likeCount\n" +
                    ",(select count(*) from Diary_comment Dc where D.diary_idx = Dc.diary_idx) as commentCount\n" +
                    "from Diary_feed D\n" +
                    "where D.mem_idx = ? order by createAt desc limit ?,?";

            int startPoint = page-10;
            return this.jdbcTemplate.query(getDiaryWriteQuery,
                    (rs, rowNum) -> {
                        GetComuWriteRes getDiaryWriteRes = new GetComuWriteRes(
                                rs.getInt("D.board_idx"),
                                rs.getInt("roomType"),
                                rs.getInt("idx"),
                                rs.getString("title"),
                                rs.getString("countImg"),
                                rs.getInt("hit"),
                                rs.getInt("likeCount"),
                                rs.getInt("commentCount"),
                                rs.getString("createAt")
                        );
                        // //이미지 없을 경우
                        // if(rs.getInt("countImg") != 0){
                        //     String getDiaryImgQuery = "select image_url from Image_url where content_category = ? and content_idx = ? and image_order = 0";
                        //     String diaryImg = this.jdbcTemplate.queryForObject(getDiaryImgQuery,String.class,getDiaryWriteRes.getBoarIdx(),getDiaryWriteRes.getComuIdx());
                        //     getDiaryWriteRes.setFeedImg(diaryImg);
                        // }else{
                        //     getDiaryWriteRes.setFeedImg(null);
                        // }

                        return getDiaryWriteRes;
                    },
                    memIdx,startPoint,page);

        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    //전체 대상 조회
    public List<GetComuWriteRes> getAllWrite(int memIdx,int page,boolean like) throws BaseException {
        try{
            String getAllWriteQuery = "";
            if(like){
                getAllWriteQuery = "select board_idx, review_idx as idx,null as roomType,concat(sell_mem_idx,'님의 ',review_goods) as title,review_image as countImg,review_hit as hit,review_created_at as createAt\n" +
                        ",(select count(*) from Market_review_like where Mr.review_idx = market_re_idx) as likeCount\n" +
                        ",(select count(*) from Market_review_comment Mrc where Mr.review_idx = Mrc.review_idx) as commentCount\n" +
                        "from Market_review Mr join Market_review_like Mrl on Mr.review_idx = Mrl.market_re_idx\n" +
                        "where Mrl.mem_idx = ? group by Mrl.mrl_idx\n" +
                        "union\n" +
                        "select board_idx, S.story_idx as idx,story_roomType as roomType,story_title as title,story_image as countImg,story_hit as hit,story_created_at as createAt\n" +
                        ",(select count(*) from Story_feed_like SFL where S.story_idx = SFL.story_idx) as likeCount\n" +
                        ",(select count(*) from Story_feed_comment Sfc where S.story_idx = Sfc.story_idx) as commentCount\n" +
                        "from Story_feed S join Story_feed_like l on S.story_idx = l.story_idx\n" +
                        "where l.mem_idx = ? group by l.sfl_idx\n" +
                        "union\n" +
                        "select board_idx, D.diary_idx as idx,diary_roomType as roomType,diary_title as title,diary_image as countImg,diary_hit as hit,diary_created_at as createAt\n" +
                        ",(select count(*) from Diary_feed_like Dfl where D.diary_idx = Dfl.diary_idx) as likeCount\n" +
                        ",(select count(*) from Diary_comment Dc where D.diary_idx = Dc.diary_idx) as commentCount\n" +
                        "from Diary_feed D join Diary_feed_like l on D.diary_idx = l.diary_idx\n" +
                        "where l.mem_idx = ? group by l.dfl_idx order by createAt desc limit ?,?";
            }else{
                getAllWriteQuery = "select board_idx, review_idx as idx,null as roomType,concat(sell_mem_idx,'님의 ',review_goods) as title,review_image as countImg,review_hit as hit,review_created_at as createAt\n" +
                        ",(select count(*) from Market_review_like where Mr.review_idx = market_re_idx) as likeCount\n" +
                        ",(select count(*) from Market_review_comment Mrc where Mr.review_idx = Mrc.review_idx) as commentCount\n" +
                        "from Market_review Mr\n" +
                        "where buy_mem_idx = ?\n" +
                        "union\n" +
                        "select board_idx, story_idx as idx,story_roomType as roomType,story_title as title,story_image as countImg,story_hit as hit,story_created_at as createAt\n" +
                        ",(select count(*) from Story_feed_like SFL where S.story_idx = SFL.story_idx) as likeCount\n" +
                        ",(select count(*) from Story_feed_comment Sfc where S.story_idx = Sfc.story_idx) as commentCount\n" +
                        "from Story_feed S " +
                        "where S.mem_idx =? " +
                        "union " +
                        "select board_idx, diary_idx as idx,diary_roomType as roomType,diary_title as title,diary_image as countImg,diary_hit as hit,diary_created_at as createAt\n" +
                        ",(select count(*) from Diary_feed_like Dfl where D.diary_idx = Dfl.diary_idx) as likeCount\n" +
                        ",(select count(*) from Diary_comment Dc where D.diary_idx = Dc.diary_idx) as commentCount\n" +
                        "from Diary_feed D\n" +
                        "where D.mem_idx = ? order by createAt desc limit ?,?";
            }
            int startPoint = page-10;
            return this.jdbcTemplate.query(getAllWriteQuery,
                    (rs, rowNum) -> {
                        GetComuWriteRes getComuWriteRes = new GetComuWriteRes(
                                rs.getInt("board_idx"),
                                rs.getInt("roomType"),
                                rs.getInt("idx"),
                                rs.getString("title"),
                                rs.getString("countImg"),
                                rs.getInt("hit"),
                                rs.getInt("likeCount"),
                                rs.getInt("commentCount"),
                                rs.getString("createAt")
                        );
                        // //이미지 없을 경우
                        // if (rs.getInt("countImg") != 0) {
                        //     String getDiaryImgQuery = "select image_url from Image_url where content_category = ? and content_idx = ? and image_order = 0";
                        //     String img = this.jdbcTemplate.queryForObject(getDiaryImgQuery, String.class, getComuWriteRes.getBoarIdx(), getComuWriteRes.getComuIdx());
                        //     getComuWriteRes.setFeedImg(img);
                        // } else {
                        //     getComuWriteRes.setFeedImg(null);
                        // }
                        return getComuWriteRes;
                    }, memIdx, memIdx, memIdx,startPoint,page);
        }catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.GET_WRITE_FEED_EMPTY);
        }
    }

    //이야기방 장터 후기 대상 조회
    public List<GetComuWriteRes> getRSWrite(int memIdx,int page) throws BaseException {
        try {
            String getRSWriteQuery = "select board_idx, review_idx as idx,null as roomType,concat(sell_mem_idx,'님의 ',review_goods) as title,review_image as countImg,review_hit as hit,review_created_at as createAt\n" +
                    ",(select count(*) from Market_review_like where Mr.review_idx = market_re_idx and mrl_status = 1) as likeCount\n" +
                    ",(select count(*) from Market_review_comment Mrc where Mr.review_idx = Mrc.review_idx) as commentCount\n" +
                    "from Market_review Mr\n" +
                    "where buy_mem_idx = ?\n" +
                    "union\n" +
                    "select board_idx, story_idx as idx,story_roomType as roomType,story_title as title,story_image as countImg,story_hit as hit,story_created_at as createAt\n" +
                    ",(select count(*) from Story_feed_like SFL where S.story_idx = SFL.story_idx and sfl_status = 1) as likeCount\n" +
                    ",(select count(*) from Story_feed_comment Sfc where S.story_idx = Sfc.story_idx) as commentCount\n" +
                    "from Story_feed S\n" +
                    "where S.mem_idx =? order by createAt desc limit ?,?";
            int startPoint = page-10;
            return this.jdbcTemplate.query(getRSWriteQuery,
                    (rs, rowNum) -> {
                        GetComuWriteRes getComuWriteRes = new GetComuWriteRes(
                                rs.getInt("board_idx"),
                                rs.getInt("roomType"),
                                rs.getInt("idx"),
                                rs.getString("title"),
                                rs.getString("countImg"),
                                rs.getInt("hit"),
                                rs.getInt("likeCount"),
                                rs.getInt("commentCount"),
                                rs.getString("createAt")
                        );
                        // //이미지 없을 경우
                        // if (rs.getInt("countImg") != 0) {
                        //     String getDiaryImgQuery = "select image_url from Image_url where content_category = ? and content_idx = ? and image_order = 0";
                        //     String img = this.jdbcTemplate.queryForObject(getDiaryImgQuery, String.class, getComuWriteRes.getBoarIdx(), getComuWriteRes.getComuIdx());
                        //     getComuWriteRes.setFeedImg(img);
                        // } else {
                        //     getComuWriteRes.setFeedImg(null);
                        // }
                        return getComuWriteRes;
                    }, memIdx, memIdx,startPoint,page);
        }catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.GET_WRITE_FEED_FAILED);
        }
    }

    //나눔장터 조회
    public List<GetMarketWriteRes> getMarketWrite(int memIdx,int page) throws BaseException {
        try {
            String getMarketWriteQuery = "select board_idx,M.market_idx,market_title,market_soldout,market_image, " +
                    "(select count(*) from Market_like Ml where Ml.market_idx = M.market_idx and ml_status = 1) as reserveCount " +
                    "from Market M where mem_idx = ? order by market_created_at desc limit ?,?";
            int startPoint = page-10;
            return this.jdbcTemplate.query(getMarketWriteQuery,
                    (rs, rowNum) -> new GetMarketWriteRes(
                                rs.getInt("board_idx"),
                                rs.getInt("market_idx"),
                                rs.getString("market_title"),
                                rs.getString("market_image"),
                                rs.getInt("market_soldout"),
                                rs.getInt("reserveCount"))
                    ,memIdx,startPoint,page);
        }catch (EmptyResultDataAccessException e){
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.GET_WRITE_FEED_FAILED);
        }
    }
    //count
    public Integer getSWriteCount(int memIdx){
        String getSWriteCountQuery = "select count(*) from Story_feed where mem_idx = ?";
        return this.jdbcTemplate.queryForObject(getSWriteCountQuery,Integer.class,memIdx);
    }
    public Integer getRWriteCount(int memIdx){
        String getSWriteCountQuery = "select count(*) from Market_review where buy_mem_idx = ?";
        return this.jdbcTemplate.queryForObject(getSWriteCountQuery,Integer.class,memIdx);
    }
    public Integer getDWriteCount(int memIdx){
        String getSWriteCountQuery = "select count(*) from Diary_feed where mem_idx = ?";
        return this.jdbcTemplate.queryForObject(getSWriteCountQuery,Integer.class,memIdx);
    }
    public Integer getMWriteCount(int memIdx){
        String getSWriteCountQuery = "select count(*) from Market where mem_idx = ?";
        return this.jdbcTemplate.queryForObject(getSWriteCountQuery,Integer.class,memIdx);
    }

    /** ===================inquiryRecord================================= **/
    //작성한 댓글 조회
    public List<GetComentWriteRes> getComentWrite(int memIdx,int page)throws BaseException{
        try {
            String getComentWriteQuery = "select board_idx,S.story_idx as comuIdx,story_title as title, M.mem_nickname as writeNick,story_cmt_created_at as feedCreateAt,story_hit as hit,story_cmt_content as coment,story_cmt_created_at as comentCreateAt\n" +
                    "from Story_feed S join Story_feed_comment Sfc on S.story_idx = Sfc.story_idx\n" +
                    "    join Member M on M.mem_idx = Sfc.mem_idx\n" +
                    "where Sfc.mem_idx = ?\n" +
                    "union all\n" +
                    "select board_idx,mr.review_idx as comuIdx,concat(sell_mem_idx,'님의 ',review_goods) as title,m.mem_nickname as writenick,review_created_at as feedCreateAt,review_hit as hit,market_re_cmt_content as coment,market_re_cmt_create_at as comentCreateAt\n" +
                    "from Market_review as mr join Market_review_comment as mrc on mr.review_idx = mrc.review_idx left join Member m\n" +
                    "                                                                                                       on buy_mem_idx = m.mem_idx\n" +
                    "where mrc.mem_idx = ?\n" +
                    "union all\n" +
                    "select board_idx,df.diary_idx as comuIdx,diary_title as title,diary_cmt_created_at as feedCreateAt,m.mem_nickname as writeNick,diary_hit as hit,diary_cmt_content as coment,diary_cmt_created_at as comentCreateAt\n" +
                    "from Diary_feed as df join Diary_comment as dc\n" +
                    "                           on df.diary_idx = dc.diary_idx left join Member as m on m.mem_idx = df.mem_idx\n" +
                    "where dc.mem_idx = ? order by comentCreateAt desc limit ?,?";
            int startPoint = page-10;
            return this.jdbcTemplate.query(getComentWriteQuery,
                    (rs, rowNum) -> new GetComentWriteRes(
                            rs.getInt("board_idx"),
                            rs.getInt("comuIdx"),
                            rs.getString("coment"),
                            rs.getString("title"),
                            rs.getString("writeNick"),
                            rs.getString("feedCreateAt"),
                            rs.getInt("hit")
                    ),memIdx,memIdx,memIdx,startPoint,page);
        }catch (EmptyResultDataAccessException e){
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.GET_WRITE_FEED_FAILED);
        }
    }
    //count-댓글
    public Integer getComentWriteCount(int memIdx){
        String getComentWriteCountQuery = "select count(*) as S ,(select count(*) from Market_review Mr join Market_review_comment Mrc on Mr.review_idx = Mrc.review_idx where Mrc.mem_idx = ?) as Mr," +
                "(select count(*) as S from Diary_feed D join Diary_comment Dc on D.diary_idx = Dc.diary_idx where Dc.mem_idx = ?) as D" +
                " from Story_feed S join Story_feed_comment Sfc on S.story_idx = Sfc.story_idx where Sfc.mem_idx = ?";
        return this.jdbcTemplate.queryForObject(getComentWriteCountQuery,
                (rs,rowNum)-> {
                    Count count = new Count(
                            rs.getInt("S"),
                            rs.getInt("Mr"),
                            rs.getInt("D")
                    );
                    int result = count.getD()+count.getR()+count.getS();
                    return result;
                },memIdx,memIdx,memIdx);
    }
    //count-좋아요
    public Integer getLikeCount(int memIdx){
        String getLikeCountQuery = "select count(*) as Mr ,(select count(*) as S from Story_feed S join Story_feed_like l on S.story_idx = l.story_idx where S.mem_idx =?) as S," +
                "(select count(*) as D from Diary_feed D join Diary_feed_like l on D.diary_idx = l.diary_idx where D.mem_idx = ? ) as D " +
                " from Market_review Mr join Market_review_like Mrl on Mr.review_idx = Mrl.market_re_idx where buy_mem_idx = ?";

        return this.jdbcTemplate.queryForObject(getLikeCountQuery,
                (rs,rowNum)-> {
                    Count count = new Count(
                            rs.getInt("S"),
                            rs.getInt("Mr"),
                            rs.getInt("D")
                    );
                    int result = count.getD()+count.getR()+count.getS();
                    return result;
                },memIdx,memIdx,memIdx);
    }
    //나눔장터 - 찜한 게시글 개수
    public Integer getWantMarketCount(int memIdx){
        String getWantMarketCountQuery = "select count(*) from Market m join Market_like Ml on m.market_idx = Ml.market_idx where Ml.mem_idx = ? and ml_status = 1 group by m.market_idx";
        return this.jdbcTemplate.queryForObject(getWantMarketCountQuery,Integer.class,memIdx);
    }
    //나눔장터 - 찜한 게시글 조회
    public List<GetWantMarketRes> getWantMarket(int memIdx,int page) throws BaseException {
        try {
            String getWantFeedQuery = "select board_idx,m.market_idx,market_price,TIMEDIFF(market_created_at,CURRENT_TIMESTAMP()) as createAt ,market_hit,market_soldout,market_image,market_title\n" +
                    "from Market m join Market_like ml\n" +
                    "        on ml.market_idx = m.market_idx\n" +
                    "where ml.mem_idx = ? and ml_status = 1 group by m.market_idx limit ?,?";
            String getWantCountQuery = "select count(*) from Market_like where market_idx = ?";
            int startPoint = page-10;
            return this.jdbcTemplate.query(getWantFeedQuery,
                    (rs, rowNum) ->{
                        GetWantMarketRes getWantMarketRes = new GetWantMarketRes(
                                rs.getInt("board_idx"),
                                rs.getInt("m.market_idx"),
                                rs.getString("market_image"),
                                rs.getInt("market_price"),
                                rs.getString("market_title"),
                                rs.getString("createAt"),
                                rs.getInt("market_hit"),
                                rs.getInt("market_soldout")
                        );
                        int wantCount = this.jdbcTemplate.queryForObject(getWantCountQuery,int.class,getWantMarketRes.getComuIdx());
                        getWantMarketRes.setWantCount(wantCount);
                        return getWantMarketRes;
                    },memIdx,startPoint,page);
        }catch (EmptyResultDataAccessException e){
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.GET_WRITE_FEED_FAILED);
        }
    }
    //차단한 사용자 조회
    public List<GetBlockMemRes> getBlockMem(int memIdx) throws BaseException {
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
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.GET_WRITE_FEED_FAILED);
        }
    }
    //문의 하기
    public void postAsk(PostAskReq postAskReq){
        String postAskQuery = "insert into Complain(complain_title,complain_content,complain_email) VALUES (?,?,?)";
        this.jdbcTemplate.update(postAskQuery,postAskReq.getTitle(),postAskReq.getContent(),postAskReq.getEmail());
    }
    //내가 신고한 게시글 조회하기
    public List<GetBlameFeedRes> getBlameFeed(int memIdx){
        String getBlameQuery = "select story_roomType as roomType,mem_profile_url as profile,mem_nickname as nick,\n" +
                "       target_type,target_idx,TIMEDIFF(blame_time,CURRENT_TIMESTAMP())as createAt\n" +
                "from Story_feed S join Member M on S.mem_idx = M.mem_idx\n" +
                "join Blame B on S.story_idx = B.target_idx and S.board_idx = B.target_type\n" +
                "where B.mem_idx = ?\n" +
                "union all\n" +
                "select diary_roomType as roomType ,mem_profile_url as profile,M.mem_nickname as nick,\n" +
                "       target_type,target_idx,TIMEDIFF(blame_time,CURRENT_TIMESTAMP())as createAt\n" +
                "from Diary_feed D join Member M on D.mem_idx = M.mem_idx\n" +
                "join Blame B on D.diary_idx = B.target_idx and D.board_idx = B.target_type\n" +
                "where B.mem_idx = ?\n" +
                "union all\n" +
                "select null as roomType,mem_profile_url as profile,mem_nickname as nick,\n" +
                "       target_type,target_idx,TIMEDIFF(blame_time,CURRENT_TIMESTAMP())as createAt\n" +
                "from Market_review MR join Member M on MR.buy_mem_idx = M.mem_idx\n" +
                "join Blame B on MR.review_idx = B.target_idx and MR.board_idx = B.target_type\n" +
                "where B.mem_idx = ?\n" +
                "union all\n" +
                "select market_group as roomType,mem_profile_url as profile,mem_nickname as nick,\n" +
                "       target_type,target_idx,TIMEDIFF(blame_time,CURRENT_TIMESTAMP())as createAt\n" +
                "from Market join Member M on Market.mem_idx = M.mem_idx\n" +
                "join Blame B on Market.market_idx = B.target_idx and Market.board_idx = B.target_type\n" +
                "where B.mem_idx = ? order by createAt desc";

        return this.jdbcTemplate.query(getBlameQuery,
                (rs, rowNum) -> new GetBlameFeedRes(
                        rs.getInt("roomType"),
                        rs.getString("profile"),
                        rs.getString("nick"),
                        rs.getString("createAt")
                ),memIdx,memIdx,memIdx,memIdx);
    }
}
