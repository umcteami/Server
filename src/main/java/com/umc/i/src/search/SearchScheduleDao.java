package com.umc.i.src.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Slf4j
@Repository
@RequiredArgsConstructor
public class SearchScheduleDao implements SearchScheduleRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void resetKeywordTable() {
        String query = "delete from Keyword where created_at < (now() - interval 12 HOUR)";
        try {
            jdbcTemplate.update(query);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void getSearchKeywordTable() {
        String query = "drop table if exists Hot_keyword";
        try {
            jdbcTemplate.update(query);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        query = "create table Hot_keyword as\n" +
                "select keyword, count(*) as count\n" +
                "from Hot_keyword\n" +
                "group by keyword\n" +
                "order by count DESC\n" +
                "limit 0, 7;";
        try {
            jdbcTemplate.update(query);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
