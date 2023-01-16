package com.umc.i.src.feeds;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponse;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.feeds.model.patch.PatchFeedsReq;
import com.umc.i.src.feeds.model.patch.PatchFeedsRes;
import com.umc.i.src.feeds.model.post.PostFeedsReq;
import com.umc.i.src.feeds.model.post.PostFeedsRes;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
public class FeedsController {
    @Autowired
    private final FeedsService feedsService;

    @ResponseBody
    @PostMapping("/write/{boardType}")     // 이야기방, 일기장 게시글 작성
    public BaseResponse<PostFeedsRes> createFeeds(@PathVariable("boardType") int boardType, 
                        @RequestBody PostFeedsReq postFeedsReq) throws BaseException {
        switch(boardType) {
            case 1: //이야기방
                if(postFeedsReq.getRoomType() > 3) 
                    return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
                return new BaseResponse<>(feedsService.writeFeeds(boardType, postFeedsReq, null));
            case 2: //일기장
                if(postFeedsReq.getRoomType() > 2) 
                    return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
                return new BaseResponse<>(feedsService.writeFeeds(boardType, postFeedsReq, null));

        }

        return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
    }

    @ResponseBody
    @PostMapping("/write/img/{boardType}")     // 이야기방, 일기장 게시글 작성
    public BaseResponse<PostFeedsRes> createFeedsWithImg(@PathVariable("boardType") int boardType, 
                        @RequestPart("request") PostFeedsReq postFeedsReq, 
                        @RequestPart("img") List<MultipartFile> file) throws BaseException {
        switch(boardType) {
            case 1: //이야기방
                if(postFeedsReq.getRoomType() > 3) 
                    return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
                return new BaseResponse<>(feedsService.writeFeeds(boardType, postFeedsReq, file));
            case 2: //일기장
                if(postFeedsReq.getRoomType() > 2) 
                    return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
                return new BaseResponse<>(feedsService.writeFeeds(boardType, postFeedsReq, file));

        }

        return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
    }


    @ResponseBody
    @PatchMapping("/edit/{boardType}")  // 이야기방, 일기장 게시글 수정
    public BaseResponse<PatchFeedsRes> editFeeds(@PathVariable("boardType") int boardType,
                    @RequestBody PatchFeedsReq patchFeedsReq) throws BaseException {
        switch(boardType) {
            case 1:     // 이야기방
                if(patchFeedsReq.getRoomType() > 3) break;
                return new BaseResponse<>(feedsService.editFeeds(boardType, patchFeedsReq, null));
            case 2:     // 일기장
                if(patchFeedsReq.getRoomType() > 2) break;
                return new BaseResponse<>(feedsService.editFeeds(boardType, patchFeedsReq, null));
        }

        return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
    }

    @ResponseBody
    @PatchMapping("/edit/img/{boardType}")  // 이야기방, 일기장 게시글 수정(이미지 포함)
    public BaseResponse<PatchFeedsRes> editFeedsWithImg(@PathVariable("boardType") int boardType,
                    @RequestPart("request") PatchFeedsReq patchFeedsReq,
                    @RequestPart("img") List<MultipartFile> img) throws BaseException {
        try {
            switch(boardType) {
                case 1:     // 이야기방
                    if(patchFeedsReq.getRoomType() > 3) break;
                    return new BaseResponse<>(feedsService.editFeeds(boardType, patchFeedsReq, img));
                case 2:     // 일기장
                    if(patchFeedsReq.getRoomType() > 2) break;
                    return new BaseResponse<>(feedsService.editFeeds(boardType, patchFeedsReq, img));
            }
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        } 

        return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
    }

    
}
