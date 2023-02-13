package com.umc.i.src.search;

import com.umc.i.src.feeds.model.get.GetAllFeedsRes;
import com.umc.i.src.market.feed.model.GetMarketFeedRes;
import com.umc.i.src.review.model.get.GetAllReviewsRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final SearchDao searchDao;

    @Override
    public List<GetMarketFeedRes> searchAllMarketFeedByKeywordByTitleInLatest(int userIdx, String search_keyword, int page) {
        return searchDao.searchAllMarketFeedByKeywordInLatest(userIdx, search_keyword, page);
    }

    @Override
    public List<GetMarketFeedRes> searchCategoryMarketFeedByKeywordByTitleInLatest(int userIdx,
                                                                                   String categoryIdx,
                                                                                   String search_keyword,
                                                                                   int page) {
        return searchDao.searchCategoryMarketFeedByKeywordInLatest(userIdx,
                categoryIdx,
                search_keyword,
                page);
    }

    @Override
    public List<GetMarketFeedRes> searchAllMarketFeedByKeywordByTitleContentInLatest(int userIdx,
                                                                                     String search_keyword,
                                                                                     int page) {
        return searchDao.searchAllMarketFeedByKeywordByTitleContentInLatest(userIdx,
                search_keyword,
                page);
    }

    @Override
    public List<GetMarketFeedRes> searchCategoryMarketFeedByKeywordByTitleContentInLatest(int userIdx,
                                                                                          String categoryIdx,
                                                                                          String search_keyword,
                                                                                          int page) {
        return searchDao.searchCategoryMarketFeedByKeywordByTitleContentInLatest(userIdx,
                categoryIdx,
                search_keyword,
                page);
    }

    @Override
    public List<GetMarketFeedRes> searchAllMarketFeedByKeywordByNicknameInLatest(int userIdx,
                                                                                 String search_keyword,
                                                                                 int page) {
        return searchDao.searchAllMarketFeedByKeywordByNicknameInLatest(userIdx, search_keyword, page);
    }

    @Override
    public List<GetMarketFeedRes> searchCategoryMarketFeedByKeywordByNicknameInLatest(int userIdx,
                                                                                      String categoryIdx,
                                                                                      String search_keyword,
                                                                                      int page) {
        return searchDao.searchCategoryMarketFeedByKeywordByNicknameInLatest(userIdx,
                categoryIdx,
                search_keyword,
                page);
    }

    @Override
    public List<GetAllReviewsRes> searchAllReviewFeedByKeywordByContentInLatest(String search_keyword, int page) {
        return searchDao.searchAllReviewFeedByKeywordByContentInLatest(search_keyword, page);
    }

    @Override
    public List<GetAllFeedsRes> searchAllDairyFeedByKeywordByTitleInLatest(String search_keyword, int page) {
        return searchDao.searchAllDairyFeedByKeywordByTitleInLatest(search_keyword, page);
    }

    @Override
    public List<GetAllFeedsRes> searchCategoryDairyFeedByKeywordByTitleInLatest(String categoryIdx, String search_keyword, int page) {
        return searchDao.searchCategoryDairyFeedByKeywordByTitleInLatest(categoryIdx, search_keyword, page);
    }

    @Override
    public List<GetAllFeedsRes> searchAllDairyFeedByKeywordByTitleContentInLatest(String search_keyword, int page) {
        return searchDao.searchAllDairyFeedByKeywordByTitleContentInLatest(search_keyword, page);
    }

    @Override
    public List<GetAllFeedsRes> searchCategoryDairyFeedByKeywordByTitleContentInLatest(String categoryIdx, String search_keyword, int page) {
        return searchDao.searchCategoryDairyFeedByKeywordByTitleContentInLatest(categoryIdx, search_keyword, page);
    }

    @Override
    public List<GetAllFeedsRes> searchAllDairyFeedByKeywordByMemberNicknameInLatest(String search_keyword, int page) {
        return searchDao.searchAllDairyFeedByKeywordByMemberNicknameInLatest(search_keyword, page);
    }

    @Override
    public List<GetAllFeedsRes> searchCategoryDairyFeedByKeywordByMemberNicknameInLatest(String categoryIdx, String search_keyword, int page) {
        return searchDao.searchCategoryDairyFeedByKeywordByMemberNicknameInLatest(categoryIdx, search_keyword, page);
    }

    @Override
    public List<GetAllFeedsRes> searchAllStoryFeedByKeywordByTitleInLatest(String search_keyword, int page) {
        return searchDao.searchAllStoryFeedByKeywordByTitleInLatest(search_keyword, page);
    }

    @Override
    public List<GetAllFeedsRes> searchCategoryStoryFeedByKeywordByTitleInLatest(String categoryIdx, String search_keyword, int page) {
        return searchDao.searchCategoryStoryFeedByKeywordByTitleInLatest(categoryIdx, search_keyword, page);
    }

    @Override
    public List<GetAllFeedsRes> searchAllStoryFeedByKeywordByTitleContentInLatest(String search_keyword, int page) {
        return searchDao.searchAllStoryFeedByKeywordByTitleContentInLatest(search_keyword, page);
    }

    @Override
    public List<GetAllFeedsRes> searchCategoryStoryFeedByKeywordByTitleContentInLatest(String categoryIdx, String search_keyword, int page) {
        return searchDao.searchCategoryStoryFeedByKeywordByTitleContentInLatest(categoryIdx, search_keyword, page);
    }

    @Override
    public List<GetAllFeedsRes> searchAllStoryFeedByKeywordByMemberNicknameInLatest(String search_keyword, int page) {
        return searchDao.searchAllStoryFeedByKeywordByMemberNicknameInLatest(search_keyword, page);
    }

    @Override
    public List<GetAllFeedsRes> searchCategoryStoryFeedByKeywordByMemberNicknameInLatest(String categoryIdx, String search_keyword, int page) {
        return searchDao.searchCategoryStoryFeedByKeywordByMemberNicknameInLatest(categoryIdx, search_keyword, page);
    }

    @Override
    public List<GetAllFeedsRes> searchAllHomeFeedByKeywordByTitleInLatest(String search_keyword, int page) {
        return searchDao.searchAllHomeFeedByKeywordByTitleInLatest(search_keyword, page);
    }

    @Override
    public List<GetAllFeedsRes> searchAllHomeFeedByKeywordByTitleContentInLatest(String search_keyword, int page) {
        return searchDao.searchAllHomeFeedByKeywordByTitleContentInLatest(search_keyword, page);
    }

    @Override
    public List<GetAllFeedsRes> searchAllHomeFeedByKeywordByMemberNicknameInLatest(String search_keyword, int page) {
        return searchDao.searchAllHomeFeedByKeywordByMemberNicknameInLatest(search_keyword, page);
    }

    @Override
    public List<String> bestSearchKeyword() {
        // TODO Auto-generated method stub
        return null;
    }
}
