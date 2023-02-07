package com.umc.i.src.search;


import com.umc.i.src.feeds.model.get.GetAllFeedsRes;
import com.umc.i.src.market.feed.model.GetMarketFeedRes;
import com.umc.i.src.review.model.get.GetAllReviewsRes;

import java.util.List;

public interface SearchService {

    List<GetMarketFeedRes> searchAllMarketFeedByKeywordByTitleInLatest(int userIdx, String search_keyword, int page);

    List<GetMarketFeedRes> searchCategoryMarketFeedByKeywordByTitleInLatest(int userIdx, String categoryIdx, String search_keyword, int page);

    List<GetMarketFeedRes> searchAllMarketFeedByKeywordByTitleContentInLatest(int userIdx, String search_keyword, int page);

    List<GetMarketFeedRes> searchCategoryMarketFeedByKeywordByTitleContentInLatest(int userIdx, String categoryIdx, String search_keyword, int page);

    List<GetMarketFeedRes> searchAllMarketFeedByKeywordByNicknameInLatest(int userIdx, String search_keyword, int page);

    List<GetMarketFeedRes> searchCategoryMarketFeedByKeywordByNicknameInLatest(int userIdx, String categoryIdx, String search_keyword, int page);

    List<GetAllReviewsRes> searchAllReviewFeedByKeywordByContentInLatest(String search_keyword, int page);

    List<GetAllFeedsRes> searchAllDairyFeedByKeywordByTitleInLatest(String search_keyword, int page);

    List<GetAllFeedsRes> searchCategoryDairyFeedByKeywordByTitleInLatest(String categoryIdx, String search_keyword, int page);

    List<GetAllFeedsRes> searchAllDairyFeedByKeywordByTitleContentInLatest(String search_keyword, int page);

    List<GetAllFeedsRes> searchCategoryDairyFeedByKeywordByTitleContentInLatest(String categoryIdx, String search_keyword, int page);

    List<GetAllFeedsRes> searchAllDairyFeedByKeywordByMemberNicknameInLatest(String search_keyword, int page);

    List<GetAllFeedsRes> searchCategoryDairyFeedByKeywordByMemberNicknameInLatest(String categoryIdx, String search_keyword, int page);
}
