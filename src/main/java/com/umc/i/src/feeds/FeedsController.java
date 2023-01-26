package com.umc.i.src.feeds;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponse;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.feeds.model.post.PostBlameReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
@Slf4j
public class FeedsController {
    @Autowired
    private final FeedsService feedsService;
    @ResponseBody
    @PostMapping("/blame")
    public BaseResponse<BaseException> postBlame(@RequestBody PostBlameReq postBlameReq){
        try {
            feedsService.postBlame(postBlameReq);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        }catch (BaseException e){
            return new BaseResponse<>(BaseResponseStatus.POST_FEED_BLAME_DOUBLE);
        } catch (Exception e){
            e.printStackTrace();
            return new BaseResponse<>(BaseResponseStatus.INTERNET_ERROR);
        }
    }
}
