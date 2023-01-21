package com.umc.i.src.mypage;

import com.umc.i.config.BaseException;
import com.umc.i.src.member.MemberDao;
import com.umc.i.src.member.model.get.GetMemRes;
import com.umc.i.src.mypage.model.get.GetComuWriteResDto;
import com.umc.i.src.mypage.model.get.GetMypageMemRes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.print.Pageable;

import static com.umc.i.config.BaseResponseStatus.INTERNET_ERROR;

@Service
@RequiredArgsConstructor
public class MypageService {
    @Autowired
    private MypageDao mypageDao;
}
