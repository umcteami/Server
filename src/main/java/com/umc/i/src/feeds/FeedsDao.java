package com.umc.i.src.feeds;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.umc.i.src.feeds.model.post.PostFeedsReq;

@Repository
public class FeedsDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 이야기방, 일기장 게시글 저장
    public int createFeeds(int boardType, PostFeedsReq postFeedsReq) {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = time.format(timeFormatter);

        String createFeedsQuery = null;

        switch(boardType) {
            case 1: //이야기방
                createFeedsQuery = "insert into Story_feed (story_roomType, mem_idx, story_title, story_content, ";
                createFeedsQuery += "story_image, story_hit, story_comment_count, story_like_count, story_blame, story_created_at)";
                createFeedsQuery += "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            case 2: //일기장
                 createFeedsQuery = "insert into Story_feed (story_roomType, mem_idx, story_title, story_content, ";
                createFeedsQuery += "story_image, story_hit, story_comment_count, story_like_count, story_blame, story_created_at)";
                createFeedsQuery += "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        }

        Object[] createFeedsParams = new Object[] {postFeedsReq.getRoomType()};
        this.jdbcTemplate.update(createFeedsQuery, createFeedsParams);  // 게시물 저장

        String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        int feedsIdx = this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
        
        return feedsIdx;
    }
}
