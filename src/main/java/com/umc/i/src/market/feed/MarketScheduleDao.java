package com.umc.i.src.market.feed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;


@Slf4j
@Repository
@RequiredArgsConstructor
public class MarketScheduleDao implements MarketScheduleRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void resetHitCountTable() {
        String query = "delete from Daily_market_feed_hit where created_at < (now() - interval 12 HOUR)";

        try {
            jdbcTemplate.update(query);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void getHitRankView() {
        String query = "drop view if exists Hot_market_feed";

        try {
            jdbcTemplate.update(query);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        query = "create view Hot_market_feed as\n" +
                "select *, market_category, count(*) as count\n" +
                "from Daily_market_feed_hit\n" +
                "group by market_idx\n" +
                "order by count DESC";

        try {
            jdbcTemplate.update(query);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
