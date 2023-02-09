package com.umc.i.src.search;

import com.umc.i.src.market.feed.model.GetMarketFeedRes;
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
}
