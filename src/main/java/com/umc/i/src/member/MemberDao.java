package com.umc.i.src.member;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.umc.i.src.member.model.post.PostJoinAuthReq;

@Repository

public class MemberDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //핸드폰번호 중복 확인
    public int checkPhone(String tel) {
        String checkPhoneQuery = "select * from Member where mem_phone = ?";
        // 있으면 1 없으면 0
        return this.jdbcTemplate.queryForObject(checkPhoneQuery, int.class, tel);
    }

    //이메일 중복 확인
    public int checkEmail(String email) {
        String checkEmailQuery = "select * from Member where mem_email = ?";
        // 있으면 1 없으면 0
        return this.jdbcTemplate.queryForObject(checkEmailQuery, int.class, email);
    }

    //인증 코드 발송(인증테이블에 저장)
    public String createAuth(PostJoinAuthReq postJoinAuthReq, String key) {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = time.format(timeFormatter);
        
        String createAuthQuery = "insert into Member_auth (ma_key, ma_type, ma_generate_time, ma_expired) values (?, ?, ?, ?)";
        Object[] createAuthParams = new Object[]{key, postJoinAuthReq.getType(), currentTime, false}; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(createAuthQuery, createAuthParams);    //인증 정보 생성

        String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        String authIdx = Integer.toString(this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class));
        return authIdx;
    }

}
