package com.umc.i.src.mypage;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponse;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.mypage.model.MypageResponse;
import com.umc.i.src.mypage.model.get.*;
import com.umc.i.src.mypage.model.post.PostAskReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Slf4j
public class MypageController {
    @Autowired
    private final MypageService mypageService;
    private final MypageProvider mypageProvider;

    //마이 홈페이지 시작창 조회 - clear
    @ResponseBody
    @GetMapping("/{memIdx}")
    public BaseResponse<GetMypageMemRes> getMyPMem(@PathVariable("memIdx") int memIdx) throws BaseException {
        try {
            GetMypageMemRes getMypageMemRes = mypageProvider.getMyPMem(memIdx);
            return new BaseResponse<>(getMypageMemRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    //type별 대상 작성 조회-clear
    @ResponseBody
    @GetMapping("/{contents}/{memIdx}/{page}")
    public MypageResponse<List<GetComuWriteRes>> getWrite(@PathVariable("memIdx")int memIdx,
                                                          @PathVariable("page")int page,
                                                          @PathVariable("contents")String contents) {
        try {
            page *=10;
            if(contents.equals("RSDWrite")){
                return new MypageResponse<>(mypageProvider.getRWriteCount(memIdx) + mypageProvider.getSWriteCount(memIdx)+mypageProvider.getDWriteCount(memIdx),mypageProvider.getAllWrite(memIdx,page));
            } else if (contents.equals("RSWrite")) {
                return new MypageResponse<>(mypageProvider.getRWriteCount(memIdx) + mypageProvider.getSWriteCount(memIdx),mypageProvider.getRSWrite(memIdx,page));
            } else if (contents.equals("DWrite")) {
                return new MypageResponse<>(mypageProvider.getDWriteCount(memIdx),mypageProvider.getDWrite(memIdx,page));
            }

            return new MypageResponse<>(BaseResponseStatus.GET_WRITE_FEED_TYPERROR);
        }catch (BaseException exception) {
            return new MypageResponse<>((exception.getStatus()));
        }catch(Exception exception){
            exception.printStackTrace();
            return new MypageResponse<>(BaseResponseStatus.INTERNET_ERROR);
        }
    }
    //나눔장터 대상-clear
    @ResponseBody
    @GetMapping("/market/{memIdx}/{page}")
    public MypageResponse<List<GetMarketWriteRes>> getMarketWrite(@PathVariable("memIdx")int memIdx,
                                                                    @PathVariable("page")int page){
        try {
            page *=10;

            return new MypageResponse<>(mypageProvider.getMWriteCount(memIdx),mypageProvider.getMarketWrite(memIdx,page));

        }catch (BaseException exception) {
            return new MypageResponse<>((exception.getStatus()));
        }
    }
    //작성한 댓글 대상-clear
    @ResponseBody
    @GetMapping("/comment/{memIdx}/{page}")
    public MypageResponse<List<GetComentWriteRes>> getComentWrite(@PathVariable("memIdx")int memIdx,
                                                                    @PathVariable("page")int page){
        try {
            page*=10;
            return new MypageResponse<>(mypageProvider.getComentWriteCount(memIdx),mypageProvider.getComentWrite(memIdx,page));

        } catch (BaseException exception) {
            exception.printStackTrace();
            return new MypageResponse<>((exception.getStatus()));
        } catch (Exception exception){
            exception.printStackTrace();
            return new MypageResponse<>(BaseResponseStatus.INTERNET_ERROR);
        }
    }
    //좋아요한 게시글 조회-clear
    @ResponseBody
    @GetMapping("/like/{memIdx}/{page}")
    public MypageResponse<List<GetComuWriteRes>> getLikeFeed(@PathVariable("memIdx")int memIdx,
                                                             @PathVariable("page")int page){
        try {
            page *=10;

            return new MypageResponse<>(mypageProvider.getLikeCount(memIdx),mypageProvider.getLike(memIdx,page));
        } catch (BaseException exception) {
            return new MypageResponse<>((exception.getStatus()));
        }
    }
    //찜한거 조회 -clear
    @ResponseBody
    @GetMapping("/want/{memIdx}/{page}")
    public MypageResponse<List<GetWantMarketRes>> getWantFeed(@PathVariable("memIdx")int memIdx,
                                                      @PathVariable("page")int page){
        try {
            page*=10;
            return new MypageResponse<>(mypageProvider.getWantCount(memIdx),mypageProvider.getWantMarket(memIdx,page));

        } catch (BaseException exception) {
            return new MypageResponse<>((exception.getStatus()));
        }
    }
    //차단한 유저 조회 - clear
    @ResponseBody
    @GetMapping("/block/{memIdx}")
    public BaseResponse<List<GetBlockMemRes>> getBlockMem(@PathVariable("memIdx") int memIdx) throws BaseException {
        return new BaseResponse<>(mypageProvider.getBlockMem(memIdx));
    }
    //문의하기 - clear
    @ResponseBody
    @PostMapping("/ask")
    public BaseResponse<BaseException> postAsk(@RequestBody PostAskReq postAskReq){
        mypageService.postAsk(postAskReq);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }
    //신고한 게시글 조회 - clear
    @ResponseBody
    @GetMapping("/blame/{memIdx}")
    public BaseResponse<List<GetBlameFeedRes>> getBlameFeed(@PathVariable("memIdx")int memIdx)throws BaseException{
        return new BaseResponse<>(mypageProvider.getBlameFeed(memIdx));
    }
}
