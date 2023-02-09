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
    public BaseResponse searchMarketFeedByKeyword(@RequestParam(required = false) String category,
                                                  @RequestParam String search_keyword,
                                                  @RequestParam String search_target,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestBody GetMarketFeedReq req) {
        int userIdx = req.getUserIdx();

        if (!isCategoryValid(category)) {
            return new BaseResponse<>(BaseResponseStatus.MARKET_FEED_BY_CATEGORY_FAILED);
        }
        if (!isSearchKeywordValid(search_keyword)) {
            return new BaseResponse<>(BaseResponseStatus.SEARCH_KEYWORD_NULL_EXCEPTION);
        }
        if (!isSearchTargetValid(search_target)) {
            return new BaseResponse<>(BaseResponseStatus.SEARCH_TARGET_INVALID);
        }

        List<GetMarketFeedRes> feedRes = null;
        if (search_target.equals("title")) {
            if (category == null) {
                feedRes = searchService.searchAllMarketFeedByKeywordByTitleInLatest(userIdx, search_keyword, page);
            } else {
                String categoryIdx = Constant.MARKET_GOOD_CATEGORIES.get(category);
                feedRes = searchService.searchCategoryMarketFeedByKeywordByTitleInLatest(userIdx, categoryIdx, search_keyword, page);
            }
        } else if (search_target.equals("title_content")) {
            if (category == null) {
                feedRes = searchService.searchAllMarketFeedByKeywordByTitleContentInLatest(userIdx, search_keyword, page);
            } else {
                String categoryIdx = Constant.MARKET_GOOD_CATEGORIES.get(category);
                feedRes = searchService.searchCategoryMarketFeedByKeywordByTitleContentInLatest(userIdx, categoryIdx, search_keyword, page);
            }
        } else if (search_target.equals("member_nickname")) {
            if (category == null) {
                feedRes = searchService.searchAllMarketFeedByKeywordByNicknameInLatest(userIdx, search_keyword, page);
            } else {
                String categoryIdx = Constant.MARKET_GOOD_CATEGORIES.get(category);
                feedRes = searchService.searchCategoryMarketFeedByKeywordByNicknameInLatest(userIdx, categoryIdx, search_keyword, page);
            }
        }

        return new BaseResponse(feedRes);
    }

    private boolean isCategoryValid(String category) {
        return Constant.MARKET_GOOD_CATEGORIES.containsKey(category);
    }

    private boolean isSearchKeywordValid(String search_keyword) {
        return search_keyword != null && search_keyword.length() > 1;
    }

    private boolean isSearchTargetValid(String search_target) {
        return Constant.SEARCH_TARGET.stream().anyMatch(o -> o.equals(search_target));
    }
}

