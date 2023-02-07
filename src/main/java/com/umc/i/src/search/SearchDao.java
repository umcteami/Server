package com.umc.i.src.search;

import com.umc.i.config.Constant;
import com.umc.i.src.feeds.model.get.GetAllFeedsRes;
import com.umc.i.src.market.feed.model.GetMarketFeedRes;
import com.umc.i.src.member.model.Member;
import com.umc.i.src.review.model.get.GetAllReviewsRes;
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
                "        from Market_like\n" +
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
                "        from Market_like\n" +
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
                "        from Market_like\n" +
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
                "        from Market_like\n" +
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
                "        from Market_like\n" +
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
                "        from Market_like\n" +
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

    @Override
    public List<GetAllReviewsRes> searchAllReviewFeedByKeywordByContentInLatest(String search_keyword, int page) {
        String query = "select review_idx, sell_mem_idx, A.mem_nickname as seller_nick, buy_mem_idx, B.mem_nickname as buyer_nick, \n" +
                "I.Market_review.review_goods, review_content, review_hit, review_created_at \n" +
                "from Market_review, Member A, Member B\n" +
                "where Market_review.sell_mem_idx = A.mem_idx && Market_review.buy_mem_idx = B.mem_idx and review_content like \"%" + search_keyword + "%\"\n" +
                "order by review_created_at desc limit ?, ?;";

        try {
            return jdbcTemplate.query(query,
                    (rs, rowNum) -> new GetAllReviewsRes(
                            rs.getInt("review_idx"),
                            rs.getInt("buy_mem_idx"),
                            rs.getInt("sell_mem_idx"),
                            rs.getString("buyer_nick"),
                            rs.getString("seller_nick"),
                            rs.getString("review_goods"),
                            rs.getInt("review_hit"),
                            rs.getString("review_created_at")),
                    page * Constant.FEED_PER_PAGE, Constant.FEED_PER_PAGE);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    @Override
    public List<GetAllFeedsRes> searchAllDairyFeedByKeywordByTitleInLatest(String search_keyword, int page) {
        String query = "select D.diary_idx, diary_roomType, D.mem_idx, mem_nickname, diary_title, diary_hit, diary_created_at, \n" +
                "if(D.diary_idx = Cmt.diary_idx, comment_cnt, 0) as comment_cnt\n" +
                "from Diary_feed D, (select diary_idx, count(*) as comment_cnt from Diary_comment group by diary_idx) Cmt\n" +
                "where diary_title like \"%" + search_keyword + "%\" and diary_blame < 10  group by diary_idx order by diary_idx desc limit ?, ?;";
        try {
            return jdbcTemplate.query(query, diaryFeedRowMapper(), page * Constant.FEED_PER_PAGE, Constant.FEED_PER_PAGE);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public List<GetAllFeedsRes> searchCategoryDairyFeedByKeywordByTitleInLatest(String categoryIdx, String search_keyword, int page) {
        String query = "select D.diary_idx, diary_roomType, D.mem_idx, mem_nickname, diary_title, diary_hit, diary_created_at, \n" +
                "if(D.diary_idx = Cmt.diary_idx, comment_cnt, 0) as comment_cnt\n" +
                "from Diary_feed D, (select diary_idx, count(*) as comment_cnt from Diary_comment group by diary_idx) Cmt\n" +
                "where diary_roomType = ? and diary_title like \"%" + search_keyword + "%\" and diary_blame < 10  group by diary_idx order by diary_idx desc limit ?, ?;";
        try {
            return jdbcTemplate.query(query, diaryFeedRowMapper(), categoryIdx, page * Constant.FEED_PER_PAGE, Constant.FEED_PER_PAGE);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public List<GetAllFeedsRes> searchAllDairyFeedByKeywordByTitleContentInLatest(String search_keyword, int page) {
        String query = "select D.diary_idx, diary_roomType, D.mem_idx, mem_nickname, diary_title, diary_hit, diary_created_at, \n" +
                "if(D.diary_idx = Cmt.diary_idx, comment_cnt, 0) as comment_cnt\n" +
                "from Diary_feed D, (select diary_idx, count(*) as comment_cnt from Diary_comment group by diary_idx) Cmt\n" +
                "where (diary_title like \"%"+ search_keyword +"%\" or diary_content like \"%" + search_keyword +"%\") and diary_blame < 10  group by diary_idx order by diary_idx desc limit ?, ?;";
        try {
            return jdbcTemplate.query(query, diaryFeedRowMapper(), page * Constant.FEED_PER_PAGE, Constant.FEED_PER_PAGE);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public List<GetAllFeedsRes> searchCategoryDairyFeedByKeywordByTitleContentInLatest(String categoryIdx, String search_keyword, int page) {
        String query = "select D.diary_idx, diary_roomType, D.mem_idx, mem_nickname, diary_title, diary_hit, diary_created_at, \n" +
                "if(D.diary_idx = Cmt.diary_idx, comment_cnt, 0) as comment_cnt\n" +
                "from Diary_feed D, (select diary_idx, count(*) as comment_cnt from Diary_comment group by diary_idx) Cmt\n" +
                "where diary_roomType = ? and (diary_title like \"%" + search_keyword + "%\" or diary_content like \"%" + search_keyword + "%\") and diary_blame < 10  group by diary_idx order by diary_idx desc limit ?, ?;";
        try {
            return jdbcTemplate.query(query, diaryFeedRowMapper(), categoryIdx, page * Constant.FEED_PER_PAGE, Constant.FEED_PER_PAGE);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public List<GetAllFeedsRes> searchAllDairyFeedByKeywordByMemberNicknameInLatest(String search_keyword, int page) {
        String query = "select D.diary_idx, diary_roomType, D.mem_idx, mem_nickname, diary_title, diary_hit, diary_created_at, \n" +
                "if(D.diary_idx = Cmt.diary_idx, comment_cnt, 0) as comment_cnt\n" +
                "from Diary_feed D, (select diary_idx, count(*) as comment_cnt from Diary_comment group by diary_idx) Cmt\n" +
                "where mem_nickname like \"%" + search_keyword + "%\" and diary_blame < 10  group by diary_idx order by diary_idx desc limit ?, ?;";
        try {
            return jdbcTemplate.query(query, diaryFeedRowMapper(), page * Constant.FEED_PER_PAGE, Constant.FEED_PER_PAGE);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public List<GetAllFeedsRes> searchCategoryDairyFeedByKeywordByMemberNicknameInLatest(String categoryIdx, String search_keyword, int page) {
        String query = "select D.diary_idx, diary_roomType, D.mem_idx, mem_nickname, diary_title, diary_hit, diary_created_at, \n" +
                "if(D.diary_idx = Cmt.diary_idx, comment_cnt, 0) as comment_cnt\n" +
                "from Diary_feed D, (select diary_idx, count(*) as comment_cnt from Diary_comment group by diary_idx) Cmt\n" +
                "where diary_roomType = ? and mem_nickname like \"%" + search_keyword + "%\" and diary_blame < 10  group by diary_idx order by diary_idx desc limit ?, ?;";
        try {
            return jdbcTemplate.query(query, diaryFeedRowMapper(), categoryIdx, page * Constant.FEED_PER_PAGE, Constant.FEED_PER_PAGE);
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

    private RowMapper<GetAllFeedsRes> diaryFeedRowMapper() {
        return (rs, rowNum) -> {
            GetAllFeedsRes res = new GetAllFeedsRes();
            res.setBoardType(rs.getInt("diary_roomType"));
            res.setFeedIdx(rs.getInt("diary_idx"));
            res.setMemIdx(rs.getInt("mem_idx"));
            res.setMemNick(rs.getString("mem_nickname"));
            res.setTitle(rs.getString("diary_title"));
            res.setHit(rs.getInt("diary_hit"));
            res.setCommentCnt(rs.getInt("comment_cnt"));
            res.setCreateAt(rs.getString("diary_created_at"));
            return res;
        };
    }
}
