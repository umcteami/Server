package com.umc.i.src.search;

import com.umc.i.src.feeds.model.get.GetAllDiaryRes;
import com.umc.i.src.feeds.model.get.GetAllFeedsRes;
import com.umc.i.src.market.feed.model.GetMarketFeedRes;
import com.umc.i.src.review.model.get.GetAllReviewsRes;
import com.umc.i.src.search.model.Keyword;

import java.util.List;

public interface SearchRepository {

    List<GetMarketFeedRes> searchAllMarketFeedByKeywordInLatest(int userIdx, String search_keyword, int page);

    List<GetMarketFeedRes> searchCategoryMarketFeedByKeywordInLatest(int userIdx, String categoryIdx, String search_keyword, int page);

    List<GetMarketFeedRes> searchAllMarketFeedByKeywordByTitleContentInLatest(int userIdx, String search_keyword, int page);

    List<GetMarketFeedRes> searchCategoryMarketFeedByKeywordByTitleContentInLatest(int userIdx, String categoryIdx, String search_keyword, int page);

    List<GetMarketFeedRes> searchAllMarketFeedByKeywordByNicknameInLatest(int userIdx, String search_keyword, int page);

    List<GetMarketFeedRes> searchCategoryMarketFeedByKeywordByNicknameInLatest(int userIdx, String categoryIdx, String search_keyword, int page);

    List<GetAllReviewsRes> searchAllReviewFeedByKeywordByContentInLatest(String search_keyword, int page);

    List<GetAllDiaryRes> searchAllDairyFeedByKeywordByTitleInLatest(String search_keyword, int page);

    List<GetAllDiaryRes> searchCategoryDairyFeedByKeywordByTitleInLatest(String categoryIdx, String search_keyword, int page);

    List<GetAllDiaryRes> searchAllDairyFeedByKeywordByTitleContentInLatest(String search_keyword, int page);

    List<GetAllDiaryRes> searchCategoryDairyFeedByKeywordByTitleContentInLatest(String categoryIdx, String search_keyword, int page);

    List<GetAllDiaryRes> searchAllDairyFeedByKeywordByMemberNicknameInLatest(String search_keyword, int page);

    List<GetAllDiaryRes> searchCategoryDairyFeedByKeywordByMemberNicknameInLatest(String categoryIdx, String search_keyword, int page);

    List<GetAllFeedsRes> searchAllStoryFeedByKeywordByTitleInLatest(String search_keyword, int page);

    List<GetAllFeedsRes> searchCategoryStoryFeedByKeywordByTitleInLatest(String categoryIdx, String search_keyword, int page);

    List<GetAllFeedsRes> searchAllStoryFeedByKeywordByTitleContentInLatest(String search_keyword, int page);

    List<GetAllFeedsRes> searchCategoryStoryFeedByKeywordByTitleContentInLatest(String categoryIdx, String search_keyword, int page);

    List<GetAllFeedsRes> searchAllStoryFeedByKeywordByMemberNicknameInLatest(String search_keyword, int page);

    List<GetAllFeedsRes> searchCategoryStoryFeedByKeywordByMemberNicknameInLatest(String categoryIdx, String search_keyword, int page);

    List<GetAllFeedsRes> searchAllHomeFeedByKeywordByTitleInLatest(String search_keyword, int page);

    List<GetAllFeedsRes> searchAllHomeFeedByKeywordByTitleContentInLatest(String search_keyword, int page);

    List<GetAllFeedsRes> searchAllHomeFeedByKeywordByMemberNicknameInLatest(String search_keyword, int page);

    List<Keyword> bestSearchKeyword();

    void updateSearchKeywordCnt(String search_keyword);
}
