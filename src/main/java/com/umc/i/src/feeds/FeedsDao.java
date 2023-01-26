package com.umc.i.src.feeds;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.feeds.model.post.PostBlameReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

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
            String postBlameQuery = "insert into Blame(mem_idx,target_type,target_idx)VALUES(?,?,?)";
            this.jdbcTemplate.update(postBlameQuery,postBlameReq.getMemIdx(),postBlameReq.getBoardIdx(),postBlameReq.getComuIdx());
            return doubleProtect;
        }
        return doubleProtect;
    }
}
