package com.umc.i.src.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchScheduleTask {

    private final SearchScheduleRepository searchScheduleRepository;

    @Scheduled(cron = "0 0 * * * ?")
    public void resetHitCountTable() {
        searchScheduleRepository.resetKeywordTable();
    }

    @Scheduled(cron = "0 1 * * * ?")
    public void getHitRankView() {
        searchScheduleRepository.getSearchKeywordTable();
    }


}
