package com.umc.i.src.market.feed;

import com.umc.i.src.market.feed.model.GetMarketFeedRes;
import com.umc.i.src.market.feed.model.GetMarketFeedReq;

import java.util.List;
import java.util.Optional;

public interface MarketFeedRepository {

    int postNewFeed(GetMarketFeedReq marketFeed);

    int postFeedImages(List<String> filesUrlList, int marketIdx);

    List<GetMarketFeedRes> getAllFeed(int userIdx, String soldout, int page);

    List<GetMarketFeedRes> getFeedByCategory(String category, int userIdx, String soldout, int page);

    Optional<GetMarketFeedRes> getFeedByMarketIdx(String marketIdx, String memIdx);

    void updateFeedHitCount(int category, String marketIdx);

    void updateFeed(String marketIdx, GetMarketFeedReq req);

    void updateFeedSoldout(String marketIdx, GetMarketFeedReq req);

    void deleteFeed(String marketIdx);

    void deleteImages(int marketIdx);

    void feedLike(int userIdx, int marketIdx, String isLike, int feedUserIdx);

    List<GetMarketFeedRes> getFeedByUserIdx(int userIdx);

    int getFeedUserIdx(String marketIdx);

    void postCoverImage(List<String> filesUrlList, String marketIdx);

    List<GetMarketFeedRes> getAllHotFeed(int userIdx, String soldout, int page);

    List<GetMarketFeedRes> getHotFeedByCategory(String categoryIdx, int userIdx, String soldout, int page);
}