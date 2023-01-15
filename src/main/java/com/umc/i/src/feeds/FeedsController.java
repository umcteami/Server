package com.umc.i.src.feeds;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.amazonaws.services.cloudformation.model.StackDriftStatus;
import com.amazonaws.services.ec2.model.Image;
import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponse;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.feeds.model.post.PostFeedsReq;
import com.umc.i.src.feeds.model.post.PostFeedsRes;

import lombok.RequiredArgsConstructor;

@CrossOrigin
@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
public class FeedsController {
    @Autowired
    private final FeedsService feedsService;

    @ResponseBody
    @PostMapping("/{boardType}}")
    //이야기방, 일기장 게시글 작성
    // public BaseResponse<PostFeedsRes> createFeeds(
    //     @Valid @RequestParam("userIdx") int userIdx,
    //     @Valid @RequestParam("roomType") int roomType,
    //     @Valid @RequestParam("title") String title,
    //     @Valid @RequestParam("content") String content,
    //     MultipartHttpServletRequest multiRequest
    // ) throws BaseException {
    //     PostFeedsReq PostFeedsReq = new PostFeedsReq(userIdx, roomType, title, content)
    //     PostFeedsRes postFeedsRes;
    //     return new BaseResponse<>(postFeedsRes);
    // }

    public BaseResponse<PostFeedsRes> createFeeds(@PathVariable("boardIdx") int boardIdx, @RequestBody PostFeedsReq<MultipartFile> postFeedsReq) throws BaseException {
        switch(boardIdx) {
            case 1: //이야기방
            case 2: //일기장

        }

        return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
    }


    
}
