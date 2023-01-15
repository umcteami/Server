package com.umc.i.member.login;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class JdbcTemplateMemberRepository implements MemberRepository{

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateMemberRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Optional<Member> findByLoginEmail(String loginEmail) {
        List<Member> result = jdbcTemplate.query("select * from member where mem_email = ?", memberRowMapper(), loginEmail);

        return result.stream().findAny();
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setId(rs.getLong("mem_idx"));
            member.setEmail(rs.getString("mem_email"));
            member.setPassword(rs.getString("mem_password"));
            member.setNickname("mem_nickname");
            return member;
        };
    }
}
