package com.umc.i.src.mypage;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponse;
import com.umc.i.src.member.MemberService;
import com.umc.i.src.member.model.get.GetMemRes;
import com.umc.i.src.mypage.model.get.GetComuWriteRes;
import com.umc.i.src.mypage.model.get.GetMypageMemRes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {
    @Autowired
    private final MypageService mypageService;
    private final MypageProvider mypageProvider;

    private final MypageDao mypageDao;
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
    @ResponseBody
    @GetMapping("/test/{memIdx}")
    public BaseResponse<List<GetComuWriteRes>> getComuWrite(@PathVariable("memIdx")int memIdx){
        //List<GetComuWriteRes> test = mypageDao.getDiaryWrite(memIdx);
        return new BaseResponse<>(mypageDao.getDiaryWrite(memIdx));
    }
}
