package com.umc.i.src.search;


import com.umc.i.src.market.feed.model.GetMarketFeedRes;

import java.util.List;

public interface SearchService {

    List<GetMarketFeedRes> searchAllMarketFeedByKeyword(int userIdx, String search_keyword);

    List<GetMarketFeedRes> searchCategoryMarketFeedByKeyword(int userIdx, String categoryIdx, String search_keyword);
}
