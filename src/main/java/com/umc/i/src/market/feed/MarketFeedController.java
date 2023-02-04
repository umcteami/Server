package com.umc.i.src.market.feed;

import com.umc.i.config.BaseResponse;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.config.Constant;
import com.umc.i.src.market.feed.model.GetMarketFeedRes;
import com.umc.i.src.market.feed.model.MarketFeed;
import com.umc.i.src.market.feed.model.GetMarketFeedReq;
import com.umc.i.src.market.feed.model.PostMarketFeedRes;
import com.umc.i.utils.S3Storage.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MarketFeedController {

    private final MarketFeedService marketFeedService;

    private final S3Uploader s3Uploader;

//    private final JwtServiceImpl jwtService;

    @PostMapping("/market")
    public BaseResponse<PostMarketFeedRes> postNewsFeed(@RequestHeader Map<String, String> headers,
                                                        @RequestPart(name = "request") GetMarketFeedReq marketFeedReq,
                                                        @RequestPart(required = false, name = "images") List<MultipartFile> files) {
        /**
         * TODO
         * jwt access token으로 사용자 판별
         * access token 이 만료일 경우 refresh token 확인
         * refresh 가 만료일 경우 로그아웃
         * refresh 가 사용 가능하면 access token 재발급 한 뒤 게시물 post 진행, 반환시 새로운 access token 첨부
         */
//
//        String accessToken = headers.get("auth-access");
//        log.info("accessToken={}", accessToken);
//        Long memberId = Long.valueOf(jwtService.getMemberId(accessToken));
//        log.info("memberId={}", memberId);
//        marketFeedReq.setUserId(memberId);

        int marketIdx = marketFeedService.postNewFeed(marketFeedReq);
        log.info("markedIdx={}", marketIdx);

        if (marketIdx == -1) {
            return new BaseResponse<>(BaseResponseStatus.POST_MARKET_FEED_FAILED);
        }

        List<String> filesUrlList = s3Uploader.upload(files);

        marketFeedService.postFeedImages(filesUrlList, marketIdx);
        marketFeedService.postCoverImage(filesUrlList, String.valueOf(marketIdx));

        return new BaseResponse<>(new PostMarketFeedRes(marketFeedReq.getUserIdx(), marketIdx));
    }

    @PutMapping("/market/edit")
    public BaseResponse updateFeedDetail(@RequestParam String marketIdx,
                                         @RequestPart("request") GetMarketFeedReq marketFeedReq,
                                         @RequestPart("images") List<MultipartFile> multipartFiles) {

        int feedUserIdx = marketFeedService.getFeedUserIdx(marketIdx);
        log.info("feedUserIdx={}", feedUserIdx);
        log.info("userIdx={}", marketFeedReq.getUserIdx());

        if (marketFeedReq.getUserIdx() != feedUserIdx) {
            return new BaseResponse<>(BaseResponseStatus.FEED_UNAUTHORIZED);
        }

        marketFeedService.updateFeed(marketIdx, marketFeedReq);
        marketFeedService.deleteImages(Integer.parseInt(marketIdx));

        List<String> filesUrlList = s3Uploader.upload(multipartFiles);

        int uploadedImagesCnt = marketFeedService.postFeedImages(filesUrlList, Integer.parseInt(marketIdx));
        marketFeedService.postCoverImage(filesUrlList, marketIdx);

        return new BaseResponse<>(new PostMarketFeedRes(marketFeedReq.getUserIdx(), Integer.parseInt(marketIdx)));
    }

    @DeleteMapping("/market/delete")
    public BaseResponse deleteFeed(@RequestParam String marketIdx,
                                   @RequestBody GetMarketFeedReq marketFeedReq) {

        int feedUserIdx = marketFeedService.getFeedUserIdx(marketIdx);
        if (marketFeedReq.getUserIdx() != feedUserIdx) {
            return new BaseResponse<>(BaseResponseStatus.FEED_UNAUTHORIZED);
        }

        marketFeedService.deleteFeed(marketIdx);
        marketFeedService.deleteImages(Integer.parseInt(marketIdx));

        return new BaseResponse<>();
    }

    @PutMapping("/market/soldout")
    public BaseResponse updateFeedSoldoutStatus(@RequestParam String marketIdx,
                                                @RequestBody GetMarketFeedReq marketFeedReq) {

        int feedUserIdx = marketFeedService.getFeedUserIdx(marketIdx);
        if (marketFeedReq.getUserIdx() != feedUserIdx) {
            return new BaseResponse<>(BaseResponseStatus.FEED_UNAUTHORIZED);
        }

        marketFeedService.updateFeedSoldout(marketIdx, marketFeedReq);

        return new BaseResponse<>();
    }

    @GetMapping("/market/latest")
    public BaseResponse getNewsFeed(@RequestParam(required = false) String category,
                                    @RequestParam(defaultValue = "0") String soldout,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestBody GetMarketFeedReq req) throws RuntimeException {
        // query string 값 오류
        HashMap<String, String> marketGoodCategories = Constant.MARKET_GOOD_CATEGORIES;
        String[] booleans = Constant.BOOLEANS;

        if (!marketGoodCategories.containsKey(category) || !Arrays.asList(booleans).contains(soldout)) {
            return new BaseResponse<>(BaseResponseStatus.GET_MARKET_FEED_BY_PARAM_FAILED);
        }

        int userIdx = req.getUserIdx();
        String categoryIdx = marketGoodCategories.get(category);

        List<GetMarketFeedRes> feedResList;
        if (categoryIdx == null) { // category 무관
            feedResList= marketFeedService.getAllFeed(userIdx, soldout, page);
        } else { // category 선택
            feedResList = marketFeedService.getFeedByCategory(categoryIdx, userIdx, soldout, page);
        }

        return new BaseResponse<>(feedResList);
    }

    @GetMapping("market/hot")
    public BaseResponse getHotNewsFeed(@RequestParam(required = false) String category,
                                       @RequestParam(defaultValue = "0") String soldout,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestBody GetMarketFeedReq req) throws RuntimeException {

        // query string 값 오류
        HashMap<String, String> marketGoodCategories = Constant.MARKET_GOOD_CATEGORIES;
        String[] booleans = Constant.BOOLEANS;

        if (!marketGoodCategories.containsKey(category) || !Arrays.asList(booleans).contains(soldout)) {
            return new BaseResponse<>(BaseResponseStatus.GET_MARKET_FEED_BY_PARAM_FAILED);
        }

        int userIdx = req.getUserIdx();
        String categoryIdx = marketGoodCategories.get(category);

        List<GetMarketFeedRes> feedResList;
        if (categoryIdx == null) {
            feedResList = marketFeedService.getAllHotFeed(userIdx, soldout, page);
        } else {
            feedResList = marketFeedService.getHotFeedByCategory(categoryIdx, userIdx, soldout, page);
        }

        return new BaseResponse<>(feedResList);
    }

    @GetMapping("/market/detail")
    public BaseResponse getFeedDetail(@RequestParam String marketIdx,
                                      @RequestBody MarketFeed feed) {

        GetMarketFeedRes result = marketFeedService.getFeedByMarketIdx(marketIdx, String.valueOf(feed.getUserIdx()));

        if (result == null) {
            return new BaseResponse<>(BaseResponseStatus.FEED_NOT_EXIST);
        }

        marketFeedService.updateMarketFeedHitCount(result.getCategory(), marketIdx);

        return new BaseResponse<>(result);
    }

    @GetMapping("/market/list")
    public BaseResponse getFeedByUserIdx(@RequestParam int userIdx) {

        List<GetMarketFeedRes> result = marketFeedService.getFeedByUserIdx(userIdx);

        return new BaseResponse<>(result);
    }

    @PostMapping("/market/like")
    public BaseResponse feedLikeByMember(@RequestBody PostMarketFeedRes feed) {

        int feedUserIdx = marketFeedService.getFeedUserIdx(String.valueOf(feed.getMarketIdx()));

        marketFeedService.feedLike(feed.getUserIdx(), feed.getMarketIdx(), feed.getIsLike(), feedUserIdx);

        return new BaseResponse<>();
    }
}
