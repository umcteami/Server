package com.umc.i.src.market.feed;

import com.umc.i.config.Constant;
import com.umc.i.src.market.feed.model.GetMarketFeedRes;
import com.umc.i.src.market.feed.model.GetMarketFeedReq;
import com.umc.i.src.market.feed.model.MarketImage;
import com.umc.i.src.member.model.Member;
import com.umc.i.utils.S3Storage.S3Uploader;
import lombok.RequiredArgsConstructor;
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
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MarketFeedDao implements MarketFeedRepository {

    private final JdbcTemplate jdbcTemplate;
    
    private final S3Uploader s3Uploader;

    @Override
    public int getFeedUserIdx(String marketIdx) {
        String query = "select mem_idx from Market where market_idx = ?";

        try {
            List<Member> result = jdbcTemplate.query(query, getFeedMemberIdxRowMapper(), marketIdx);
            return result.get(0).getMemIdx();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return -1;
    }

    @Override
    public void postCoverImage(List<String> filesUrlList, String marketIdx) {
        String query = "update Market set market_image = ? where market_idx = ?";

        try {
            jdbcTemplate.update(query, filesUrlList.get(0), marketIdx);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void updateFeedHitCount(int category, String marketIdx) {
        String query = "insert into Daily_market_feed_hit values (default, ?, ?, default);";
        try {
            jdbcTemplate.update(query, marketIdx, category);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        query = "update Market set market_hit = market_hit + 1 where market_idx = ?;";
        try {
            jdbcTemplate.update(query, marketIdx);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void updateFeed(String marketIdx, GetMarketFeedReq req) {
        String query = "update Market set market_group = ?, market_price = ?, market_title = ?, market_content = ?, market_soldout = ? where market_idx = ?";

        try {
            jdbcTemplate.update(query, req.getCategory(), req.getPrice(), req.getTitle(), req.getContent(), req.getSoldout(), marketIdx);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void updateFeedSoldout(String marketIdx, GetMarketFeedReq req) {
        String query = "update Market set market_soldout = ? where market_idx = ?";

        try {
            jdbcTemplate.update(query, req.getSoldout(), marketIdx);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void deleteFeed(String marketIdx) {
        String query = "delete from Market where market_idx = ?";

        try {
            jdbcTemplate.update(query, marketIdx);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public int postFeedImages(List<String> filesUrlList, int marketIdx) {
        String query = "insert into Image_url (content_category, content_idx, image_url, image_order) values (?, ?, ?, ?)";

        int affectedRows = 0;
        for (String imageUrl : filesUrlList) {
            try {
                affectedRows += jdbcTemplate.update(query, 4, marketIdx, imageUrl, filesUrlList.indexOf(imageUrl));
            } catch (Exception e) {
                log.error(e.getMessage());
                return -1;
            }
        }
        return affectedRows;
    }

    public void deleteImages(int marketIdx) {
        String query = "select * from Image_url where content_idx = ? and content_category = 4;";

        try {
            List<MarketImage> imageList = jdbcTemplate.query(query,
                    (rs, rowNum) -> new MarketImage(
                            rs.getInt("image_idx"),
                            rs.getInt("content_category"),
                            rs.getInt("content_idx"),
                            rs.getString("image_url"),
                            rs.getInt("image_order")
                    ), marketIdx);
            for (MarketImage image : imageList) {
                s3Uploader.delete(image.getImageUrl());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        
        query = "delete from Image_url where content_idx = ? and content_category = 4";

        try {
            jdbcTemplate.update(query, marketIdx);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public Optional<GetMarketFeedRes> getFeedByMarketIdx(String marketIdx, String memIdx) {
        String query = "select g.*, group_concat(h.image_url) as image_url \n" +
                "from ( \n" +
                "\tselect e.*, f.mem_nickname, f.mem_profile_url\n" +
                "\tfrom ( \n" +
                "\t\tselect c.*, d.market_like_count \n" +
                "\t\tfrom ( \n" +
                "\t\t\tselect  \n" +
                "\t\t\t\ta.market_idx,  \n" +
                "                a.mem_idx,  \n" +
                "                a.market_group,  \n" +
                "                a.market_title,  \n" +
                "                a.market_content,  \n" +
                "                a.market_soldout,  \n" +
                "                a.market_price,  \n" +
                "                a.market_image,  \n" +
                "                a.market_hit,  \n" +
                "                IF (b.mem_idx22 is null, false, true) as mem_liked,  \n" +
                "                a.market_created_at  \n" +
                "\t\t\tfrom Market a \n" +
                "\t\t\tleft join ( \n" +
                "\t\t\t\tselect market_idx, mem_idx22 \n" +
                "                from Market_like \n" +
                "                where mem_idx22 = ?\n" +
                "\t\t\t) b \n" +
                "\t\t\ton a.market_idx = b.market_idx \n" +
                "\t\t\twhere a.market_idx = ?\n" +
                "\t\t) c \n" +
                "\t\tleft join ( \n" +
                "\t\t\tselect market_idx, count(*) as market_like_count \n" +
                "\t\t\tfrom Market_like \n" +
                "\t\t\twhere market_idx = ?\n" +
                "\t\t\tgroup by market_idx \n" +
                "\t\t) d \n" +
                "        on c.market_idx = d.market_idx \n" +
                "\t) e \n" +
                "\tleft join ( \n" +
                "\t\tselect mem_idx, mem_nickname, mem_profile_url\n" +
                "\t\tfrom Member \n" +
                "\t) f \n" +
                "\ton e.mem_idx = f.mem_idx \n" +
                "\t) g \n" +
                "\tjoin Image_url h \n" +
                "\ton g.market_idx = h.content_idx\n" +
                "    where content_category = 4\n" +
                "\torder by h.image_order;";

        List<GetMarketFeedRes> feedResult = null;
        try {
            feedResult = jdbcTemplate.query(query, marketFeedByMarketIdxRowMapper(), memIdx, marketIdx, marketIdx);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return feedResult.stream().findAny();
    }

    @Override
    public int postNewFeed(GetMarketFeedReq marketFeed) {
        String query = "insert into Market (mem_idx, market_group, market_price, market_title, market_content, board_idx) values (?, ?, ?, ?, ?, 4)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = (connection) -> {
            PreparedStatement preparedStatement = connection.prepareStatement(query, new String[]{"market_idx"});
            preparedStatement.setInt(1, marketFeed.getUserIdx());
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
    public List<GetMarketFeedRes> getAllFeed(int userIdx, String soldout, int page) {
        String query = "select a.*, b.market_like_count \n" +
                "from ( \n" +
                "\tselect  \n" +
                "\t\tm.market_idx, \n" +
                "\t\tm.mem_idx,  \n" +
                "\t\tm.market_group,  \n" +
                "\t\tm.market_price,  \n" +
                "\t\tm.market_title,  \n" +
                "\t\tLEFT(m.market_content, 90) as market_content, \n" +
                "\t\tm.market_image,  \n" +
                "\t\tm.market_soldout,  \n" +
                "\t\tm.market_hit,  \n" +
                "\t\tm.market_created_at,  \n" +
                "\t\tIF (ml.mem_idx22 is null, false, true) as mem_liked \n" +
                "\t\tfrom Market m  \n" +
                "\t\tleft join ( \n" +
                "\t\t\tselect \n" +
                "\t\t\t\tmarket_idx, mem_idx22  \n" +
                "\t\t\tfrom Market_like  \n" +
                "\t\t\twhere mem_idx22 = ?) ml \n" +
                "\t\ton m.market_idx = ml.market_idx\n" +
                "\t\t) a \n" +
                "left join ( \n" +
                "\tselect market_idx, count(*) as market_like_count \n" +
                "\tfrom Market_like \n" +
                "\tgroup by market_idx \n" +
                ") b \n" +
                "on a.market_idx = b.market_idx\n" +
                "where market_soldout in " + soldout + "\n" +
                "order by a.market_created_at DESC\n" +
                "limit ?, ?;";
        try {
            List<GetMarketFeedRes> result = jdbcTemplate.query(query,
                    marketFeedByCategoryRowMapper(),
                    userIdx, page * 9, Constant.FEED_PER_PAGE);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public List<GetMarketFeedRes> getFeedByCategory(String category, int userIdx, String soldout, int page) {
        String query = "select a.*, b.market_like_count \n" +
                "from ( \n" +
                "\tselect  \n" +
                "\t\tm.market_idx, \n" +
                "\t\tm.mem_idx,  \n" +
                "\t\tm.market_group,  \n" +
                "\t\tm.market_price,  \n" +
                "\t\tm.market_title,  \n" +
                "\t\tLEFT(m.market_content, 90) as market_content,\n" +
                "\t\tm.market_image,  \n" +
                "\t\tm.market_soldout,  \n" +
                "\t\tm.market_hit,  \n" +
                "\t\tm.market_created_at,  \n" +
                "\t\tIF (ml.mem_idx22 is null, false, true) as mem_liked \n" +
                "\tfrom Market m  \n" +
                "\tleft join ( \n" +
                "\t\tselect market_idx, mem_idx22  \n" +
                "\t\tfrom Market_like  \n" +
                "\t\twhere mem_idx22 = ?) ml \n" +
                "\ton m.market_idx = ml.market_idx  \n" +
                "\twhere m.market_group = ? and m.market_soldout in " + soldout + "\n" +
                "\t) a \n" +
                "left join ( \n" +
                "\tselect market_idx, count(*) as market_like_count \n" +
                "\tfrom Market_like \n" +
                "\tgroup by market_idx \n" +
                ") b \n" +
                "on a.market_idx = b.market_idx \n" +
                "order by a.market_created_at DESC \n" +
                "limit ?, ?;";
        try {
            List<GetMarketFeedRes> result = jdbcTemplate.query(query,
                    marketFeedByCategoryRowMapper(),
                    userIdx, category, page * Constant.FEED_PER_PAGE, Constant.FEED_PER_PAGE);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public List<GetMarketFeedRes> getAllHotFeed(int userIdx, String soldout, int page) {
        String query = "select e.*, f.market_like_count \n" +
                "from ( \n" +
                "\tselect c.*, if(d.mem_idx22 is null, false, true) as mem_liked \n" +
                "\tfrom (\n" +
                "\t\tselect a.* from (\n" +
                "\t\tselect \n" +
                "\t\t\tmarket_idx, \n" +
                "\t\t\tmem_idx, \n" +
                "\t\t\tmarket_group, \n" +
                "\t\t\tmarket_price, \n" +
                "\t\t\tmarket_title,  \n" +
                "\t\t\tleft(market_content, 90) as market_content,\n" +
                "\t\t\tmarket_image,  \n" +
                "\t\t\tmarket_soldout,  \n" +
                "\t\t\tmarket_hit,  \n" +
                "\t\t\tmarket_created_at \n" +
                "\t\t\tfrom Market\n" +
                "            where market_soldout in " + soldout + ") a \n" +
                "\t\t\tinner join ( \n" +
                "\t\t\t\tselect market_idx \n" +
                "                from Hot_market_feed  \n" +
                "                limit ?, ?) b \n" +
                "\t\t\ton a.market_idx = b.market_idx ) c \n" +
                "\t\tleft join ( \n" +
                "\t\t\tselect market_idx, mem_idx22 \n" +
                "\t\t\tfrom Market_like \n" +
                "\t\t\twhere mem_idx22 = ?\n" +
                "\t\t) d \n" +
                "\t\ton c.market_idx = d.market_idx \n" +
                "\t) e \n" +
                "left join ( \n" +
                "\tselect market_idx, count(*) as market_like_count \n" +
                "\tfrom Market_like \n" +
                "\tgroup by market_idx) f \n" +
                "on e.market_idx = f.market_idx  \n" +
                "order by e.market_created_at DESC;";
        try {
            List<GetMarketFeedRes> result = jdbcTemplate.query(query,
                    marketFeedByCategoryRowMapper(),
                    page * Constant.HOT_MARKET_FEED_PER_PAGE, Constant.HOT_MARKET_FEED_PER_PAGE, userIdx);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public List<GetMarketFeedRes> getHotFeedByCategory(String categoryIdx, int userIdx, String soldout, int page) {
        String query = "select e.*, f.market_like_count \n" +
                "from ( \n" +
                "\tselect c.*, if(d.mem_idx22 is null, false, true) as mem_liked \n" +
                "\tfrom (\n" +
                "\t\tselect a.* from (\n" +
                "\t\tselect \n" +
                "\t\t\tmarket_idx, \n" +
                "\t\t\tmem_idx, \n" +
                "\t\t\tmarket_group, \n" +
                "\t\t\tmarket_price, \n" +
                "\t\t\tmarket_title,  \n" +
                "\t\t\tleft(market_content, 90) as market_content,\n" +
                "\t\t\tmarket_image,  \n" +
                "\t\t\tmarket_soldout,  \n" +
                "\t\t\tmarket_hit,  \n" +
                "\t\t\tmarket_created_at \n" +
                "\t\t\tfrom Market\n" +
                "            where market_soldout in " + soldout + ") a \n" +
                "\t\t\tinner join ( \n" +
                "\t\t\t\tselect market_idx \n" +
                "                from Hot_market_feed  \n" +
                "                where market_category = ?\n" +
                "                limit ?, ?) b \n" +
                "\t\t\ton a.market_idx = b.market_idx ) c \n" +
                "\t\tleft join ( \n" +
                "\t\t\tselect market_idx, mem_idx22 \n" +
                "\t\t\tfrom Market_like \n" +
                "\t\t\twhere mem_idx22 = ?\n" +
                "\t\t) d \n" +
                "\t\ton c.market_idx = d.market_idx \n" +
                "\t) e \n" +
                "left join ( \n" +
                "\tselect market_idx, count(*) as market_like_count \n" +
                "\tfrom Market_like \n" +
                "\tgroup by market_idx) f \n" +
                "on e.market_idx = f.market_idx  \n" +
                "order by e.market_created_at DESC;";

        try {
            List<GetMarketFeedRes> result = jdbcTemplate.query(query,
                    marketFeedByCategoryRowMapper(),
                    categoryIdx, page * Constant.HOT_MARKET_FEED_PER_PAGE, Constant.HOT_MARKET_FEED_PER_PAGE, userIdx);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public void feedLike(int userIdx, int marketIdx, String isLike, int feedUserIdx) {

        if (isLike.equals("False")) {
            String deleteQuery = "delete from Market_like where market_idx = ? and mem_idx22 = ?";

            try {
                jdbcTemplate.update(deleteQuery, marketIdx, userIdx);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        else {
            String query = "insert into Market_like values (null, ?, ?, ?, ?)";

            try {
                jdbcTemplate.update(query, marketIdx, feedUserIdx, 1, userIdx);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public List<GetMarketFeedRes> getFeedByUserIdx(int userIdx, int page, int myUserIdx) {
        String query = "select c.*, if(d.mem_idx22 is null, false, true) as mem_liked\n" +
                "from (\n" +
                "\tselect a.*, b.market_like_count\n" +
                "\tfrom (\n" +
                "\t\tselect\n" +
                "\t\t\tmarket_idx,\n" +
                "\t\t\tmem_idx,\n" +
                "\t\t\tmarket_group,\n" +
                "\t\t\tmarket_price,\n" +
                "\t\t\tmarket_title, \n" +
                "\t\t\tleft(market_content, 90) as market_content, \n" +
                "\t\t\tmarket_image, \n" +
                "\t\t\tmarket_soldout, \n" +
                "\t\t\tmarket_hit, \n" +
                "\t\t\tmarket_created_at\n" +
                "\t\tfrom Market \n" +
                "\t\twhere mem_idx = ?\n" +
                "\t\torder by market_created_at DESC\n" +
                "        limit ?, ?\n" +
                "\t) a\n" +
                "\tleft join (\n" +
                "\t\tselect market_idx, count(*) as market_like_count\n" +
                "\t\tfrom Market_like\n" +
                "\t\tgroup by market_idx\n" +
                "\t) b\n" +
                "\ton a.market_idx = b.market_idx\n" +
                ") c\n" +
                "left join (\n" +
                "\tselect market_idx, mem_idx22\n" +
                "    from Market_like\n" +
                "    where mem_idx22 = ?) d\n" +
                "on c.market_idx = d.market_idx;";

        try {
            List<GetMarketFeedRes> result = jdbcTemplate.query(query, marketFeedByUserIdxRowMapper(),
                    userIdx, page * Constant.FEED_PER_PAGE, Constant.FEED_PER_PAGE, myUserIdx);
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
            feedRes.setUserProfileUrl(rs.getString("mem_profile_url"));
            feedRes.setCategory(rs.getInt("market_group"));
            feedRes.setTitle(rs.getString("market_title"));
            feedRes.setContent(rs.getString("market_content"));
            feedRes.setPrice(rs.getInt("market_price"));
            feedRes.setSoldout(rs.getString("market_soldout"));
            feedRes.setImage(rs.getString("image_url"));
            feedRes.setLikeCount(rs.getInt("market_like_count"));
            feedRes.setUserLiked(rs.getBoolean("mem_liked"));
            feedRes.setHit(rs.getInt("market_hit"));
            feedRes.setCreatedAt(rs.getTimestamp("market_created_at"));
            return feedRes;
        };
    }

    private RowMapper<GetMarketFeedRes> marketFeedByCategoryRowMapper() {
        return (rs, rowNum) -> {
            GetMarketFeedRes marketFeed = new GetMarketFeedRes();
            marketFeed.setMarketIdx(rs.getInt("market_idx"));
            marketFeed.setUserIdx(rs.getInt("mem_idx"));
            marketFeed.setCategory(rs.getInt("market_group"));
            marketFeed.setTitle(rs.getString("market_title"));
            marketFeed.setContent(rs.getString("market_content"));
            marketFeed.setPrice(rs.getInt("market_price"));
            marketFeed.setSoldout(rs.getString("market_soldout"));
            marketFeed.setImage(rs.getString("market_image"));
            marketFeed.setLikeCount(rs.getInt("market_like_count"));
            marketFeed.setHit(rs.getInt("market_hit"));
            marketFeed.setCreatedAt(rs.getTimestamp("market_created_at"));
            marketFeed.setUserLiked(rs.getBoolean("mem_liked"));
            return marketFeed;
        };
    }

    private RowMapper<GetMarketFeedRes> marketFeedByUserIdxRowMapper() {
        return (rs, rowNum) -> {
            GetMarketFeedRes marketFeed = new GetMarketFeedRes();
            marketFeed.setMarketIdx(rs.getInt("market_idx"));
            marketFeed.setUserIdx(rs.getInt("mem_idx"));
            marketFeed.setCategory(rs.getInt("market_group"));
            marketFeed.setTitle(rs.getString("market_title"));
            marketFeed.setContent(rs.getString("market_content"));
            marketFeed.setPrice(rs.getInt("market_price"));
            marketFeed.setSoldout(rs.getString("market_soldout"));
            marketFeed.setImage(rs.getString("market_image"));
            marketFeed.setHit(rs.getInt("market_hit"));
            marketFeed.setCreatedAt(rs.getTimestamp("market_created_at"));
            marketFeed.setLikeCount(rs.getInt("market_like_count"));
            marketFeed.setUserLiked(rs.getBoolean("mem_liked"));
            return marketFeed;
        };
    }

    private RowMapper<Member> getFeedMemberIdxRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setMemIdx(rs.getInt("mem_idx"));
            return member;
        };
    }
}