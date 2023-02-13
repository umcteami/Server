package com.umc.i.src.search;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponse;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.config.Constant;
import com.umc.i.src.feeds.model.get.GetAllDiaryRes;
import com.umc.i.src.feeds.model.get.GetAllFeedsRes;
import com.umc.i.src.market.feed.model.GetMarketFeedReq;
import com.umc.i.src.market.feed.model.GetMarketFeedRes;
import com.umc.i.src.review.model.get.GetAllReviewsRes;
import com.umc.i.src.search.model.Keyword;
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

        if (!isMarketCategoryValid(category)) {
            return new BaseResponse<>(BaseResponseStatus.FEED_BY_CATEGORY_FAILED);
        }
        if (!isSearchKeywordValid(search_keyword)) {
            return new BaseResponse<>(BaseResponseStatus.SEARCH_KEYWORD_NULL_EXCEPTION);
        }
        if (!isSearchTargetValid(search_target)) {
            return new BaseResponse<>(BaseResponseStatus.SEARCH_TARGET_INVALID);
        }

        searchService.updateSearchKeywordCnt(search_keyword);

        List<GetMarketFeedRes> feedRes = null;
        switch (search_target) {
            case "title":
                if (category == null) {
                    feedRes = searchService.searchAllMarketFeedByKeywordByTitleInLatest(userIdx, search_keyword, page);
                } else {
                    String categoryIdx = Constant.MARKET_GOOD_CATEGORIES.get(category);
                    feedRes = searchService.searchCategoryMarketFeedByKeywordByTitleInLatest(userIdx, categoryIdx, search_keyword, page);
                }
                break;
            case "title_content":
                if (category == null) {
                    feedRes = searchService.searchAllMarketFeedByKeywordByTitleContentInLatest(userIdx, search_keyword, page);
                } else {
                    String categoryIdx = Constant.MARKET_GOOD_CATEGORIES.get(category);
                    feedRes = searchService.searchCategoryMarketFeedByKeywordByTitleContentInLatest(userIdx, categoryIdx, search_keyword, page);
                }
                break;
            case "member_nickname":
                if (category == null) {
                    feedRes = searchService.searchAllMarketFeedByKeywordByNicknameInLatest(userIdx, search_keyword, page);
                } else {
                    String categoryIdx = Constant.MARKET_GOOD_CATEGORIES.get(category);
                    feedRes = searchService.searchCategoryMarketFeedByKeywordByNicknameInLatest(userIdx, categoryIdx, search_keyword, page);
                }
                break;
        }

        return new BaseResponse(feedRes);
    }

    @GetMapping("review/search")
    public BaseResponse<List<GetAllReviewsRes>> searchReviewFeedByKeyword(@RequestParam String search_keyword,
                                                                          @RequestParam int page) throws BaseException {
        if (!isSearchKeywordValid(search_keyword)) {
            return new BaseResponse<>(BaseResponseStatus.SEARCH_KEYWORD_NULL_EXCEPTION);
        }

        try {
            List<GetAllReviewsRes> feedRes = searchService.searchAllReviewFeedByKeywordByContentInLatest(search_keyword, page);
            return new BaseResponse<>(feedRes);
        } catch (Exception e) {
                log.error(e.getMessage());
                throw new BaseException(BaseResponseStatus.GET_REVIEW_FAIL);
        }
    }

    @GetMapping("dairy/search")
    public BaseResponse searchDiaryFeedByKeyword(@RequestParam(required = false) String category,
                                                 @RequestParam String search_keyword,
                                                 @RequestParam String search_target,
                                                 @RequestParam(defaultValue = "0") int page) throws BaseException {
        if (!isDairyCategoryValid(category)) {
            return new BaseResponse<>(BaseResponseStatus.FEED_BY_CATEGORY_FAILED);
        }
        if (!isSearchKeywordValid(search_keyword)) {
            return new BaseResponse<>(BaseResponseStatus.SEARCH_KEYWORD_NULL_EXCEPTION);
        }
        if (!isSearchTargetValid(search_target)) {
            return new BaseResponse<>(BaseResponseStatus.SEARCH_TARGET_INVALID);
        }

        List<GetAllDiaryRes> feedRes = null;
        switch (search_target) {
            case "title":
                if (category == null) {
                    try {
                        feedRes = searchService.searchAllDairyFeedByKeywordByTitleInLatest(search_keyword, page);
                        return new BaseResponse<>(feedRes);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        throw new BaseException(BaseResponseStatus.GET_REVIEW_FAIL);
                    }
                } else {
                    try {
                        String categoryIdx = Constant.DAIRY_CATEGORIES.get(category);
                        feedRes = searchService.searchCategoryDairyFeedByKeywordByTitleInLatest(categoryIdx, search_keyword, page);
                        return new BaseResponse<>(feedRes);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        throw new BaseException(BaseResponseStatus.GET_REVIEW_FAIL);
                    }
                }
            case "title_content":
                if (category == null) {
                    try {
                        feedRes = searchService.searchAllDairyFeedByKeywordByTitleContentInLatest(search_keyword, page);
                        return new BaseResponse<>(feedRes);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        throw new BaseException(BaseResponseStatus.GET_REVIEW_FAIL);
                    }
                } else {
                    try {
                        String categoryIdx = Constant.DAIRY_CATEGORIES.get(category);
                        feedRes = searchService.searchCategoryDairyFeedByKeywordByTitleContentInLatest(categoryIdx, search_keyword, page);
                        return new BaseResponse<>(feedRes);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        throw new BaseException(BaseResponseStatus.GET_REVIEW_FAIL);
                    }
                }
            case "member_nickname":
                if (category == null) {
                    try {
                        feedRes = searchService.searchAllDairyFeedByKeywordByMemberNicknameInLatest(search_keyword, page);
                        return new BaseResponse<>(feedRes);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        throw new BaseException(BaseResponseStatus.GET_REVIEW_FAIL);
                    }
                } else {
                    try {
                        String categoryIdx = Constant.DAIRY_CATEGORIES.get(category);
                        feedRes = searchService.searchCategoryDairyFeedByKeywordByMemberNicknameInLatest(categoryIdx, search_keyword, page);
                        return new BaseResponse<>(feedRes);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        throw new BaseException(BaseResponseStatus.GET_REVIEW_FAIL);
                    }
                }
        }
        return null;
    }


    @GetMapping("story/search")
    public BaseResponse searchStoryFeedByKeyword(@RequestParam(required = false) String category,
                                                 @RequestParam String search_keyword,
                                                 @RequestParam String search_target,
                                                 @RequestParam(defaultValue = "0") int page) throws BaseException {
        if (!isStoryCategoryValid(category)) {
            return new BaseResponse<>(BaseResponseStatus.FEED_BY_CATEGORY_FAILED);
        }
        if (!isSearchKeywordValid(search_keyword)) {
            return new BaseResponse<>(BaseResponseStatus.SEARCH_KEYWORD_NULL_EXCEPTION);
        }
        if (!isSearchTargetValid(search_target)) {
            return new BaseResponse<>(BaseResponseStatus.SEARCH_TARGET_INVALID);
        }

        List<GetAllFeedsRes> feedRes = null;
        switch (search_target) {
            case "title":
                if (category == null) {
                    try {
                        feedRes = searchService.searchAllStoryFeedByKeywordByTitleInLatest(search_keyword, page);
                        return new BaseResponse<>(feedRes);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        throw new BaseException(BaseResponseStatus.GET_REVIEW_FAIL);
                    }
                } else {
                    try {
                        String categoryIdx = Constant.STORY_CATEGORIES.get(category);
                        feedRes = searchService.searchCategoryStoryFeedByKeywordByTitleInLatest(categoryIdx, search_keyword, page);
                        return new BaseResponse<>(feedRes);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        throw new BaseException(BaseResponseStatus.GET_REVIEW_FAIL);
                    }
                }
            case "title_content":
                if (category == null) {
                    try {
                        feedRes = searchService.searchAllStoryFeedByKeywordByTitleContentInLatest(search_keyword, page);
                        return new BaseResponse<>(feedRes);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        throw new BaseException(BaseResponseStatus.GET_REVIEW_FAIL);
                    }
                } else {
                    try {
                        String categoryIdx = Constant.STORY_CATEGORIES.get(category);
                        feedRes = searchService.searchCategoryStoryFeedByKeywordByTitleContentInLatest(categoryIdx, search_keyword, page);
                        return new BaseResponse<>(feedRes);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        throw new BaseException(BaseResponseStatus.GET_REVIEW_FAIL);
                    }
                }
            case "member_nickname":
                if (category == null) {
                    try {
                        feedRes = searchService.searchAllStoryFeedByKeywordByMemberNicknameInLatest(search_keyword, page);
                        return new BaseResponse<>(feedRes);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        throw new BaseException(BaseResponseStatus.GET_REVIEW_FAIL);
                    }
                } else {
                    try {
                        String categoryIdx = Constant.STORY_CATEGORIES.get(category);
                        feedRes = searchService.searchCategoryStoryFeedByKeywordByMemberNicknameInLatest(categoryIdx, search_keyword, page);
                        return new BaseResponse<>(feedRes);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        throw new BaseException(BaseResponseStatus.GET_REVIEW_FAIL);
                    }
                }
        }
        return null;
    }

    @GetMapping("home/search")
    public BaseResponse searchStoryFeedByKeyword(@RequestParam String search_keyword,
                                                 @RequestParam String search_target,
                                                 @RequestParam(defaultValue = "0") int page) throws BaseException {
        if (!isSearchKeywordValid(search_keyword)) {
            return new BaseResponse<>(BaseResponseStatus.SEARCH_KEYWORD_NULL_EXCEPTION);
        }
        if (!isSearchTargetValid(search_target)) {
            return new BaseResponse<>(BaseResponseStatus.SEARCH_TARGET_INVALID);
        }

        List<GetAllFeedsRes> feedRes = null;
        switch (search_target) {
            case "title":
                try {
                    feedRes = searchService.searchAllHomeFeedByKeywordByTitleInLatest(search_keyword, page);
                    return new BaseResponse<>(feedRes);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    throw new BaseException(BaseResponseStatus.GET_REVIEW_FAIL);
                }
            case "title_content":
                try {
                    feedRes = searchService.searchAllHomeFeedByKeywordByTitleContentInLatest(search_keyword, page);
                    return new BaseResponse<>(feedRes);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    throw new BaseException(BaseResponseStatus.GET_REVIEW_FAIL);
                }

            case "member_nickname":
                try {
                    feedRes = searchService.searchAllHomeFeedByKeywordByMemberNicknameInLatest(search_keyword, page);
                    return new BaseResponse<>(feedRes);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    throw new BaseException(BaseResponseStatus.GET_REVIEW_FAIL);
                }
        }
        return null;
    }

    @GetMapping("search/bestkeyword")
    public BaseResponse bestSearchKeyword() {
        List<Keyword> keyword = searchService.bestSearchKeyword();
        return new BaseResponse<>(keyword);
    }

    private boolean isMarketCategoryValid(String category) {
        return Constant.MARKET_GOOD_CATEGORIES.containsKey(category);
    }

    private boolean isDairyCategoryValid(String category) {
        return Constant.DAIRY_CATEGORIES.containsKey(category);
    }

    private boolean isStoryCategoryValid(String category) {
        return Constant.STORY_CATEGORIES.containsKey(category);
    }

    private boolean isSearchKeywordValid(String search_keyword) {
        return search_keyword != null && search_keyword.length() > 1;
    }

    private boolean isSearchTargetValid(String search_target) {
        return Constant.SEARCH_TARGET.stream().anyMatch(o -> o.equals(search_target));
    }
}

