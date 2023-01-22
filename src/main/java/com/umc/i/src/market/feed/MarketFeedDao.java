package com.umc.i.src.market.feed;

import com.umc.i.config.Constant;
import com.umc.i.src.market.feed.model.GetMarketFeedRes;
import com.umc.i.src.market.feed.model.MarketFeed;
import com.umc.i.src.market.feed.model.PostMarketFeedReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
@Repository
public class MarketFeedDao implements MarketFeedRepository {

    private final JdbcTemplate jdbcTemplate;

    public MarketFeedDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void updateFeedHitCount(String marketIdx) {
        String query = "update market set market_hit = market_hit + 1 where market_idx = ?";

        try {
            jdbcTemplate.update(query);
        } catch (Exception e) {
            log.info("{}", e);
        }
    }

    @Override
    public void updateFeed(String marketIdx, PostMarketFeedReq req) {
        String query = "update market set market_group = ?, market_price = ?, market_title = ?, market_content = ?, market_image = ?, market_soldout = ? where market_idx = ?";

        try {
            jdbcTemplate.update(query, req.getCategory(), req.getPrice(), req.getTitle(), req.getCategory(), req.getImage(), req.getSoldout(), marketIdx);
        } catch (Exception e) {
            log.info("{}", e);
        }
    }

    @Override
    public void deleteFeed(String marketIdx) {
        String query = "delete from market where market_idx = ?";

        try {
            jdbcTemplate.update(query, marketIdx);
        } catch (Exception e) {
            log.info("{}", e);
        }
    }

    @Override
    public List<MarketFeed> getFeedByMarketIdx(String marketIdx) {
        String query = "select m.market_idx, m.mem_idx, m.market_group, m.market_price, m.market_title, m.market_content, m.market_soldout, m.market_hit, m.market_like_count, m.market_created_at from market m where m.market_idx = ?";

        try {
            List<MarketFeed> result = jdbcTemplate.query(query, marketFeedByMarketIdxRowMapper(), marketIdx);
            return result;
        } catch (Exception e) {
            log.info("{}", e);
        }

        return null;
    }

    @Override
    public int postNewFeed(PostMarketFeedReq marketFeed) {
        String query = "insert into market (mem_idx, market_group, market_price, market_title, market_content, market_image) values (?, ?, ?, ?, ?, ?)";
        Object[] params = {marketFeed.getUserId(), marketFeed.getCategory(), marketFeed.getPrice(), marketFeed.getTitle(), marketFeed.getContent(), marketFeed.getImage()};
        try {
            jdbcTemplate.update(query, params);
            return marketFeed.getUserId();
        } catch (Exception e) {
            log.info("{}", e);
        }

        return -1;
    }

    @Override
    public List<GetMarketFeedRes> getFeedByCategory(String category, int userIdx, String soldout, int page) {

        String query = "select m.market_idx, m.mem_idx, m.market_group, m.market_price, m.market_title, m.market_image, m.market_soldout, m.market_hit, m.market_like_count, m.market_created_at, IF (ml.mem_idx is null, false, true) as mem_liked from market m left join (select market_idx, mem_idx from market_like where mem_idx = ?) ml on m.market_idx = ml.market_idx where m.market_group = ? and m.market_soldout = ? order by m.market_created_at DESC limit ?, ? + 9;";

        try {
            List<GetMarketFeedRes> result = this.jdbcTemplate.query(query, marketFeedByCategoryRowMapper(), userIdx, category, soldout, page * 9, page + Constant.FEED_PER_PAGE);
            return result;
        } catch (Exception e) {
            log.info("{}", e);
        }

        return null;
    }

    @Override
    public void feedLike(int userIdx, int marketIdx) {
        String query = "insert into market_like (market_idx, mem_idx) values (?, ?)";

        try {
            jdbcTemplate.update(query);
        } catch (Exception e) {
            log.info("{}", e);
        }
    }

    @Override
    public List<GetMarketFeedRes> getFeedByUserIdx(int userIdx) {
        String query = "select m.market_idx, m.market_title, m.market_soldout, m.market_price, m.market_image, m.market_like_count, IF (ml.mem_idx is null, false, true) as mem_liked  from market left join (select market_idx, mem_idx from market_like where mem_idx = ?) ml on m.market_idx = ml.market_idx where m.mem_idx = ? order by m.market_created_at DESC";

        try {
            List<GetMarketFeedRes> result = jdbcTemplate.query(query, marketFeedByCategoryRowMapper(), userIdx, userIdx);
            return result;
        } catch (Exception e) {
            log.info("{}", e);
        }
        return null;
    }

    private RowMapper<MarketFeed> marketFeedByMarketIdxRowMapper() {
        return (rs, rowNum) -> {
            MarketFeed marketFeed = new MarketFeed();
            marketFeed.setMarketIdx(rs.getInt("m.market_idx"));
            marketFeed.setUserIdx(rs.getInt("m.mem_idx"));
            marketFeed.setGroup(rs.getString("m.market_group"));
            marketFeed.setTitle(rs.getString("m.market_title"));
            marketFeed.setContent(rs.getString("m.market_content"));
            marketFeed.setPrice(rs.getInt("m.market_price"));
            marketFeed.setSoldout(rs.getString("m.market_soldout"));
//            marketFeed.setImage(rs.getString("m.market_image"));
            marketFeed.setLikeCount(rs.getInt("m.market_like_count"));
            marketFeed.setHit(rs.getInt("m.market_hit"));
            marketFeed.setCreatedAt(rs.getTimestamp("m.market_created_at"));
            return marketFeed;
        };
    }

    private RowMapper<GetMarketFeedRes> marketFeedByCategoryRowMapper() {
        return (rs, rowNum) -> {
            GetMarketFeedRes marketFeed = new GetMarketFeedRes();
            marketFeed.setMarketIdx(rs.getInt("m.market_idx"));
            marketFeed.setUserIdx(rs.getInt("m.mem_idx"));
            marketFeed.setGroup(rs.getString("m.market_group"));
            marketFeed.setTitle(rs.getString("m.market_title"));
            marketFeed.setContent(rs.getString("m.market_content"));
            marketFeed.setPrice(rs.getInt("m.market_price"));
            marketFeed.setSoldout(rs.getString("m.market_soldout"));
//            marketFeed.setImage(rs.getString("m.market_image"));
            marketFeed.setLikeCount(rs.getInt("m.market_like_count"));
            marketFeed.setHit(rs.getInt("m.market_hit"));
            marketFeed.setCreatedAt(rs.getTimestamp("m.market_created_at"));
            marketFeed.setUserLiked(rs.getBoolean("mem_liked"));
            return marketFeed;
        };
    }
}
