package com.umc.i.src.search;

import com.umc.i.config.BaseResponse;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.config.Constant;
import com.umc.i.src.market.feed.model.GetMarketFeedReq;
import com.umc.i.src.market.feed.model.GetMarketFeedRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("market/search")
    public BaseResponse searchMarketFeedByKeyword(@RequestParam String category,
                                                  @RequestParam String search_keyword,
                                                  @RequestBody GetMarketFeedReq req) {

        if (!isCategoryValid(category)) {
            return new BaseResponse<>(BaseResponseStatus.MARKET_FEED_BY_CATEGORY_FAILED);
        }
        if (!isSearchKeywordValid(search_keyword)) {
            return new BaseResponse<>(BaseResponseStatus.SEARCH_KEYWORD_NULL_EXCEPTION);
        }

        int userIdx = req.getUserIdx();

        if (category == null) {
            List<GetMarketFeedRes> feedRes = searchService.searchAllMarketFeedByKeyword(userIdx, search_keyword);
            return new BaseResponse(feedRes);
        } else {
            String categoryIdx = Constant.MARKET_GOOD_CATEGORIES.get(category);
            List<GetMarketFeedRes> feedRes = searchService.searchCategoryMarketFeedByKeyword(userIdx, categoryIdx, search_keyword);
            return new BaseResponse(feedRes);
        }
    }

    private boolean isCategoryValid(String category) {
        return Constant.MARKET_GOOD_CATEGORIES.containsKey(category);
    }

    private boolean isSearchKeywordValid(String search_keyword) {
        return search_keyword != null;
    }
}

