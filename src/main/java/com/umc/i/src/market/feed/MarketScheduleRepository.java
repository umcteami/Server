package com.umc.i.src.market.feed;

import org.springframework.stereotype.Repository;

public interface MarketScheduleRepository {

    void resetHitCountTable();

    void getHitRankView();
}
