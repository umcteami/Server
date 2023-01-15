package com.umc.i.member.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class JdbcTemplateSignUpRepository  implements SignUpAuthRepository{

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateSignUpRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Optional<SignAuthNumber> findByAuthIdx(int authIdx) {
        List<SignAuthNumber> result = jdbcTemplate.query("select * from member_auth where ma_idx = ?", authRowMapper(), authIdx);

        return result.stream().findAny();
    }

    private RowMapper<SignAuthNumber> authRowMapper() {
        return (rs, rowNum) -> {
            SignAuthNumber signAuthNumber = new SignAuthNumber();
            signAuthNumber.setAuthIdx(rs.getInt("ma_idx"));
            signAuthNumber.setKey(rs.getInt("ma_key"));
            signAuthNumber.setType(rs.getString("ma_type"));
            signAuthNumber.setCreatedAt(rs.getTimestamp("ma_generate_time"));
            return signAuthNumber;
        };
    }

}
