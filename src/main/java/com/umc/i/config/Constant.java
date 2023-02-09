package com.umc.i.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


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

    public static HashMap<String, String> DAIRY_CATEGORIES = new HashMap<>(){{
        put("nurse", "1");
        put("rainbow", "2");
        put(null, null);
    }};

    public static final HashMap<String, String> STORY_CATEGORIES = new HashMap<>() {{
        put("justchat", "1");
        put("question", "2");
        put("info", "3");
        put(null, null);
    }};

    public static List<String> SEARCH_TARGET = Arrays.asList("title", "title_content", "member_nickname");
    
    public static String[] BOOLEANS = {"true", "false", "0", "1"};
    
    public static int FEED_PER_PAGE = 9;
}

