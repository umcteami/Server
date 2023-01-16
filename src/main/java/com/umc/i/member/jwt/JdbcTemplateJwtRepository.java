package com.umc.i.member.jwt;

import com.umc.i.member.login.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class JdbcTemplateJwtRepository implements JwtRepository{

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateJwtRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public Optional<Member> findByLoginId(String id) {
        List<Member> result = jdbcTemplate.query("select * from member where mem_idx = ?", memberRowMapper(), id);

        return result.stream().findAny();
    }

    @Override
    public void deleteRefreshToken(String memIdx) {
        try {
            int result = jdbcTemplate.update("delete from auto_login where mem_idx = ?", memIdx);
        } catch (Exception e) {
            log.info("{}", e);
            ;
        }
    }

    @Override
    public void insertRefreshToken(String memIdx, String refreshToken) {
        try {
            int result = jdbcTemplate.update("insert into auto_login(mem_idx, aul_key, aul_created_time) values (?, ?, now())", memIdx, refreshToken);
        } catch (Exception e) {
            log.info("{}", e);
            ;
        }
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setId(rs.getLong("mem_idx"));
            return member;
        };
    }
}
