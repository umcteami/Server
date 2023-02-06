package com.umc.i.src.feeds;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.feeds.model.Feeds;
import com.umc.i.src.feeds.model.get.GetAllFeedsRes;
import com.umc.i.src.feeds.model.get.GetCommentRes;
import com.umc.i.src.feeds.model.patch.PatchFeedsReq;
import com.umc.i.src.feeds.model.post.PostCommentReq;
import com.umc.i.src.feeds.model.post.PostFeedsReq;
import com.umc.i.utils.S3Storage.Image;
import com.umc.i.src.feeds.model.post.PostBlameReq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.sql.DataSource;

@Repository
public class FeedsDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 이야기방, 일기장 게시글 저장
    public int createFeeds(PostFeedsReq postFeedsReq) throws BaseException{
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = time.format(timeFormatter);

        String createFeedsQuery = null;

        try {
            switch(postFeedsReq.getBoardIdx()) {
                case 1: //이야기방
                    createFeedsQuery = "insert into Story_feed (board_idx, story_roomType, mem_idx, story_title, story_content, ";
                    createFeedsQuery += "story_image, story_hit, story_blame, story_created_at)";
                    createFeedsQuery += "values (1, ?, ?, ?, ?, ?, ?, ?, ?)";
                    break;
                case 2: //일기장
                    createFeedsQuery = "insert into Diary_feed (board_idx, diary_roomType, mem_idx, diary_title, diary_content, ";
                    createFeedsQuery += "diary_image, diary_hit, diary_blame, diary_created_at)";
                    createFeedsQuery += "values (2, ?, ?, ?, ?, ?, ?, ?, ?)";
                    break;
            }
    
            Object[] createFeedsParams = new Object[] {postFeedsReq.getRoomType(), postFeedsReq.getUserIdx(), postFeedsReq.getTitle(),
                                    postFeedsReq.getContent(), postFeedsReq.getImgCnt(), 0, 0, currentTime};
            this.jdbcTemplate.update(createFeedsQuery, createFeedsParams);  // 게시물 저장
    
            String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
            int feedsIdx = this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
            
            return feedsIdx;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.POST_FEEDS_UPLOAD_FAIL);
        }
    }

    // 이야기방, 일기장 이미지 정보 저장
    public void createFeedsImage(List<Image> img, int feedsIdx) {
        String createFeedsImageQuery = "insert into Image_url (content_category, content_idx, image_url, image_order)";
        createFeedsImageQuery += " values (?, ?, ?, ?)";
        for (int i = 0; i < img.size(); i++) {
            Object[] createFeedsImageParams = new Object[] {img.get(i).getCategory(), feedsIdx, img.get(i).getUploadFilePath(), i};
            this.jdbcTemplate.update(createFeedsImageQuery, createFeedsImageParams);
        }
    }

    // 이야기방, 일기장 게시글 수정
    public int editFeeds(PatchFeedsReq patchFeedsReq) throws BaseException {
        String editFeedsQuery = null;
        switch(patchFeedsReq.getBoardIdx()) {
            case 1: // 이야기방 수정
                editFeedsQuery = "update Story_feed set story_title=?, story_content=?, story_image=? where story_idx=?";
                break;
            case 2: // 일기장 수정
                editFeedsQuery = "update Diary_feed set diary_title=?, diary_content=?, diary_image=? where diary_idx=?";
                break;
        }

        try {
            Object[] editFeedsParams = new Object[] {patchFeedsReq.getTitle(), patchFeedsReq.getContent(), 
                patchFeedsReq.getImgCnt(), patchFeedsReq.getFeedsIdx()};
            this.jdbcTemplate.update(editFeedsQuery, editFeedsParams);
        } catch(Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.PATCH_EDIT_FEEDS_FAIL);
        }        
        
        return patchFeedsReq.getFeedsIdx();
    }

    // 이야기방, 일기장 이미지 정보 수정
    public void editFeedsImage(List<Image> img, int boardType, int feedsIdx) {
        String editFeedsImageQuery = "update Image_url set image_url=? where content_category=? && content_idx=? && image_order=?";
        Object[] editFeedsImageParams;
        int oldImgCnt = getFeedsImage(boardType, feedsIdx).size();

        if(oldImgCnt <= img.size()) {   // 이미지 교체, 추가
            int i;
            for(i = 0; i < img.size() && i < oldImgCnt; i++) {
                editFeedsImageParams = new Object[] {img.get(i).getUploadFilePath(), boardType, feedsIdx, i};
                this.jdbcTemplate.update(editFeedsImageQuery, editFeedsImageParams);
            }
            editFeedsImageQuery = "insert into Image_url (content_category, content_idx, image_url, image_order)";
            editFeedsImageQuery += " values (?, ?, ?, ?)";
            while(i < img.size()) {
                editFeedsImageParams = new Object[] {boardType, feedsIdx, img.get(i).getUploadFilePath(), i};
                this.jdbcTemplate.update(editFeedsImageQuery, editFeedsImageParams);
                i++;
            }
        } else {    // 이미지 교체, 삭제
            int i;
            for(i = 0; i < img.size(); i++) {
                editFeedsImageParams = new Object[] {img.get(i).getUploadFilePath(), boardType, feedsIdx, i};
                this.jdbcTemplate.update(editFeedsImageQuery, editFeedsImageParams);
            }
            editFeedsImageQuery = "delete from Image_url where content_category = ? && content_idx = ? && image_order = ?";
            while(i < oldImgCnt) {
                editFeedsImageParams = new Object[] {boardType, feedsIdx, i};
                this.jdbcTemplate.update(editFeedsImageQuery, editFeedsImageParams);
                i++;
            }
        }
    }

    // 이야기방, 일기장 이미지 조회
    public List<Image> getFeedsImage(int boardType, int feedsIdx) {
        String getFeedsImageQuery = "select * from Image_url where content_category = ? && content_idx = ?";
        Object[] getFeedsImageParams = new Object[] {boardType, feedsIdx};
        
        return this.jdbcTemplate.query(getFeedsImageQuery, 
        (rs, rowNum) -> new Image(
            rs.getString("image_url"), 
            rs.getString("image_url"),
            rs.getInt("content_category"), 
            rs.getInt("content_idx")), 
            getFeedsImageParams);
    }
        

    // 이야기방, 일기장 게시글 삭제
    public void deleteFeeds(int boardType, int feedsIdx) throws BaseException {
        String deleteFeedsQuery = null;

        try {  
            // feed table 삭제
            if(boardType == 1) {    // 이야기방
                deleteFeedsQuery = "delete from Story_feed where story_idx = ?";
            } else if(boardType == 2) {     // 일기장
                deleteFeedsQuery = "delete from Diary_feed where diary_idx = ?";
            }
            this.jdbcTemplate.update(deleteFeedsQuery, feedsIdx);   

            // image_url table 삭제
            deleteFeedsQuery = "delete from Image_url where content_category = ? && content_idx = ?";
            Object[] deleteFeedsParams = new Object[] {boardType, feedsIdx};
            this.jdbcTemplate.update(deleteFeedsQuery, deleteFeedsParams);

        } catch (Exception e) {
            e.printStackTrace();
             throw new BaseException(BaseResponseStatus.PATCH_DELETE_FEEDS_FAIL);
        }
        return;
    }


    // 이야기방 전체 조회
    public List<GetAllFeedsRes> getAllStories() {
        String getAllFeedsQuery = "select S.story_idx, story_roomType, S.mem_idx, mem_nickname, story_title, story_hit, story_created_at, ";
        getAllFeedsQuery += " if(S.story_idx = Cmt.story_idx, comment_cnt, 0) as comment_cnt";
        getAllFeedsQuery += " from Story_feed S, Member M, (select story_idx, count(*) as comment_cnt from Story_feed_comment group by story_idx) Cmt";
        getAllFeedsQuery += " where S.mem_idx = M.mem_idx && story_blame < 10 group by story_idx order by story_idx desc limit 20 offset 0";

        return this.jdbcTemplate.query(getAllFeedsQuery, 
        (rs, rowNum) -> new GetAllFeedsRes(
            1, 
            rs.getInt("story_roomType"), 
            rs.getInt("story_idx"), 
            rs.getInt("mem_idx"),
            rs.getString("mem_nickname"),
            rs.getString("story_title"), 
            rs.getInt("story_hit"),
            rs.getInt("comment_cnt"),
            rs.getString("story_created_at")));
    }
    
    // 이야기방 카테고리별 조회
    public List<GetAllFeedsRes> getStoryRoomType(int roomType) {
        String getAllFeedsQuery = "select S.story_idx, story_roomType, S.mem_idx, mem_nickname, story_title, story_hit, story_created_at, ";
        getAllFeedsQuery += " if(S.story_idx = Cmt.story_idx, comment_cnt, 0) as comment_cnt";
        getAllFeedsQuery += " from Story_feed S, Member M, (select story_idx, count(*) as comment_cnt from Story_feed_comment group by story_idx) Cmt";
        getAllFeedsQuery += " where story_roomType = ? && S.mem_idx = M.mem_idx && story_blame < 10 group by story_idx order by story_idx desc limit 20 offset 0";

        return this.jdbcTemplate.query(getAllFeedsQuery, 
        (rs, rowNum) -> new GetAllFeedsRes(
            1, 
            rs.getInt("story_roomType"), 
            rs.getInt("story_idx"), 
            rs.getInt("mem_idx"),
            rs.getString("mem_nickname"),
            rs.getString("story_title"), 
            rs.getInt("story_hit"),
            rs.getInt("comment_cnt"),
            rs.getString("story_created_at")),
            roomType);
    }

    // 이야기방 상세 조회
    public List<Feeds> getStory(int feedIdx) {
        String getFeedQuery = "update Story_feed set story_hit = story_hit + 1";
        this.jdbcTemplate.update(getFeedQuery);
        
        getFeedQuery = "select S.story_idx, story_roomType, S.mem_idx, mem_nickname, story_title, story_content, story_hit, story_created_at, ";
        getFeedQuery += " if(S.story_idx = Cmt.story_idx, comment_cnt, 0) as comment_cnt";
        getFeedQuery += " from Story_feed S, Member M, (select story_idx, count(*) as comment_cnt from Story_feed_comment group by story_idx) Cmt";
        getFeedQuery += " where S.story_idx = ? && S.mem_idx = M.mem_idx && story_blame < 10";

        return this.jdbcTemplate.query(getFeedQuery, 
        (rs, rowNum) -> new Feeds(
            1, 
            rs.getInt("story_roomType"), 
            rs.getInt("story_idx"), 
            rs.getInt("mem_idx"),
            rs.getString("mem_nickname"),
            rs.getString("story_title"), 
            rs.getString("story_content"),
            rs.getInt("story_hit"),
            rs.getInt("comment_cnt"),
            rs.getString("story_created_at")),
            feedIdx);
    }

    // 일기장 전체 조회
    public List<GetAllFeedsRes> getAllDiaries() {
        String getAllFeedsQuery = "select D.diary_idx, diary_roomType, D.mem_idx, mem_nickname, diary_title, diary_hit, diary_created_at, ";
        getAllFeedsQuery += " if(D.diary_idx = Cmt.diary_idx, comment_cnt, 0) as comment_cnt";
        getAllFeedsQuery += " from Diary_feed D, (select diary_idx, count(*) as comment_cnt from Diary_comment group by diary_idx) Cmt";
        getAllFeedsQuery += " where diary_blame < 10 group by diary_idx order by diary_idx desc limit 20 offset 0";

        return this.jdbcTemplate.query(getAllFeedsQuery, 
        (rs, rowNum) -> new GetAllFeedsRes(
            2, 
            rs.getInt("diary_roomType"), 
            rs.getInt("diary_idx"), 
            rs.getInt("mem_idx"),
            rs.getString("mem_nickname"),
            rs.getString("diary_title"), 
            rs.getInt("diary_hit"),
            rs.getInt("comment_cnt"),
            rs.getString("diary_created_at")));
    }

    // 일기장 카테고리별 조회
    public List<GetAllFeedsRes> getDiariesByRoomType(int roomType) {
        String getAllFeedsQuery = "select D.diary_idx, diary_roomType, D.mem_idx, mem_nickname, diary_title, diary_hit, diary_created_at, ";
        getAllFeedsQuery += " if(D.diary_idx = Cmt.diary_idx, comment_cnt, 0) as comment_cnt";
        getAllFeedsQuery += " from Diary_feed D, (select diary_idx, count(*) as comment_cnt from Diary_comment group by diary_idx) Cmt";
        getAllFeedsQuery += " where diary_roomType = ? && diary_blame < 10 group by diary_idx order by diary_idx desc limit 20 offset 0";

        return this.jdbcTemplate.query(getAllFeedsQuery, 
        (rs, rowNum) -> new GetAllFeedsRes(
            2, 
            rs.getInt("diary_roomType"), 
            rs.getInt("diary_idx"), 
            rs.getInt("mem_idx"),
            rs.getString("mem_nickname"),
            rs.getString("diary_title"), 
            rs.getInt("diary_hit"),
            rs.getInt("comment_cnt"),
            rs.getString("diary_created_at")),
            roomType);
    }

    // 일기장 상세조회
    public List<Feeds> getDiary(int diaryIdx) {
        String getFeedQuery = "update Diary_feed set diary_hit = diary_hit + 1";
        this.jdbcTemplate.update(getFeedQuery);

        getFeedQuery = "select D.diary_idx, diary_roomType, D.mem_idx, mem_nickname, diary_title, diary_content, diary_hit, diary_created_at, ";
        getFeedQuery += " if(D.diary_idx = Cmt.diary_idx, comment_cnt, 0) as comment_cnt";
        getFeedQuery += " from Diary_feed D, (select diary_idx, count(*) as comment_cnt from Diary_comment group by diary_idx) Cmt";
        getFeedQuery += " where D.diary_idx = ? && diary_blame < 10";

        return this.jdbcTemplate.query(getFeedQuery, 
        (rs, rowNum) -> new Feeds(
            2, 
            rs.getInt("diary_roomType"), 
            rs.getInt("diary_idx"), 
            rs.getInt("mem_idx"),
            rs.getString("mem_nickname"),
            rs.getString("diary_title"), 
            rs.getString("diary_content"),
            rs.getInt("diary_hit"),
            rs.getInt("comment_cnt"),
            rs.getString("diary_created_at")),
            diaryIdx);
    }

    // 좋아요, 좋아요 취소
    public void changeLikeStory(int memIdx, int storyIdx) {     // 이야기방
        String postFeedsLikeQuery = "insert into Story_feed_like (story_idx, mem_idx, sfl_status) values (?, ?, 1) ";
        postFeedsLikeQuery += " on duplicate key update story_idx = ?, mem_idx = ?, sfl_status = !sfl_status";
        this.jdbcTemplate.update(postFeedsLikeQuery, storyIdx, memIdx, storyIdx, memIdx);

        return;
    }

    public void changeLikeDiary(int memIdx, int diaryIdx) {     // 일기장
        String postFeedsLikeQuery = "insert into Diary_feed_like (diary_idx, mem_idx, dfl_status) values (?, ?, 1) ";
        postFeedsLikeQuery += " on duplicate key update diary_idx = ?, mem_idx = ?, dfl_status = !dfl_status";
        this.jdbcTemplate.update(postFeedsLikeQuery, diaryIdx, memIdx, diaryIdx, memIdx);

        return;
    }

    public void changeLikeReview(int memIdx, int reviewIdx) {     // 장터후기
        String postFeedsLikeQuery = "insert into Market_review_like (market_re_idx, mem_idx, mrl_status) values (?, ?, 1) ";
        postFeedsLikeQuery += " on duplicate key update market_re_idx = ?, mem_idx = ?, mrl_status = !mrl_status";
        this.jdbcTemplate.update(postFeedsLikeQuery, reviewIdx, memIdx, reviewIdx, memIdx);

        return;
    }

    // 댓글 작성
    public void writeStoryComment(PostCommentReq postCommentReq) throws BaseException{  // 이야기방
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = time.format(timeFormatter);

        String postCommentQuery;
        String nickname;
        try {
            postCommentQuery = "select mem_nickname from Member where mem_idx = ?";
            nickname = this.jdbcTemplate.queryForObject(postCommentQuery, String.class, postCommentReq.getMemIdx());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.POST_INVALID_IDX);
        }
        
        try {
            postCommentQuery = "insert into Story_feed_comment (story_idx, mem_idx, mem_nickname, story_parent_idx, story_cmt_content, story_cmt_created_at) ";
            postCommentQuery += " values (?, ?, ?, ?, ?, ?)";
            Object[] postCommentParams = new Object[] {postCommentReq.getFeedIdx(), postCommentReq.getMemIdx(), nickname, postCommentReq.getParentCmt(), postCommentReq.getComment(), currentTime};
    
            this.jdbcTemplate.update(postCommentQuery, postCommentParams);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.POST_COMMENTS_UPLOAD_FAIL);
        }

        return;
    }

    public void writeDiaryComment(PostCommentReq postCommentReq) throws BaseException{  // 일기장
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = time.format(timeFormatter);

        String postCommentQuery;
        String nickname;
        try {
            postCommentQuery = "select mem_nickname from Member where mem_idx = ?";
            nickname = this.jdbcTemplate.queryForObject(postCommentQuery, String.class, postCommentReq.getMemIdx());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.POST_INVALID_IDX);
        }
        
        try {
            postCommentQuery = "insert into Diary_comment (diary_idx, mem_idx, mem_nickname, diary_parent_idx, diary_cmt_content, diary_cmt_created_at) ";
            postCommentQuery += " values (?, ?, ?, ?, ?, ?)";
            Object[] postCommentParams = new Object[] {postCommentReq.getFeedIdx(), postCommentReq.getMemIdx(), nickname, postCommentReq.getParentCmt(), postCommentReq.getComment(), currentTime};
    
            this.jdbcTemplate.update(postCommentQuery, postCommentParams);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.POST_COMMENTS_UPLOAD_FAIL);
        }

        return;
    }

    public void writeReviewComment(PostCommentReq postCommentReq) throws BaseException{  // 이야기방
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = time.format(timeFormatter);

        String postCommentQuery;
        String nickname;
        try {
            postCommentQuery = "select mem_nickname from Member where mem_idx = ?";
            nickname = this.jdbcTemplate.queryForObject(postCommentQuery, String.class, postCommentReq.getMemIdx());
            System.out.println(nickname);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.POST_INVALID_IDX);
        }
        
        try {
            postCommentQuery = "insert into Market_review_comment (review_idx, mem_idx, mem_nickname, market_re_parent_idx, market_re_cmt_content, market_re_cmt_create_at) ";
            postCommentQuery += " values (?, ?, ?, ?, ?, ?)";
            Object[] postCommentParams = new Object[] {postCommentReq.getFeedIdx(), postCommentReq.getMemIdx(), nickname, postCommentReq.getParentCmt(), postCommentReq.getComment(), currentTime};
    
            this.jdbcTemplate.update(postCommentQuery, postCommentParams);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.POST_COMMENTS_UPLOAD_FAIL);
        }

        return;
    }
    
    // 댓글 조회
    public List<GetCommentRes> getComments(int boardType, int feedIdx) throws BaseException {
        String getCommentQuery;
        switch (boardType) {
            case 1:     // 이야기방
                getCommentQuery = "select * from Story_feed_comment where story_idx = ?";
                return this.jdbcTemplate.query(getCommentQuery, 
                (rs, rowNum) -> new GetCommentRes(
                    rs.getInt("story_cmt_idx"), 
                    1, feedIdx,
                    rs.getInt("story_parent_idx"),
                    rs.getInt("mem_idx"),
                    rs.getString("mem_nickname"),
                    rs.getString("story_cmt_content"),
                    rs.getString("story_cmt_created_at")), feedIdx);
            case 2:     // 일기장
                getCommentQuery = "select * from Diary_comment where diary_idx = ?";
                 return this.jdbcTemplate.query(getCommentQuery, 
                    (rs, rowNum) -> new GetCommentRes(
                    rs.getInt("diary_cmt_idx"), 
                    2, feedIdx,
                    rs.getInt("diary_parent_idx"),
                    rs.getInt("mem_idx"),
                    rs.getString("mem_nickname"),
                    rs.getString("diary_cmt_content"),
                    rs.getString("diary_cmt_created_at")), feedIdx);
            case 3:     // 장터후기
                getCommentQuery = "select * from Market_review_comment where review_idx = ?";
                return this.jdbcTemplate.query(getCommentQuery, 
                (rs, rowNum) -> new GetCommentRes(
                    rs.getInt("market_re_cmt_idx"), 
                    3, feedIdx,
                    rs.getInt("market_re_parent_idx"),
                    rs.getInt("mem_idx"),
                    rs.getString("mem_nickname"),
                    rs.getString("market_re_cmt_content"),
                    rs.getString("market_re_cmt_create_at")), feedIdx);
        }
        throw new BaseException(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
    }



    // 통합 조회 -> 최신순
    public List<GetAllFeedsRes> getAllFeeds() {
        String getAllFeedsQuery = "select 1 as boardType, S.story_idx as feedIdx, story_roomType as roomType, S.mem_idx, mem_nickname, story_title as title, story_hit as hit, story_created_at as createAt, if(S.story_idx = Cmt.story_idx, comment_cnt, 0) as comment_cnt ";
        getAllFeedsQuery += " from Story_feed S, Member M, (select story_idx, count(*) as comment_cnt from Story_feed_comment group by story_idx) Cmt";
        getAllFeedsQuery += " where S.mem_idx = M.mem_idx && S.story_blame < 10 group by S.story_idx UNION";
        getAllFeedsQuery += " select 2 as boardType, D.diary_idx as feedIdx, diary_roomType as roomType, D.mem_idx, mem_nickname, diary_title as title, diary_hit as hit, diary_created_at as createAt, if(D.diary_idx = Cmt.diary_idx, comment_cnt, 0) as comment_cnt ";
        getAllFeedsQuery += " from Diary_feed D, (select diary_idx, count(*) as comment_cnt from Diary_comment group by diary_idx) Cmt";
        getAllFeedsQuery += " where D.diary_blame < 10 group by D.diary_idx UNION";
        getAllFeedsQuery += " select 3 as boardType, review_idx as feedIdx, null as roomType, buy_mem_idx as mem_idx, B.mem_nickname as mem_nickname, concat(A.mem_nickname, '님과 ', I.Market_review.review_goods, ' 을 거래했습니다.') title, review_hit as hit, review_created_at as createAt, 0 as comment_cnt";
        getAllFeedsQuery += " from Market_review, Member A, Member B where Market_review.sell_mem_idx = A.mem_idx && Market_review.buy_mem_idx = B.mem_idx && Market_review.review_blame < 10";
        getAllFeedsQuery += " order by createAt desc limit 20 offset 0 ";

        return this.jdbcTemplate.query(getAllFeedsQuery, 
        (rs, rowNum) -> new GetAllFeedsRes(
            rs.getInt("boardType"),
            rs.getInt("roomType"),
            rs.getInt("feedIdx"),
            rs.getInt("mem_idx"),
            rs.getString("mem_nickname"),
            rs.getString("title"),
            rs.getInt("hit"),
            rs.getInt("comment_cnt"),
            rs.getString("createAt")
        ));

    //게시글 신고하기
    public int postBlame(PostBlameReq postBlameReq) {
        String doubleProtectQuery = "select count(*) from Blame where mem_idx = ? and target_type = ? and target_idx = ?";
        int doubleProtect = this.jdbcTemplate.queryForObject(doubleProtectQuery,int.class,postBlameReq.getMemIdx(),postBlameReq.getBoardIdx(),postBlameReq.getComuIdx());

        if(doubleProtect == 0){
            String postBlameQuery = "insert into Blame(mem_idx,target_type,target_idx,blame_time)VALUES(?,?,?,now())";
            this.jdbcTemplate.update(postBlameQuery,postBlameReq.getMemIdx(),postBlameReq.getBoardIdx(),postBlameReq.getComuIdx());
            return doubleProtect;
        }
        return doubleProtect;
    }
}
