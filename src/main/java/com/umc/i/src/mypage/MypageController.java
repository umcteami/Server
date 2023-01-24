package com.umc.i.src.mypage;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponse;
import com.umc.i.config.BaseResponseStatus;
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

    //마이 홈페이지 시작창 조회
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
    //type별 대상 작성 조회
    @ResponseBody
    @GetMapping("/{contents}/{memIdx}/{end}")
    public BaseResponse<Stream<GetComuWriteRes>> getWrite(@PathVariable("memIdx")int memIdx,
                                                                      @PathVariable("end")int end,
                                                                      @PathVariable("contents")String contents) {
        try {
            end *= 3;
            /**switch (contents) {
                case "RSDWrite" : //RSDWrite 리뷰 스토리 일기장 대상 조회
                    List<GetComuWriteRes> getComuWriteList = mypageProvider.getWrite(memIdx);
                case "RSWrite"://리뷰 스토리 대상 조회
                    getComuWriteList = mypageProvider.getRSWrite(memIdx);//결과값 이상해
                case "SWrite": //일기장 대상 조회
                    getComuWriteList = mypageProvider.getSWrite(memIdx);
            }**/
            List<GetComuWriteRes> getComuWriteList = null;

            if(contents.equals("RSDWrite")){
                getComuWriteList = mypageProvider.getWrite(memIdx);
                log.info("{}","RSDWrite");
            } else if (contents.equals("RSWrite")) {
                getComuWriteList = mypageProvider.getRSWrite(memIdx);
                log.info("{}",contents);
            } else if (contents.equals("SWrite")) {
                getComuWriteList = mypageProvider.getSWrite(memIdx);
                log.info("{}",contents);
            }

            if (end > getComuWriteList.size()) {
                end = getComuWriteList.size();
            }
            return new BaseResponse<>(getComuWriteList.size(),getComuWriteList.stream().limit(end));
        }catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    //나눔장터 대상
    @ResponseBody
    @GetMapping("/market/{memIdx}/{end}")
    public BaseResponse<Stream<GetMarketWriteRes>> getMarketWrite(@PathVariable("memIdx")int memIdx,
                                                                    @PathVariable("end")int end){
        try {
            end *=3;
            List<GetMarketWriteRes> getMarketWriteList = mypageProvider.getMarketWrite(memIdx);
            if (end > getMarketWriteList.size()) {
                end = getMarketWriteList.size();
            }
            return new BaseResponse<>(getMarketWriteList.size(),getMarketWriteList.stream().limit(end));

        }catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    //작성한 댓글 대상
    @ResponseBody
    @GetMapping("/comment/{memIdx}/{end}")
    public BaseResponse<Stream<GetComentWriteRes>> getComentWrite(@PathVariable("memIdx")int memIdx,
                                                                    @PathVariable("end")int end){
        try {
            end*=3;
            List<GetComentWriteRes> getComentWriteResList = mypageProvider.getCmtWrite(memIdx);
            if (end > getComentWriteResList.size()) {
                end = getComentWriteResList.size();
            }
            return new BaseResponse<>(getComentWriteResList.size(),getComentWriteResList.stream().limit(end));

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    @ResponseBody
    @GetMapping("/like/{memIdx}/{end}")
    public BaseResponse<Stream<GetComuWriteRes>> getLikeFeed(@PathVariable("memIdx")int memIdx,
                                                             @PathVariable("end")int end){
        try {
            end*=3;
            List<GetComuWriteRes> getLikeList = mypageProvider.getLikeFeed(memIdx);
            if (end > getLikeList.size()) {
                end = getLikeList.size();
            }
            return new BaseResponse<>(getLikeList.size(),getLikeList.stream().limit(end));

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    @ResponseBody
    @GetMapping("/want/{memIdx}/{end}")
    public BaseResponse<Stream<GetWantMarketRes>> getWantFeed(@PathVariable("memIdx")int memIdx,
                                                      @PathVariable("end")int end){
        try {
            end*=3;
            List<GetWantMarketRes> getLikeList = mypageProvider.getWantMarket(memIdx);
            if (end > getLikeList.size()) {
                end = getLikeList.size();
            }
            return new BaseResponse<>(getLikeList.size(),getLikeList.stream().limit(end));

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    @ResponseBody
    @GetMapping("/block/{memIdx}")
    public BaseResponse<List<GetBlockMemRes>> getBlockMem(@PathVariable("memIdx") int memIdx) throws BaseException {
        return new BaseResponse<>(mypageProvider.getBlockMem(memIdx));
    }
    @ResponseBody
    @PostMapping("/ask")
    public BaseResponse<BaseException> postAsk(@RequestBody PostAskReq postAskReq){
        mypageService.postAsk(postAskReq);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }
}

