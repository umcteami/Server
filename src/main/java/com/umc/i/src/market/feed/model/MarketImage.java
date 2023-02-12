package com.umc.i.src.market.feed.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageUrl {
    private int imageIdx;
    private int contentCategory;
    private int contentIdx;
    private String imageUrl;
    
}
