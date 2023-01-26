package com.umc.i.src.market.feed.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostMarketFeedReq {
    private int userId;
    private String title;
    private String category;
    private int price;
    private String content;
    private String[] image;
    private String soldout;
    private Date createdAt;
}