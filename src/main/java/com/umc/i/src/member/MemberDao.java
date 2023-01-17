package com.umc.i.src.member;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import com.umc.i.src.member.model.post.PostAuthNumberReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

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
    public int createAuth(int type, String key) {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = time.format(timeFormatter);
        
        String createAuthQuery = "insert into Member_auth (ma_key, ma_type, ma_generate_time, ma_expired) values (?, ?, ?, ?)";
        Object[] createAuthParams = new Object[]{key, Integer.toString(type), currentTime, false}; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(createAuthQuery, createAuthParams);    //인증 정보 생성

        String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        int authIdx = this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
        return authIdx;
    }

    // 인증 코드 찾기
    public Optional<PostAuthNumberReq> findByAuthIdx(int maIdx) {
        List<PostAuthNumberReq> result = this.jdbcTemplate.query("select * from Member_auth where ma_idx = ?", authRowMapper(), maIdx);

        return result.stream().findAny();
    }

    private static RowMapper<PostAuthNumberReq> authRowMapper() {
        return (rs, rowNum) -> {
            PostAuthNumberReq postAuthNumberReq = new PostAuthNumberReq();
            postAuthNumberReq.setAuthIdx(rs.getInt("ma_idx"));
            postAuthNumberReq.setType(rs.getString("ma_type"));
            postAuthNumberReq.setAuthNumber(rs.getString("ma_key"));
            postAuthNumberReq.setCreatedAt(rs.getTimestamp("ma_generate_time"));
            return postAuthNumberReq;
        };
    }
}

