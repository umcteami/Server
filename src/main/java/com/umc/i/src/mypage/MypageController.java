package com.umc.i.src.mypage;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponse;
import com.umc.i.src.mypage.model.get.GetComuWriteRes;
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
    //무한 스크롤.....
    @ResponseBody
    @GetMapping("/test/{memIdx}/{start}")
    public BaseResponse<Stream<GetComuWriteRes>> getComuWrite(@PathVariable("memIdx")int memIdx,
                                                              @PathVariable("start")int start) {
        try {
            int skip = start;
            int limit = skip+3;

            List<GetComuWriteRes> list = mypageProvider.getRSDWrite(memIdx);
            if(limit > list.size()){limit = list.size();}
            return new BaseResponse<>(list.stream().skip(skip).limit(limit));
        } catch (BaseException exception) {
            throw new RuntimeException();
        }
    }
}
