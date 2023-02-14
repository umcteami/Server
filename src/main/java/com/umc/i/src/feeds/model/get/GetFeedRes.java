package com.umc.i.src.feeds.model.get;

import java.util.List;

import com.umc.i.src.feeds.model.Feeds;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetFeedRes {
    private List<Feeds> feed;       // 게시글
    private List<String> img;       // 이미지
}
