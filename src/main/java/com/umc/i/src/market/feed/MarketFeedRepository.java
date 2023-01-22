package com.umc.i.src.market.feed;

import com.umc.i.src.market.feed.model.GetMarketFeedRes;
import com.umc.i.src.market.feed.model.MarketFeed;
import com.umc.i.src.market.feed.model.PostMarketFeedReq;

import java.util.List;

public interface MarketFeedRepository {

    public int postNewFeed(PostMarketFeedReq marketFeed);

    public List<GetMarketFeedRes> getFeedByCategory(String category, int userIdx, String soldout, int page);

    public List<MarketFeed> getFeedByMarketIdx(String marketIdx);

    public void updateFeedHitCount(String marketIdx);

    public void updateFeed(String marketIdx, PostMarketFeedReq req);

    public void deleteFeed(String marketIdx);

    public void feedLike(int userIdx, int marketIdx);

    public List<GetMarketFeedRes> getFeedByUserIdx(int userIdx);
}
