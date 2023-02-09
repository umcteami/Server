package com.umc.i.src.market.feed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarketScheduleTask {

    private final MarketScheduleRepository marketScheduleRepository;

    @Scheduled(cron = "0 * 0/12 * * ?")
    public void resetHitCountTable() {
        marketScheduleRepository.resetHitCountTable();
    }

    @Scheduled(cron = "0 5 3/12 * * ?")
    public void getHitRankView() {
        marketScheduleRepository.getHitRankView();
    }


}
