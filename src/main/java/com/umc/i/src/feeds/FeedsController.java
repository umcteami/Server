package com.umc.i.src.feeds;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponse;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.feeds.model.patch.PatchDeleteReq;
import com.umc.i.src.feeds.model.patch.PatchFeedsReq;
import com.umc.i.src.feeds.model.patch.PatchFeedsRes;
import com.umc.i.src.feeds.model.post.PostCommentReq;
import com.umc.i.src.feeds.model.post.PostFeedsLikeReq;
import com.umc.i.src.feeds.model.post.PostFeedsReq;
import com.umc.i.src.feeds.model.post.PostFeedsRes;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
public class FeedsController {
    @Autowired
    private final FeedsService feedsService;
    @Autowired
    private final FeedsProvider feedsProvider;

    @ResponseBody
    @PostMapping("/write")     // 이야기방, 일기장 게시글 작성
    public BaseResponse<PostFeedsRes> createFeeds(@RequestBody PostFeedsReq postFeedsReq) throws BaseException {
        switch(postFeedsReq.getBoardIdx()) {
            case 1: //이야기방
                if(postFeedsReq.getRoomType() > 3) 
                    return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
                return new BaseResponse<>(feedsService.writeFeeds(postFeedsReq, null));
            case 2: //일기장
                if(postFeedsReq.getRoomType() > 2) 
                    return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
                return new BaseResponse<>(feedsService.writeFeeds(postFeedsReq, null));
        }

        return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
    }

    @ResponseBody
    @PostMapping("/write/img")     // 이야기방, 일기장 게시글 작성
    public BaseResponse<PostFeedsRes> createFeedsWithImg(@RequestPart("request") PostFeedsReq postFeedsReq, 
                        @RequestPart("img") List<MultipartFile> file) throws BaseException {
        switch(postFeedsReq.getBoardIdx()) {
            case 1: //이야기방
                if(postFeedsReq.getRoomType() > 3) 
                    return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
                return new BaseResponse<>(feedsService.writeFeeds(postFeedsReq, file));
            case 2: //일기장
                if(postFeedsReq.getRoomType() > 2) 
                    return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
                return new BaseResponse<>(feedsService.writeFeeds(postFeedsReq, file));

        }

        return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
    }


    @ResponseBody
    @PatchMapping("/edit")  // 이야기방, 일기장 게시글 수정
    public BaseResponse<PatchFeedsRes> editFeeds(@RequestBody PatchFeedsReq patchFeedsReq) throws BaseException {
        switch(patchFeedsReq.getBoardIdx()) {
            case 1:     // 이야기방
                if(patchFeedsReq.getRoomType() > 3) break;
                return new BaseResponse<>(feedsService.editFeeds(patchFeedsReq, null));
            case 2:     // 일기장
                if(patchFeedsReq.getRoomType() > 2) break;
                return new BaseResponse<>(feedsService.editFeeds(patchFeedsReq, null));
        }

        return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
    }

    @ResponseBody
    @PatchMapping("/edit/img")  // 이야기방, 일기장 게시글 수정(이미지 포함)
    public BaseResponse<PatchFeedsRes> editFeedsWithImg(@RequestPart("request") PatchFeedsReq patchFeedsReq,
                    @RequestPart("img") List<MultipartFile> img) throws BaseException {
        try {
            switch(patchFeedsReq.getBoardIdx()) {
                case 1:     // 이야기방
                    if(patchFeedsReq.getRoomType() > 3) break;
                    return new BaseResponse<>(feedsService.editFeeds(patchFeedsReq, img));
                case 2:     // 일기장
                    if(patchFeedsReq.getRoomType() > 2) break;
                    return new BaseResponse<>(feedsService.editFeeds(patchFeedsReq, img));
            }
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        } 

        return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
    }

    @ResponseBody
    @PatchMapping("/delete") // 이야기방, 일기장 게시글 삭제
    public BaseResponse deleteFeeds(@RequestBody PatchDeleteReq patchDeleteReq) throws BaseException {
        try {
            feedsService.deleteFeeds(patchDeleteReq.getBoardIdx(), patchDeleteReq.getFeedsIdx());
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
        return new BaseResponse<>("success!");
    }

    @ResponseBody
    @GetMapping("/story/all")   // 이야기방 전체 조회
    public BaseResponse getStories() throws BaseException {
        try {
            return new BaseResponse<>(feedsProvider.getAllStories());
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(BaseResponseStatus.GET_REVIEW_FAIL);
        }
    }

    @ResponseBody
    @GetMapping("/story")   // 이야기방 카테고리별 조회
    public BaseResponse getStoryByRoomType(@RequestParam("roomType") int roomType) throws BaseException {
        try {
            return new BaseResponse<>(feedsProvider.getStoryByRoomType(roomType));
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(BaseResponseStatus.GET_REVIEW_FAIL);
        }
    }

    @ResponseBody
    @GetMapping("/story/{storyIdx}")    // 이야기방 상세 조회
    public BaseResponse getStory(@PathVariable("storyIdx") int storyIdx) throws BaseException {
        try {
            return new BaseResponse<>(feedsProvider.getStory(storyIdx));
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(BaseResponseStatus.GET_REVIEW_FAIL);
        }
    }

    @ResponseBody
    @GetMapping("/diary/all")   // 일기장 전체 조회
    public BaseResponse getDiaries() throws BaseException {
        try {
            return new BaseResponse<>(feedsProvider.getAllDiaries());
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(BaseResponseStatus.GET_REVIEW_FAIL);
        }
    }

    @ResponseBody
    @GetMapping("/diary")   // 일기장 카테고리별 조회
    public BaseResponse getDiariesByRoomType(@RequestParam("roomType") int roomType) throws BaseException {
        try {
            return new BaseResponse<>(feedsProvider.getDiariesByRoomType(roomType));
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(BaseResponseStatus.GET_REVIEW_FAIL);
        }
    }

    @ResponseBody
    @GetMapping("/diary/{diaryIdx}")    // 일기장 상세 조회
    public BaseResponse getDiary(@PathVariable("diaryIdx") int diaryIdx) throws BaseException {
        try {
            return new BaseResponse<>(feedsProvider.getDiary(diaryIdx));
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(BaseResponseStatus.GET_REVIEW_FAIL);
        }
    }

    @ResponseBody
    @PostMapping("like/change")     // 좋아요 변경
    public BaseResponse changeLike(@RequestBody PostFeedsLikeReq postFeedsLikeReq) {
        try {
            boolean isSuccess = feedsService.changeLike(postFeedsLikeReq);
            return new BaseResponse<>("");
        } catch (BaseException e) {
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("comment/write") // 댓글 작성
    public BaseResponse writeComment(@RequestBody PostCommentReq postCommentReq) {
        try {
            feedsService.writeComment(postCommentReq);
            return new BaseResponse<>("댓글을 작성했습니다");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
     
}
