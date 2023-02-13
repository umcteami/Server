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
import com.umc.i.src.feeds.model.get.GetAllDiaryRes;
import com.umc.i.src.feeds.model.get.GetAllFeedsRes;
import com.umc.i.src.feeds.model.get.GetCommentRes;
import com.umc.i.src.feeds.model.patch.PatchFeedsReq;
import com.umc.i.src.feeds.model.post.PostCommentReq;
import com.umc.i.src.feeds.model.post.PostFeedsReq;
import com.umc.i.utils.S3Storage.Image;
import com.umc.i.src.feeds.model.post.PostBlameReq;

@Repository
public class FeedsDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 멤버 닉네임 조회
    public String getNickname(int memIdx) throws BaseException {
        try {
            String getNicknameQuery = "select mem_nickname from Member where mem_idx = ?";
            return this.jdbcTemplate.queryForObject(getNicknameQuery, String.class, memIdx);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.POST_AUTH_MEMBER_NOT_EXIST);
        }
    }

    // 이야기방, 일기장 게시글 저장
    public int createFeeds(PostFeedsReq postFeedsReq, List<Image> img) throws BaseException{
        String createFeedsQuery = null;
        int feedsIdx;
        try {
            String nickname = getNickname(postFeedsReq.getUserIdx());
        } catch (BaseException e) {
            throw e;
        }

        try {
            switch(postFeedsReq.getBoardIdx()) {
                case 1: //이야기방
                    createFeedsQuery = "insert into Story_feed (board_idx, story_roomType, mem_idx, story_title, story_content, ";
                    createFeedsQuery += "story_image, story_hit, story_blame)";
                    createFeedsQuery += "values (1, ?, ?, ?, ?, ?, ?, ?)";
                    break;
                case 2: //일기장
                    createFeedsQuery = "insert into Diary_feed (board_idx, diary_roomType, mem_idx, diary_title, diary_content, ";
                    createFeedsQuery += "diary_image, diary_hit, diary_blame)";
                    createFeedsQuery += "values (2, ?, ?, ?, ?, ?, ?, ?)";
                    break;
            }

            if(img == null) {
                Object[] createFeedsParams = new Object[] {postFeedsReq.getRoomType(), postFeedsReq.getUserIdx(), 
                    postFeedsReq.getTitle(), postFeedsReq.getContent(), null, 0, 0};
                this.jdbcTemplate.update(createFeedsQuery, createFeedsParams);  // 게시물 저장  

                String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
                feedsIdx = this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
            } else {
                Object[] createFeedsParams = new Object[] {postFeedsReq.getRoomType(), postFeedsReq.getUserIdx(), 
                    postFeedsReq.getTitle(), postFeedsReq.getContent(), img.get(0).getUploadFilePath(), 0, 0};
                this.jdbcTemplate.update(createFeedsQuery, createFeedsParams);  // 게시물 저장  

                String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
                feedsIdx = this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
                createFeedsImage(img, feedsIdx);
            }
            
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
    public int editFeeds(PatchFeedsReq patchFeedsReq, List<Image> img) throws BaseException {
        String editFeedsQuery = null;
        switch(patchFeedsReq.getBoardType()) {
            case 1: // 이야기방 수정
                editFeedsQuery = "update Story_feed set story_title=?, story_content=?, story_image=? where story_idx=?";
                break;
            case 2: // 일기장 수정
                editFeedsQuery = "update Diary_feed set diary_title=?, diary_content=?, diary_image=? where diary_idx=?";
                break;
        }

        try {
            if(img == null) {
                Object[] editFeedsParams = new Object[] {patchFeedsReq.getTitle(), patchFeedsReq.getContent(), 
                    null, patchFeedsReq.getFeedsIdx()};
                this.jdbcTemplate.update(editFeedsQuery, editFeedsParams);
            } else {
                Object[] editFeedsParams = new Object[] {patchFeedsReq.getTitle(), patchFeedsReq.getContent(), 
                    img.get(0).getUploadFilePath(), patchFeedsReq.getFeedsIdx()};
                this.jdbcTemplate.update(editFeedsQuery, editFeedsParams);
            }
            
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
    public List<GetAllFeedsRes> getAllStories(int page) throws BaseException {
        try {
            String getAllFeedsQuery = "select S.story_idx, story_roomType, S.mem_idx, M.mem_nickname, mem_profile_url, story_title, story_image, story_hit, story_created_at, ";
            getAllFeedsQuery += " if(S.story_idx = Cmt.story_idx, comment_cnt, 0) as comment_cnt, if(S.story_idx = LikeCnt.story_idx, like_cnt, 0) as like_cnt ";
            getAllFeedsQuery += " from Story_feed S, Member M, (select story_idx, count(*) as comment_cnt from Story_feed_comment group by story_idx) Cmt, (select story_idx, count(*) as like_cnt from Story_feed_like group by story_idx) LikeCnt";
            getAllFeedsQuery += " where story_blame < 10 && S.mem_idx = M.mem_idx group by story_idx order by story_idx desc limit 20 offset ?";
    
            return this.jdbcTemplate.query(getAllFeedsQuery, 
            (rs, rowNum) -> new GetAllFeedsRes(
                1, 
                rs.getInt("story_roomType"), 
                rs.getInt("story_idx"), 
                rs.getInt("mem_idx"),
                rs.getString("mem_nickname"),
                rs.getString("mem_profile_url"),
                rs.getString("story_title"), 
                rs.getString("story_image"),
                rs.getInt("story_hit"),
                rs.getInt("comment_cnt"),
                rs.getInt("like_cnt"),
                rs.getString("story_created_at")),
                page);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.GET_REVIEW_FAIL);
        }
        
    }
    
    // 이야기방 카테고리별 조회
    public List<GetAllFeedsRes> getStoryRoomType(int roomType, int page) {
        String getAllFeedsQuery = "select S.story_idx, story_roomType, S.mem_idx, M.mem_nickname, mem_profile_url, story_title, story_image, story_hit, story_created_at, ";
        getAllFeedsQuery += " if(S.story_idx = Cmt.story_idx, comment_cnt, 0) as comment_cnt, if(S.story_idx = LikeCnt.story_idx, like_cnt, 0) as like_cnt";
        getAllFeedsQuery += " from Story_feed S, Member M, (select story_idx, count(*) as comment_cnt from Story_feed_comment group by story_idx) Cmt, (select story_idx, count(*) as like_cnt from Story_feed_like group by story_idx) LikeCnt";
        getAllFeedsQuery += " where story_roomType = ? && story_blame < 10 && S.mem_idx = M.mem_idx group by story_idx order by story_idx desc limit 20 offset ?";

        return this.jdbcTemplate.query(getAllFeedsQuery, 
        (rs, rowNum) -> new GetAllFeedsRes(
            1, 
            rs.getInt("story_roomType"), 
            rs.getInt("story_idx"), 
            rs.getInt("mem_idx"),
            rs.getString("mem_nickname"),
            rs.getString("mem_profile_url"),
            rs.getString("story_title"), 
            rs.getString("story_image"),
            rs.getInt("story_hit"),
            rs.getInt("comment_cnt"),
            rs.getInt("like_cnt"),
            rs.getString("story_created_at")),
            roomType, page);
    }

    // 이야기방 상세 조회
    public List<Feeds> getStory(int feedIdx, int memIdx) {
        String getFeedQuery = "update Story_feed set story_hit = story_hit + 1 where story_idx = ?";
        this.jdbcTemplate.update(getFeedQuery, feedIdx);
        
        getFeedQuery = "select S.story_idx, story_roomType, S.mem_idx, M.mem_nickname, mem_profile_url, story_title, story_content, story_hit, story_created_at, SC.comment_cnt, like_cnt, islike";
        getFeedQuery += " from Story_feed S join Member M on S.mem_idx = M.mem_idx, (select count(*) as comment_cnt from Story_feed_comment where story_idx = ?) SC, ";
        getFeedQuery += " (select count(*) as like_cnt from Story_feed_like where story_idx = ? && sfl_status = 1) SFL, (select count(*) as islike from Story_feed_like where story_idx = ? && sfl_status = 1 && Story_feed_like.mem_idx = ?) SFisLike";
        getFeedQuery += " where S.story_idx = ? && S.story_blame < 10";

        return this.jdbcTemplate.query(getFeedQuery, 
        (rs, rowNum) -> new Feeds(
            1, 
            rs.getInt("story_roomType"), 
            rs.getInt("story_idx"), 
            rs.getInt("mem_idx"),
            rs.getString("mem_nickname"),
            rs.getString("mem_profile_url"),
            rs.getString("story_title"), 
            rs.getString("story_content"),
            rs.getInt("story_hit"),
            rs.getInt("comment_cnt"),
            rs.getInt("like_cnt"),
            rs.getString("story_created_at"),
            rs.getInt("islike")),
            feedIdx, feedIdx, feedIdx, memIdx, feedIdx);
    }

    // 일기장 전체 조회
    public List<GetAllDiaryRes> getAllDiaries(int page) {
        String getAllFeedsQuery = "select D.diary_idx, diary_roomType, D.mem_idx, M.mem_nickname, mem_profile_url, diary_title, left(diary_content, 100) as content, diary_image, diary_hit, diary_created_at, ";
        getAllFeedsQuery += " if(D.diary_idx = Cmt.diary_idx, comment_cnt, 0) as comment_cnt, if(D.diary_idx = LikeCnt.diary_idx, like_cnt, 0) as like_cnt";
        getAllFeedsQuery += " from Diary_feed D, Member M, (select diary_idx, count(*) as comment_cnt from Diary_comment group by diary_idx) Cmt, (select diary_idx, count(*) as like_cnt from Diary_feed_like group by diary_idx) LikeCnt";
        getAllFeedsQuery += " where diary_blame < 10 && D.mem_idx = M.mem_idx group by diary_idx order by diary_idx desc limit 20 offset ?";

        return this.jdbcTemplate.query(getAllFeedsQuery, 
        (rs, rowNum) -> new GetAllDiaryRes(
            2, 
            rs.getInt("diary_roomType"), 
            rs.getInt("diary_idx"), 
            rs.getInt("mem_idx"),
            rs.getString("mem_nickname"),
            rs.getString("mem_profile_url"),
            rs.getString("diary_title"), 
            rs.getString("content"),
            rs.getString("diary_image"),
            rs.getInt("diary_hit"),
            rs.getInt("comment_cnt"),
            rs.getInt("like_cnt"),
            rs.getString("diary_created_at")),
            page);
    }

    // 일기장 카테고리별 조회
    public List<GetAllDiaryRes> getDiariesByRoomType(int roomType, int page) {
        String getAllFeedsQuery = "select D.diary_idx, diary_roomType, D.mem_idx, M.mem_nickname, mem_profile_url, diary_title, left(diary_content, 100) as content, diary_image, diary_hit, diary_created_at, ";
        getAllFeedsQuery += " if(D.diary_idx = Cmt.diary_idx, comment_cnt, 0) as comment_cnt, if(D.diary_idx = LikeCnt.diary_idx, like_cnt, 0) as like_cnt";
        getAllFeedsQuery += " from Diary_feed D, Member M, (select diary_idx, count(*) as comment_cnt from Diary_comment group by diary_idx) Cmt, (select diary_idx, count(*) as like_cnt from Diary_feed_like group by diary_idx) LikeCnt";
        getAllFeedsQuery += " where diary_roomType = ? && diary_blame < 10 && D.mem_idx = M.mem_idx group by diary_idx order by diary_idx desc limit 20 offset ?";

        return this.jdbcTemplate.query(getAllFeedsQuery, 
        (rs, rowNum) -> new GetAllDiaryRes(
            2, 
            rs.getInt("diary_roomType"), 
            rs.getInt("diary_idx"), 
            rs.getInt("mem_idx"),
            rs.getString("mem_nickname"),
            rs.getString("mem_profile_url"),
            rs.getString("diary_title"), 
            rs.getString("content"),
            rs.getString("diary_image"),
            rs.getInt("diary_hit"),
            rs.getInt("comment_cnt"),
            rs.getInt("like_cnt"),
            rs.getString("diary_created_at")),
            roomType, page);
    }

    // 일기장 상세조회
    public List<Feeds> getDiary(int diaryIdx, int memIdx) {
        String getFeedQuery = "update Diary_feed set diary_hit = diary_hit + 1 where diary_idx = ?";
        this.jdbcTemplate.update(getFeedQuery, diaryIdx);

        getFeedQuery = "select D.diary_idx, diary_roomType, D.mem_idx, M.mem_nickname, mem_profile_url, diary_title, diary_content, diary_hit, diary_created_at, DC.comment_cnt, like_cnt, islike";
        getFeedQuery += " from Diary_feed D join Member M on D.mem_idx = M.mem_idx, (select count(*) as comment_cnt from Diary_comment where diary_idx = ?) DC, ";
        getFeedQuery += " (select count(*) as like_cnt from Diary_feed_like where diary_idx = ? && dfl_status = 1) DFL, (select count(*) as islike from Diary_feed_like where diary_idx = ? && dfl_status = 1 && Diary_feed_like.mem_idx = ?) DFisLike";
        getFeedQuery += " where D.diary_idx = ? && diary_blame < 10";

        return this.jdbcTemplate.query(getFeedQuery, 
        (rs, rowNum) -> new Feeds( 
            2, 
            rs.getInt("diary_roomType"), 
            rs.getInt("diary_idx"), 
            rs.getInt("mem_idx"),
            rs.getString("mem_nickname"),
            rs.getString("mem_profile_url"),
            rs.getString("diary_title"), 
            rs.getString("diary_content"),
            rs.getInt("diary_hit"),
            rs.getInt("comment_cnt"),
            rs.getInt("like_cnt"),
            rs.getString("diary_created_at"),
            rs.getInt("islike")),
            diaryIdx, diaryIdx, diaryIdx, memIdx, diaryIdx);
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
    public List<GetAllFeedsRes> getAllFeeds(int page) {
        String getAllFeedsQuery = "select 1 as boardType, S.story_idx as feedIdx, story_roomType as roomType, S.mem_idx, mem_nickname, story_title as title, story_image as image, story_hit as hit, story_created_at as createAt, if(S.story_idx = Cmt.story_idx, comment_cnt, 0) as comment_cnt, if(S.story_idx = LikeCnt.story_idx, like_cnt, 0) as like_cnt ";
        getAllFeedsQuery += " from Story_feed S, Member M, (select story_idx, count(*) as comment_cnt from Story_feed_comment group by story_idx) Cmt, (select story_idx, count(*) as like_cnt from Story_feed_like group by story_idx) LikeCnt";
        getAllFeedsQuery += " where S.mem_idx = M.mem_idx && S.story_blame < 10 group by S.story_idx UNION";
        getAllFeedsQuery += " select 2 as boardType, D.diary_idx as feedIdx, diary_roomType as roomType, D.mem_idx, mem_nickname, diary_title as title, diary_image as image, diary_hit as hit, diary_created_at as createAt, if(D.diary_idx = Cmt.diary_idx, comment_cnt, 0) as comment_cnt, if(D.diary_idx = LikeCnt.diary_idx, like_cnt, 0) as like_cnt ";
        getAllFeedsQuery += " from Diary_feed D, (select diary_idx, count(*) as comment_cnt from Diary_comment group by diary_idx) Cmt, (select diary_idx, count(*) as like_cnt from Diary_feed_like group by diary_idx) LikeCnt";
        getAllFeedsQuery += " where D.diary_blame < 10 group by D.diary_idx UNION";
        getAllFeedsQuery += "  select 3 as boardType, review_idx as feedIdx, null as roomType, buy_mem_idx as mem_idx, B.mem_nickname as mem_nickname, concat(A.mem_nickname, '님의 ', I.Market_review.review_goods) as title, review_image as image, review_hit as hit, review_created_at as createAt, if(D.diary_idx = Cmt.diary_idx, comment_cnt, 0) as comment_cnt, if(Market_review.review_idx = LikeCnt.market_re_idx, like_cnt, 0) as like_cnt";
        getAllFeedsQuery += " from Market_review, Member A, Member B , (select review_idx, count(*) as comment_cnt from Market_review_comment group by review_idx) Cmt, (select market_re_idx, count(*) as like_cnt from Market_review_like group by market_re_idx) LikeCnt where Market_review.sell_mem_idx = A.mem_idx && Market_review.buy_mem_idx = B.mem_idx && Market_review.review_blame < 10";
        getAllFeedsQuery += " order by createAt desc limit 20 offset ? ";

        return this.jdbcTemplate.query(getAllFeedsQuery, 
        (rs, rowNum) -> new GetAllFeedsRes(
            rs.getInt("boardType"),
            rs.getInt("roomType"),
            rs.getInt("feedIdx"),
            rs.getInt("mem_idx"),
            rs.getString("mem_nickname"),
            null,
            rs.getString("title"),
            rs.getString("image"),
            rs.getInt("hit"),
            rs.getInt("comment_cnt"),
            rs.getInt("like_cnt"),
            rs.getString("createAt")),
            page);
        
    }

    // 아이홈 인기순 조회
    public List<GetAllFeedsRes> getHotFeeds(int page) throws BaseException {
        try {
            String getAllFeedsQuery = "select 1 as boardType, S.story_idx as feedIdx, story_roomType as roomType, S.mem_idx, mem_nickname, story_title as title, story_image as image, story_hit as hit, story_created_at as createAt, if(S.story_idx = Cmt.story_idx, comment_cnt, 0) as comment_cnt, if(S.story_idx = LikeCnt.story_idx, like_cnt, 0) as like_cnt ";
            getAllFeedsQuery += " from Story_feed S, Member M, (select story_idx, count(*) as comment_cnt from Story_feed_comment group by story_idx) Cmt, (select story_idx, count(*) as like_cnt from Story_feed_like group by story_idx) LikeCnt";
            getAllFeedsQuery += " where story_created_at between DATE_ADD(now(), interval -1 week) and now() && S.mem_idx = M.mem_idx && S.story_blame < 10 group by S.story_idx UNION";
            getAllFeedsQuery += " select 2 as boardType, D.diary_idx as feedIdx, diary_roomType as roomType, D.mem_idx, mem_nickname, diary_title as title, diary_image as image, diary_hit as hit, diary_created_at as createAt, if(D.diary_idx = Cmt.diary_idx, comment_cnt, 0) as comment_cnt, if(D.diary_idx = LikeCnt.diary_idx, like_cnt, 0) as like_cnt ";
            getAllFeedsQuery += " from Diary_feed D, (select diary_idx, count(*) as comment_cnt from Diary_comment group by diary_idx) Cmt, (select diary_idx, count(*) as like_cnt from Diary_feed_like group by diary_idx) LikeCnt";
            getAllFeedsQuery += " where diary_created_at between DATE_ADD(now(), interval -1 week) and now() && D.diary_blame < 10 group by D.diary_idx UNION";
            getAllFeedsQuery += "  select 3 as boardType, review_idx as feedIdx, null as roomType, buy_mem_idx as mem_idx, B.mem_nickname as mem_nickname, concat(A.mem_nickname, '님의 ', I.Market_review.review_goods) as title, review_image as image, review_hit as hit, review_created_at as createAt, if(D.diary_idx = Cmt.diary_idx, comment_cnt, 0) as comment_cnt, if(Market_review.review_idx = LikeCnt.market_re_idx, like_cnt, 0) as like_cnt";

            getAllFeedsQuery += " from Market_review, Member A, Member B , (select review_idx, count(*) as comment_cnt from Market_review_comment group by review_idx) Cmt, (select market_re_idx, count(*) as like_cnt from Market_review_like group by market_re_idx) LikeCnt where review_created_at between DATE_ADD(now(), interval -1 week) and now() && Market_review.sell_mem_idx = A.mem_idx && Market_review.buy_mem_idx = B.mem_idx && Market_review.review_blame < 10";
            getAllFeedsQuery += " order by hit desc limit 20 offset ?";
    
            return this.jdbcTemplate.query(getAllFeedsQuery, 
            (rs, rowNum) -> new GetAllFeedsRes(
                rs.getInt("boardType"),
                rs.getInt("roomType"),
                rs.getInt("feedIdx"),
                rs.getInt("mem_idx"),
                rs.getString("mem_nickname"),
                null,
                rs.getString("title"),
                rs.getString("image"),
                rs.getInt("hit"),
                rs.getInt("comment_cnt"),
                rs.getInt("like_cnt"),
                rs.getString("createAt")),
                page);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.GET_REVIEW_FAIL);
        }   
    }

    // 게시판별 인기순 조회
    public List<GetAllFeedsRes> getHotStories(int roomType, int filter, int page) throws BaseException {
        try {
            String getHotStoriesQuery = "select 1 as boardType, S.story_idx as feedIdx, story_roomType as roomType, S.mem_idx, mem_nickname, story_title as title, story_image as image, ";
            getHotStoriesQuery += " story_hit as hit, story_created_at as createAt, if(S.story_idx = Cmt.story_idx, comment_cnt, 0) as comment_cnt, if(S.story_idx = LikeCnt.story_idx, like_cnt, 0) as like_cnt";
            getHotStoriesQuery += " from Story_feed S, Member M, (select story_idx, count(*) as comment_cnt from Story_feed_comment group by story_idx) Cmt, (select story_idx, count(*) as like_cnt from Story_feed_like group by story_idx) LikeCnt";
            
            switch(filter) {
                case 1:     // 1시간(default)
                    if(roomType == 0) 
                    getHotStoriesQuery += " where story_created_at between DATE_SUB(NOW(), interval 1 hour) and now()";
                    else if(roomType == 1)
                    getHotStoriesQuery += " where story_roomType = 1 && story_created_at between DATE_SUB(NOW(), interval 1 hour) and now()";
                    else if(roomType == 2)
                    getHotStoriesQuery += " where story_roomType = 2 && story_created_at between DATE_SUB(NOW(), interval 1 hour) and now()";
                    else if(roomType == 3)
                    getHotStoriesQuery += " where story_roomType = 3 && story_created_at between DATE_SUB(NOW(), interval 1 hour) and now()";
                    getHotStoriesQuery += " && S.mem_idx = M.mem_idx && S.story_blame < 10";
                    getHotStoriesQuery += " group by S.story_idx order by hit desc, story_created_at desc limit 30 offset ?";
                    break;

                case 2:     // 24시간
                    if(roomType == 0)
                    getHotStoriesQuery += " where story_created_at between DATE_SUB(NOW(), interval 24 hour) and now()";
                    else if(roomType == 1)
                    getHotStoriesQuery += " where story_roomType = 1 && story_created_at between DATE_SUB(NOW(), interval 24 hour) and now()";
                    else if(roomType == 2)
                    getHotStoriesQuery += " where story_roomType = 2 && story_created_at between DATE_SUB(NOW(), interval 24 hour) and now()";
                    else if(roomType == 3)
                    getHotStoriesQuery += " where story_roomType = 3 && story_created_at between DATE_SUB(NOW(), interval 24 hour) and now()";
                    getHotStoriesQuery += " && S.mem_idx = M.mem_idx && S.story_blame < 10";
                    getHotStoriesQuery += " group by S.story_idx order by hit desc, story_created_at desc limit 30 offset ?";
                    break;

                case 3:     // 일주일
                    if(roomType == 0)
                    getHotStoriesQuery += " where story_created_at between DATE_FORMAT(DATE_SUB(NOW(), INTERVAL WEEKDAY(NOW()) day), '%Y-%m-%d') and now()";
                    else if(roomType == 1)
                    getHotStoriesQuery += " where story_roomType = 1 && story_created_at between DATE_FORMAT(DATE_SUB(NOW(), INTERVAL WEEKDAY(NOW()) day), '%Y-%m-%d') and now()";
                    else if(roomType == 2)
                    getHotStoriesQuery += " where story_roomType = 2 && story_created_at between DATE_FORMAT(DATE_SUB(NOW(), INTERVAL WEEKDAY(NOW()) day), '%Y-%m-%d') and now()";
                    else if(roomType == 3)
                    getHotStoriesQuery += " where story_roomType = 3 && story_created_at between DATE_FORMAT(DATE_SUB(NOW(), INTERVAL WEEKDAY(NOW()) day), '%Y-%m-%d') and now()";
                    getHotStoriesQuery += " && S.mem_idx = M.mem_idx && S.story_blame < 10";
                    getHotStoriesQuery += " group by S.story_idx order by hit desc, story_created_at desc limit 30 offset ?";
                    break;
            }
            
            return this.jdbcTemplate.query(getHotStoriesQuery, 
            (rs, rowNum) -> new GetAllFeedsRes(
                rs.getInt("boardType"),
                rs.getInt("roomType"),
                rs.getInt("feedIdx"),
                rs.getInt("mem_idx"),
                rs.getString("mem_nickname"),
                null,
                rs.getString("title"),
                rs.getString("image"),
                rs.getInt("hit"),
                rs.getInt("comment_cnt"),
                rs.getInt("like_cnt"),
                rs.getString("createAt")),
                page);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.GET_REVIEW_FAIL);
        }
    }

    public List<GetAllFeedsRes> getHotDiaries(int roomType, int filter, int page) throws BaseException {
        try {
            String getHotDiariesQuery = "select 2 as boardType, D.diary_idx as feedIdx, diary_roomType as roomType, D.mem_idx, M.mem_nickname, diary_title as title, diary_image as image,";
            getHotDiariesQuery += " diary_hit as hit, diary_created_at as createAt, if(D.diary_idx = Cmt.diary_idx, comment_cnt, 0) as comment_cnt, if(D.diary_idx = LikeCnt.diary_idx, like_cnt, 0) as like_cnt";
            getHotDiariesQuery += " from Diary_feed D, Member M, (select diary_idx, count(*) as comment_cnt from Diary_comment group by diary_idx) Cmt, (select diary_idx, count(*) as like_cnt from Diary_feed_like group by diary_idx) LikeCnt";
            
            switch(filter) {
                case 1:     // 1시간(default)
                if(roomType == 0)
                getHotDiariesQuery += " where diary_created_at between DATE_SUB(NOW(), interval 1 hour) and now()";
                else if(roomType == 1)
                getHotDiariesQuery += " where diary_roomType = 1 && diary_created_at between DATE_SUB(NOW(), interval 1 hour) and now()";
                else if(roomType == 2)
                getHotDiariesQuery += " where diary_roomType = 2 && diary_created_at between DATE_SUB(NOW(), interval 1 hour) and now()";
                getHotDiariesQuery += " && D.mem_idx = M.mem_idx && D.diary_blame < 10";
                getHotDiariesQuery += " group by D.diary_idx order by hit desc, diary_created_at desc limit 30 offset ?";
                    break;

                case 2:     // 24시간
                if(roomType == 0)
                getHotDiariesQuery += " where diary_created_at between DATE_SUB(NOW(), interval 24 hour) and now()";
                else if(roomType == 1)
                getHotDiariesQuery += " where diary_roomType = 1 && diary_created_at between DATE_SUB(NOW(), interval 24 hour) and now()";
                else if(roomType == 2)
                getHotDiariesQuery += " where diary_roomType = 2 && diary_created_at between DATE_SUB(NOW(), interval 24 hour) and now()";
                getHotDiariesQuery += " && D.mem_idx = M.mem_idx && D.diary_blame < 10";
                getHotDiariesQuery += " group by D.diary_idx order by hit desc, diary_created_at desc limit 30 offset ?";
                    break;

                case 3:     // 일주일
                if(roomType == 0)
                getHotDiariesQuery += " where diary_created_at between DATE_FORMAT(DATE_SUB(NOW(), INTERVAL WEEKDAY(NOW()) day), '%Y-%m-%d') and now()";
                else if(roomType == 1)
                getHotDiariesQuery += " where diary_roomType = 1 && diary_created_at between DATE_FORMAT(DATE_SUB(NOW(), INTERVAL WEEKDAY(NOW()) day), '%Y-%m-%d') and now()";
                else if(roomType == 2)
                getHotDiariesQuery += " where diary_roomType = 2 && diary_created_at between DATE_FORMAT(DATE_SUB(NOW(), INTERVAL WEEKDAY(NOW()) day), '%Y-%m-%d') and now()";
                getHotDiariesQuery += " && D.mem_idx = M.mem_idx && D.diary_blame < 10";
                getHotDiariesQuery += " group by D.diary_idx order by hit desc, diary_created_at desc limit 30 offset ?";
                    break;
            }
            
            return this.jdbcTemplate.query(getHotDiariesQuery, 
            (rs, rowNum) -> new GetAllFeedsRes(
                rs.getInt("boardType"),
                rs.getInt("roomType"),
                rs.getInt("feedIdx"),
                rs.getInt("mem_idx"),
                rs.getString("mem_nickname"),
                null,
                rs.getString("title"),
                rs.getString("image"),
                rs.getInt("hit"),
                rs.getInt("comment_cnt"),
                rs.getInt("like_cnt"),
                rs.getString("createAt")),
                page);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.GET_REVIEW_FAIL);
        }
    }

    public List<GetAllFeedsRes> getHotReivews(int filter, int page) throws BaseException {
        try {
            String getHotReviewsQuery = "select 3 as boardType, R.review_idx as feedIdx, null as roomType, R.buy_mem_idx, B.mem_nickname, concat(A.mem_nickname, '님의 ', R.review_goods) as title, ";
            getHotReviewsQuery += " review_image as image, review_hit as hit, review_created_at as createAt, if(R.review_idx = Cmt.review_idx, comment_cnt, 0) as comment_cnt, if(R.review_idx = LikeCnt.market_re_idx, like_cnt, 0) as like_cnt";
            getHotReviewsQuery += "  from Market_review R, Member A, Member B, (select review_idx, count(*) as comment_cnt from Market_review_comment group by review_idx) Cmt, (select market_re_idx, count(*) as like_cnt from Market_review_like group by market_re_idx) LikeCnt";

            switch(filter) {
                case 1:     // 1시간(default)
                getHotReviewsQuery += " where review_created_at between DATE_SUB(NOW(), interval 1 hour) and now()";
                getHotReviewsQuery += " && R.sell_mem_idx = A.mem_idx && R.buy_mem_idx = B.mem_idx && R.review_blame < 10";
                getHotReviewsQuery += " group by R.review_idx order by hit desc, review_created_at desc limit 30 offset ?";
                    break;

                case 2:     // 24시간
                getHotReviewsQuery += " where review_created_at between DATE_SUB(NOW(), interval 24 hour) and now()";
                getHotReviewsQuery += " && R.sell_mem_idx = A.mem_idx && R.buy_mem_idx = B.mem_idx && R.review_blame < 10";
                getHotReviewsQuery += " group by R.review_idx order by hit desc, review_created_at desc limit 30 offset ?";
                    break;

                case 3:     // 일주일
                getHotReviewsQuery += " where review_created_at between DATE_FORMAT(DATE_SUB(NOW(), INTERVAL WEEKDAY(NOW()) day), '%Y-%m-%d') and now()";
                getHotReviewsQuery += " && R.sell_mem_idx = A.mem_idx && R.buy_mem_idx = B.mem_idx && R.review_blame < 10";
                getHotReviewsQuery += " group by R.review_idx order by hit desc, review_created_at desc limit 30 offset ?";
                    break;
            }
            
            return this.jdbcTemplate.query(getHotReviewsQuery, 
            (rs, rowNum) -> new GetAllFeedsRes(
                rs.getInt("boardType"),
                rs.getInt("roomType"),
                rs.getInt("feedIdx"),
                rs.getInt("buy_mem_idx"),
                rs.getString("mem_nickname"),
                null,
                rs.getString("title"),
                rs.getString("image"),
                rs.getInt("hit"),
                rs.getInt("comment_cnt"),
                rs.getInt("like_cnt"),
                rs.getString("createAt")),
                page);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.GET_REVIEW_FAIL);
        }
    }

    //게시글 신고하기
    public int postBlame(PostBlameReq postBlameReq) {
        String doubleProtectQuery = "select count(*) from Blame where mem_idx = ? and target_type = ? and target_idx = ?";
        int doubleProtect = this.jdbcTemplate.queryForObject(doubleProtectQuery,int.class,postBlameReq.getMemIdx(),postBlameReq.getBoardIdx(),postBlameReq.getComuIdx());

        if(doubleProtect == 0){
            String postBlameQuery = "insert into Blame(mem_idx,target_type,target_idx,blame_time)VALUES(?,?,?,now())";
            this.jdbcTemplate.update(postBlameQuery,postBlameReq.getMemIdx(),postBlameReq.getBoardIdx(),postBlameReq.getComuIdx());
            return doubleProtect;
        }
        if(postBlameReq.getBoardIdx() == 1){
            this.jdbcTemplate.update("update Story_feed set story_blame = story_blame + 1 where story_idx = ? and board_idx = ?",
                    postBlameReq.getComuIdx(),postBlameReq.getBoardIdx());
        } else if(postBlameReq.getBoardIdx() == 2){
            this.jdbcTemplate.update("update Diary_feed D set diary_blame = diary_blame + 1 where diary_idx = ? and board_idx = ?",
                    postBlameReq.getComuIdx(),postBlameReq.getBoardIdx());
        } else if(postBlameReq.getBoardIdx() == 3){
            this.jdbcTemplate.update("update Market_review set review_blame = review_blame + 1 where review_idx = ? and board_idx = ?",
                    postBlameReq.getComuIdx(),postBlameReq.getBoardIdx());
        } else if(postBlameReq.getBoardIdx() == 4){
            this.jdbcTemplate.update("update Market set market_blame = market_blame + 1 where market_idx = ? and board_idx = ?",
                    postBlameReq.getComuIdx(),postBlameReq.getBoardIdx());
        }

        return doubleProtect;
    }
}
