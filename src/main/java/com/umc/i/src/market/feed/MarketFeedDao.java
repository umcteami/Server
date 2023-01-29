package com.umc.i.src.market.feed;

import com.umc.i.config.Constant;
import com.umc.i.src.market.feed.model.GetMarketFeedRes;
import com.umc.i.src.market.feed.model.GetMarketFeedReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;

@Slf4j
@Repository
public class MarketFeedDao implements MarketFeedRepository {

    private final JdbcTemplate jdbcTemplate;

    public MarketFeedDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int getFeedUserIdx(String marketIdx) {
        String query = "select mem_idx from market where market_idx = ?";

        try {
            int memIdx = jdbcTemplate.update(query, marketIdx);
            return memIdx;
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return -1;
    }

    @Override
    public void postCoverImage(List<String> filesUrlList, String marketIdx) {
        String query = "update market set market_image = ? where market_idx = ?";

        try {
            jdbcTemplate.update(query, filesUrlList.get(0), marketIdx);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void updateFeedHitCount(String marketIdx) {
        String query = "update market set market_hit = market_hit + 1 where market_idx = ?";

        try {
            jdbcTemplate.update(query);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void updateFeed(String marketIdx, GetMarketFeedReq req) {
        String query = "update market set market_group = ?, market_price = ?, market_title = ?, market_content = ?, market_soldout = ? where market_idx = ?";

        try {
            jdbcTemplate.update(query, req.getCategory(), req.getPrice(), req.getTitle(), req.getCategory(), req.getSoldout(), marketIdx);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void deleteFeed(String marketIdx) {
        String query = "delete from market where market_idx = ?";

        try {
            jdbcTemplate.update(query, marketIdx);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public int postFeedImages(List<String> filesUrlList, int marketIdx) {
        String query = "insert into image_url (content_category, content_idx, image_url, image_order) values (?, ?, ?, ?)";

        int affectedRows = 0;
        for (String imageUrl : filesUrlList) {
            try {
                affectedRows += jdbcTemplate.update(query, 0, marketIdx, imageUrl, filesUrlList.indexOf(imageUrl));
            } catch (Exception e) {
                log.error(e.getMessage());
                return -1;
            }
        }
        return affectedRows;
    }

    public void deleteImages(int marketIdx) {
        String query = "delete from image_url where market_idx = ?";

        try {
            jdbcTemplate.update(query, marketIdx);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public List<GetMarketFeedRes> getFeedByMarketIdx(String marketIdx, String memIdx) {
        String query = "select f.*, group_concat(g.image_url) as image_url\n" +
                "from (\n" +
                "\tselect d.*, e.mem_nickname\n" +
                "\tfrom (\n" +
                "\t\tselect a.market_idx, a.mem_idx, a.market_title, a.market_content, a.market_soldout, a.market_price, a.market_image, a.market_like_count, a.market_hit, IF (b.mem_idx is null, false, true) as mem_liked, a.market_created_at\n" +
                "\t\tfrom market a \t\n" +
                "\t\tleft join (\n" +
                "\t\t\tselect market_idx, mem_idx \n" +
                "\t\t\tfrom market_like \n" +
                "\t\t\twhere mem_idx = ?) b \n" +
                "\t\ton a.market_idx = b.market_idx \n" +
                "\t\twhere a.market_idx = ?\n" +
                "\t) d\n" +
                "\tjoin member e\n" +
                "\ton d.mem_idx = e.mem_idx ) f\n" +
                "join image_url g\n" +
                "on f.market_idx = g.content_idx\n" +
                "order by g.image_order;";

        try {
            List<GetMarketFeedRes> feedResult = jdbcTemplate.query(query, marketFeedByMarketIdxRowMapper(), memIdx, marketIdx);
            return feedResult;

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    @Override
    public int postNewFeed(GetMarketFeedReq marketFeed) {
        String query = "insert into market (mem_idx, market_group, market_price, market_title, market_content) values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = (connection) -> {
            PreparedStatement preparedStatement = connection.prepareStatement(query, new String[]{"market_idx"});
            preparedStatement.setInt(1, marketFeed.getUserId());
            preparedStatement.setString(2, marketFeed.getCategory());
            preparedStatement.setInt(3, marketFeed.getPrice());
            preparedStatement.setString(4, marketFeed.getTitle());
            preparedStatement.setString(5, marketFeed.getContent());
            return preparedStatement;
        };

        try {
            jdbcTemplate.update(preparedStatementCreator, keyHolder);
            return keyHolder.getKey().intValue();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return -1;
    }

    @Override
    public List<GetMarketFeedRes> getFeedByCategory(String category, int userIdx, String soldout, int page) {

        String query = "select m.market_idx, m.mem_idx, m.market_group, m.market_price, m.market_title, m.market_image, m.market_soldout, m.market_hit, m.market_like_count, m.market_created_at, IF (ml.mem_idx is null, false, true) as mem_liked from market m left join (select market_idx, mem_idx from market_like where mem_idx = ?) ml on m.market_idx = ml.market_idx where m.market_group = ? and m.market_soldout = ? order by m.market_created_at DESC limit ?, ?;";

        try {
            List<GetMarketFeedRes> result = this.jdbcTemplate.query(query, marketFeedByCategoryRowMapper(), userIdx, category, soldout, page * 9, (page * 9) + Constant.FEED_PER_PAGE);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
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
        String query = "select m.market_idx, m.market_title, m.market_soldout, m.market_price, m.market_image, m.market_like_count, IF (ml.mem_idx is null, false, true) as mem_liked  from market m left join (select market_idx, mem_idx from market_like where mem_idx = ?) ml on m.market_idx = ml.market_idx where m.mem_idx = ? order by m.market_created_at DESC";

        try {
            List<GetMarketFeedRes> result = jdbcTemplate.query(query, marketFeedByUserIdxRowMapper(), userIdx, userIdx);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private RowMapper<GetMarketFeedRes> marketFeedByMarketIdxRowMapper() {
        return (rs, rowNum) -> {
            GetMarketFeedRes feedRes = new GetMarketFeedRes();
            feedRes.setMarketIdx(rs.getInt("market_idx"));
            feedRes.setUserIdx(rs.getInt("mem_idx"));
            feedRes.setUserNickname(rs.getString("mem_nickname"));
            feedRes.setTitle(rs.getString("market_title"));
            feedRes.setContent(rs.getString("market_content"));
            feedRes.setPrice(rs.getInt("market_price"));
            feedRes.setSoldout(rs.getString("market_soldout"));
            feedRes.setImage(rs.getString("image_url"));
            feedRes.setLikeCount(rs.getInt("market_like_count"));
            feedRes.setHit(rs.getInt("market_hit"));
            feedRes.setCreatedAt(rs.getTimestamp("market_created_at"));
            return feedRes;
        };
    }

    private RowMapper<GetMarketFeedRes> marketFeedByCategoryRowMapper() {
        return (rs, rowNum) -> {
            GetMarketFeedRes marketFeed = new GetMarketFeedRes();
            marketFeed.setMarketIdx(rs.getInt("m.market_idx"));
            marketFeed.setUserIdx(rs.getInt("m.mem_idx"));
            marketFeed.setGroup(rs.getString("m.market_group"));
            marketFeed.setTitle(rs.getString("m.market_title"));
//            marketFeed.setContent(rs.getString("m.market_content"));
            marketFeed.setPrice(rs.getInt("m.market_price"));
            marketFeed.setSoldout(rs.getString("m.market_soldout"));
            marketFeed.setImage(rs.getString("m.market_image"));
            marketFeed.setLikeCount(rs.getInt("m.market_like_count"));
            marketFeed.setHit(rs.getInt("m.market_hit"));
            marketFeed.setCreatedAt(rs.getTimestamp("m.market_created_at"));
            marketFeed.setUserLiked(rs.getBoolean("mem_liked"));
            return marketFeed;
        };
    }

    private RowMapper<GetMarketFeedRes> marketFeedByUserIdxRowMapper() {
        return (rs, rowNum) -> {
            GetMarketFeedRes marketFeed = new GetMarketFeedRes();
            marketFeed.setMarketIdx(rs.getInt("m.market_idx"));
            marketFeed.setUserIdx(rs.getInt("mem_idx"));
            marketFeed.setTitle(rs.getString("m.market_title"));
            marketFeed.setPrice(rs.getInt("m.market_price"));
            marketFeed.setSoldout(rs.getString("m.market_soldout"));
            marketFeed.setImage(rs.getString("m.market_image"));
            marketFeed.setLikeCount(rs.getInt("m.market_like_count"));
            marketFeed.setUserLiked(rs.getBoolean("mem_liked"));
            return marketFeed;
        };
    }
}
