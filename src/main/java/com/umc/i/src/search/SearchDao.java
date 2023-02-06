package com.umc.i.src.search;

import com.umc.i.config.Constant;
import com.umc.i.src.market.feed.model.GetMarketFeedRes;
import com.umc.i.src.member.model.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class SearchDao implements SearchRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<GetMarketFeedRes> searchAllMarketFeedByKeywordInLatest(int userIdx,
                                                                       String search_keyword,
                                                                       int page) {
        String query = "select a.*, b.market_like_count\n" +
                "from (\n" +
                "\tselect m.*, if(ml.mem_idx22 is null, false, true) as mem_liked\n" +
                "    from (\n" +
                "\t\tselect \n" +
                "\t\t\tm.market_idx,\n" +
                "\t\t\tm.mem_idx,\n" +
                "\t\t\tm.market_group, \n" +
                "\t\t\tm.market_price, \n" +
                "\t\t\tm.market_title, \n" +
                "\t\t\tm.market_image, \n" +
                "\t\t\tm.market_soldout,\n" +
                "\t\t\tm.market_hit,\n" +
                "\t\t\tm.market_created_at\n" +
                "\t\tfrom Market m \n" +
                "        where market_title like \"%" + search_keyword +"%\" \n" +
                "        order by market_created_at DESC\n" +
                "        limit ?, ? ) m\n" +
                "\tleft join (\n" +
                "\t\tselect market_idx, mem_idx22\n" +
                "        from market_like\n" +
                "        where mem_idx22 = ?\n" +
                "\t) ml\n" +
                "\ton m.market_idx = ml.market_idx\n" +
                ") a\n" +
                "left join (\n" +
                "\tselect market_idx, count(*) as market_like_count\n" +
                "\tfrom Market_like\n" +
                "\tgroup by market_idx\n" +
                ") b\n" +
                "on a.market_idx = b.market_idx;";

        try {
            return jdbcTemplate.query(query,
                    marketFeedByCategoryRowMapper(),
                    page * Constant.FEED_PER_PAGE, Constant.FEED_PER_PAGE, userIdx);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    @Override
    public List<GetMarketFeedRes> searchCategoryMarketFeedByKeywordInLatest(int userIdx,
                                                                            String categoryIdx,
                                                                            String search_keyword,
                                                                            int page) {
        String query = "select a.*, b.market_like_count\n" +
                "from (\n" +
                "\tselect m.*, if(ml.mem_idx22 is null, false, true) as mem_liked\n" +
                "    from (\n" +
                "\t\tselect \n" +
                "\t\t\tm.market_idx,\n" +
                "\t\t\tm.mem_idx,\n" +
                "\t\t\tm.market_group, \n" +
                "\t\t\tm.market_price, \n" +
                "\t\t\tm.market_title, \n" +
                "\t\t\tm.market_image, \n" +
                "\t\t\tm.market_soldout,\n" +
                "\t\t\tm.market_hit,\n" +
                "\t\t\tm.market_created_at\n" +
                "\t\tfrom Market m \n" +
                "        where market_title like \"%" + search_keyword + "%\" and market_group = ?\n" +
                "        order by market_created_at DESC\n" +
                "        limit ?, ? ) m\n" +
                "\tleft join (\n" +
                "\t\tselect market_idx, mem_idx22\n" +
                "        from market_like\n" +
                "        where mem_idx22 = ?\n" +
                "\t) ml\n" +
                "\ton m.market_idx = ml.market_idx\n" +
                ") a\n" +
                "left join (\n" +
                "\tselect market_idx, count(*) as market_like_count\n" +
                "\tfrom Market_like\n" +
                "\tgroup by market_idx\n" +
                ") b\n" +
                "on a.market_idx = b.market_idx;";

        try {
            return jdbcTemplate.query(query, marketFeedByCategoryRowMapper(),
                    categoryIdx, page * Constant.FEED_PER_PAGE, Constant.FEED_PER_PAGE, userIdx);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    @Override
    public List<GetMarketFeedRes> searchAllMarketFeedByKeywordByTitleContentInLatest(int userIdx,
                                                                                     String search_keyword,
                                                                                     int page) {
        String query = "select a.*, b.market_like_count\n" +
                "from (\n" +
                "\tselect m.*, if(ml.mem_idx22 is null, false, true) as mem_liked\n" +
                "    from (\n" +
                "\t\tselect \n" +
                "\t\t\tm.market_idx,\n" +
                "\t\t\tm.mem_idx,\n" +
                "\t\t\tm.market_group, \n" +
                "\t\t\tm.market_price, \n" +
                "\t\t\tm.market_title, \n" +
                "\t\t\tm.market_image, \n" +
                "\t\t\tm.market_soldout,\n" +
                "\t\t\tm.market_hit,\n" +
                "\t\t\tm.market_created_at\n" +
                "\t\tfrom Market m \n" +
                "        where 1market_title like \"%" + search_keyword + "%\" or market_content like \"%" + search_keyword + "%\" \n" +
                "        order by market_created_at DESC\n" +
                "        limit ?, ? ) m\n" +
                "\tleft join (\n" +
                "\t\tselect market_idx, mem_idx22\n" +
                "        from market_like\n" +
                "        where mem_idx22 = ?\n" +
                "\t) ml\n" +
                "\ton m.market_idx = ml.market_idx\n" +
                ") a\n" +
                "left join (\n" +
                "\tselect market_idx, count(*) as market_like_count\n" +
                "\tfrom Market_like\n" +
                "\tgroup by market_idx\n" +
                ") b\n" +
                "on a.market_idx = b.market_idx;";

        try {
            return jdbcTemplate.query(query, marketFeedByCategoryRowMapper(),
                    page * Constant.FEED_PER_PAGE, Constant.FEED_PER_PAGE, userIdx);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    @Override
    public List<GetMarketFeedRes> searchCategoryMarketFeedByKeywordByTitleContentInLatest(int userIdx,
                                                                                          String categoryIdx,
                                                                                          String search_keyword,
                                                                                          int page) {
        String query = "select a.*, b.market_like_count\n" +
                "from (\n" +
                "\tselect m.*, if(ml.mem_idx22 is null, false, true) as mem_liked\n" +
                "    from (\n" +
                "\t\tselect \n" +
                "\t\t\tm.market_idx,\n" +
                "\t\t\tm.mem_idx,\n" +
                "\t\t\tm.market_group, \n" +
                "\t\t\tm.market_price, \n" +
                "\t\t\tm.market_title, \n" +
                "\t\t\tm.market_image, \n" +
                "\t\t\tm.market_soldout,\n" +
                "\t\t\tm.market_hit,\n" +
                "\t\t\tm.market_created_at\n" +
                "\t\tfrom Market m \n" +
                "        where (market_title like \"%" + search_keyword + "%\" or market_content like \"%" + search_keyword + "%\") and market_group = ?\n" +
                "        order by market_created_at DESC\n" +
                "        limit ?, ? ) m\n" +
                "\tleft join (\n" +
                "\t\tselect market_idx, mem_idx22\n" +
                "        from market_like\n" +
                "        where mem_idx22 = ?\n" +
                "\t) ml\n" +
                "\ton m.market_idx = ml.market_idx\n" +
                ") a\n" +
                "left join (\n" +
                "\tselect market_idx, count(*) as market_like_count\n" +
                "\tfrom Market_like\n" +
                "\tgroup by market_idx\n" +
                ") b\n" +
                "on a.market_idx = b.market_idx;";

        try {
            return jdbcTemplate.query(query, marketFeedByCategoryRowMapper(),
                    categoryIdx, page * Constant.FEED_PER_PAGE, Constant.FEED_PER_PAGE, userIdx);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    @Override
    public List<GetMarketFeedRes> searchAllMarketFeedByKeywordByNicknameInLatest(int userIdx,
                                                                                 String search_keyword,
                                                                                 int page) {
        Integer searchMemberIdx = findMemberNickNameByMemberIdx(search_keyword);

        String query = "select a.*, b.market_like_count\n" +
                "from (\n" +
                "\tselect m.*, if(ml.mem_idx22 is null, false, true) as mem_liked\n" +
                "    from (\n" +
                "\t\tselect \n" +
                "\t\t\tm.market_idx,\n" +
                "\t\t\tm.mem_idx,\n" +
                "\t\t\tm.market_group, \n" +
                "\t\t\tm.market_price, \n" +
                "\t\t\tm.market_title, \n" +
                "\t\t\tm.market_image, \n" +
                "\t\t\tm.market_soldout,\n" +
                "\t\t\tm.market_hit,\n" +
                "\t\t\tm.market_created_at\n" +
                "\t\tfrom Market m \n" +
                "        where mem_idx = ?" +
                "        order by market_created_at DESC\n" +
                "        limit ?, ? ) m\n" +
                "\tleft join (\n" +
                "\t\tselect market_idx, mem_idx22\n" +
                "        from market_like\n" +
                "        where mem_idx22 = ?\n" +
                "\t) ml\n" +
                "\ton m.market_idx = ml.market_idx\n" +
                ") a\n" +
                "left join (\n" +
                "\tselect market_idx, count(*) as market_like_count\n" +
                "\tfrom Market_like\n" +
                "\tgroup by market_idx\n" +
                ") b\n" +
                "on a.market_idx = b.market_idx;";

        try {
            return jdbcTemplate.query(query, marketFeedByCategoryRowMapper(),
                    searchMemberIdx, page * Constant.FEED_PER_PAGE, Constant.FEED_PER_PAGE, userIdx);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    @Override
    public List<GetMarketFeedRes> searchCategoryMarketFeedByKeywordByNicknameInLatest(int userIdx,
                                                                                      String categoryIdx,
                                                                                      String search_keyword,
                                                                                      int page) {
        Integer searchMemberIdx = findMemberNickNameByMemberIdx(search_keyword);

        String query = "select a.*, b.market_like_count\n" +
                "from (\n" +
                "\tselect m.*, if(ml.mem_idx22 is null, false, true) as mem_liked\n" +
                "    from (\n" +
                "\t\tselect \n" +
                "\t\t\tm.market_idx,\n" +
                "\t\t\tm.mem_idx,\n" +
                "\t\t\tm.market_group, \n" +
                "\t\t\tm.market_price, \n" +
                "\t\t\tm.market_title, \n" +
                "\t\t\tm.market_image, \n" +
                "\t\t\tm.market_soldout,\n" +
                "\t\t\tm.market_hit,\n" +
                "\t\t\tm.market_created_at\n" +
                "\t\tfrom Market m \n" +
                "        where mem_idx = ? and market_group = ?\n" +
                "        order by market_created_at DESC\n" +
                "        limit ?, ? ) m\n" +
                "\tleft join (\n" +
                "\t\tselect market_idx, mem_idx22\n" +
                "        from market_like\n" +
                "        where mem_idx22 = ?\n" +
                "\t) ml\n" +
                "\ton m.market_idx = ml.market_idx\n" +
                ") a\n" +
                "left join (\n" +
                "\tselect market_idx, count(*) as market_like_count\n" +
                "\tfrom Market_like\n" +
                "\tgroup by market_idx\n" +
                ") b\n" +
                "on a.market_idx = b.market_idx;";

        try {
            return jdbcTemplate.query(query, marketFeedByCategoryRowMapper(),
                    searchMemberIdx, categoryIdx, page * Constant.FEED_PER_PAGE, Constant.FEED_PER_PAGE, userIdx);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private Integer findMemberNickNameByMemberIdx(String userNickname) {
        String query = "select mem_idx from Member where mem_nickname = ?";

        try {
            List<Member> result = jdbcTemplate.query(query, memberRowMapper(), userNickname);
            return result.get(0).getMemIdx();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    private RowMapper<Member> memberRowMapper() {
        return ((rs, rowNum) -> {
            Member member = new Member();
            member.setMemIdx(rs.getInt("mem_idx"));
            return member;
        });
    }

    private RowMapper<GetMarketFeedRes> marketFeedByCategoryRowMapper() {
        return (rs, rowNum) -> {
            GetMarketFeedRes marketFeed = new GetMarketFeedRes();
            marketFeed.setMarketIdx(rs.getInt("market_idx"));
            marketFeed.setUserIdx(rs.getInt("mem_idx"));
            marketFeed.setCategory(rs.getInt("market_group"));
            marketFeed.setTitle(rs.getString("market_title"));
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
}
