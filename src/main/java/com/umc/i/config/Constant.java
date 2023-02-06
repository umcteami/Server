package com.umc.i.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.HashMap;


// 프로젝트에서 공통적으로 사용하는 상수들
public class Constant {

    public static long NUMBER_AUTH_TIME_LIMIT = 60 * 10; // 10분

    public static HashMap<String, String> MARKET_GOOD_CATEGORIES = new HashMap<>(){{
        put("food", "1");
        put("toy", "2");
        put("medicine", "3");
        put("nursetool", "4");
        put("etc", "5");
        put(null, null);
    }};

    public static List<String> SEARCH_TARGET = Arrays.asList("title", "title_content", "member_nickname");
    
    public static String[] BOOLEANS = {"true", "false", "0", "1"};
    
    public static int FEED_PER_PAGE = 9;
}

