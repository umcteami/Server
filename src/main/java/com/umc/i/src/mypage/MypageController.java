package com.umc.i.src.mypage;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponse;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.mypage.model.get.GetComuWriteRes;
import com.umc.i.src.mypage.model.get.GetMarketWriteRes;
import com.umc.i.src.mypage.model.get.GetMypageMemRes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
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
    @GetMapping("/{type}/{memIdx}/{end}")
    public BaseResponse<Stream<GetComuWriteRes>> getWrite(@PathVariable("memIdx")int memIdx,
                                                            @PathVariable("end")int end,
                                                            @PathVariable("type")int type) {
        try {
            end *= 3;
            List<GetComuWriteRes> getComuWriteList = null;
            switch (type) {
                case 1: //RSDWrite 리뷰 스토리 일기장 대상 조회
                    getComuWriteList = mypageProvider.getWrite(memIdx);
                case 2://리뷰 스토리 대상 조회
                    getComuWriteList = mypageProvider.getRSWrite(memIdx);
                case 3: //일기장 대상 조회
                    getComuWriteList = mypageProvider.getSWrite(memIdx);
            }
            if (end > getComuWriteList.size()) {
                end = getComuWriteList.size();
            }
            return new BaseResponse<>(getComuWriteList.stream().limit(end));
        }catch (Exception e){
            throw new RuntimeException();
        }
    }
    @ResponseBody
    @GetMapping("/market/{memIdx}/{end}")
    public BaseResponse<Stream<GetMarketWriteRes>>  getMarketWrite(@PathVariable("memIdx")int memIdx,
                                                                    @PathVariable("end")int end){
        try {
            end *=3;
            List<GetMarketWriteRes> getMarketWriteList = mypageProvider.getMarketWrite(memIdx);
            if (end > getMarketWriteList.size()) {
                end = getMarketWriteList.size();
            }
            return new BaseResponse<>(getMarketWriteList.stream().limit(end));

        }catch (Exception e){
            throw new RuntimeException();
        }
    }
}

