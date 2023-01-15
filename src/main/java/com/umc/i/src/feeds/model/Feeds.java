package com.umc.i.src.feeds.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Feeds {
    private int userIdx;
    private int roomType;
    private String title;
    private String content;
}
