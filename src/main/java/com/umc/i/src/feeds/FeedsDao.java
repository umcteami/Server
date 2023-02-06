package com.umc.i.src.feeds;

import com.umc.i.src.feeds.model.post.PostBlameReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;

@Repository
public class FeedsDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
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
