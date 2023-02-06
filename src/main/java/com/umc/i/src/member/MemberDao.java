package com.umc.i.src.member;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;


import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponse;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.member.model.get.GetMemRes;
import com.umc.i.src.member.model.Member;
import com.umc.i.src.member.model.get.GetMemRes;

import com.umc.i.src.member.model.patch.PatchMemReq;
import com.umc.i.src.member.model.post.PostAuthNumberReq;
import com.umc.i.src.member.model.post.PostJoinReq;
import com.umc.i.src.member.model.post.PostMemblockReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import static com.umc.i.config.BaseResponseStatus.POST_MEMBER_WITHDRAW;
import static com.umc.i.config.BaseResponseStatus.POST_NEMBER_BLOCK_DOUBLE;

@Repository
@Slf4j
public class MemberDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //회원가입
    public BaseResponseStatus createMem(PostJoinReq postJoinReq, String profileUrl) {
        String createUserQuery = "insert into Member (mem_email, mem_password,mem_phone, mem_nickname,mem_profile_content,mem_profile_url,mem_birth,mem_address,mem_address_code,mem_address_detail) VALUES (?,?,?,?,?,?,?,?,?,?)";
        Object[] createUserParams = new Object[]{postJoinReq.getEmail(), postJoinReq.getPw(),postJoinReq.getPhone(), postJoinReq.getNick(),postJoinReq.getIntro(),profileUrl,
                postJoinReq.getBirth(),postJoinReq.getAddres(),postJoinReq.getAddresCode(),postJoinReq.getAddresPlus()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);
        //추가된 idx 받기
        String lastInsertIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        int lastIdx = this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userIdx번호를 반환한다.
        //멤버 닉네임 테이블로 이동
        String uploadNickQuery = "insert into Member_nickname (mem_idx,nickname) VALUES (?,?)";
        Object[] uploadNickParams = new Object[]{lastIdx,postJoinReq.getNick()};
        this.jdbcTemplate.update(uploadNickQuery,uploadNickParams);

        //성공 시 0
        return BaseResponseStatus.SUCCESS;
    }

    // 닉네임 확인
    public int checkNick(String nick) {
        String checkNickQuery = "select count(*) from Member_nickname where nickname = ?";
        int num = this.jdbcTemplate.queryForObject(checkNickQuery, int.class, nick);
        if(num != 0){ num = 1;}
        //중복 있으면 1 없으면 0
        return num;
    }

    //유저 정보 변경
    public void editMem(int memIdx,PatchMemReq patchMemReq, String profileUrl) {
        String editMemQuery = "update Member set mem_email = ? ,mem_phone = ?,mem_nickname = ?, mem_profile_content = ?, mem_profile_url = ?, mem_birth = ?,mem_address = ?,mem_address_code=?,mem_address_detail=? where mem_idx = ? ";
        Object[] editMemParams = new Object[]{patchMemReq.getEmail(),patchMemReq.getPhone(), patchMemReq.getNick(),patchMemReq.getIntro(),profileUrl,
                patchMemReq.getBirth(),patchMemReq.getAddres(),patchMemReq.getAddresCode(),patchMemReq.getAddresPlus(),memIdx};
        this.jdbcTemplate.update(editMemQuery,editMemParams);

        //변경된 닉네임 닉네임테이블로 이동
        String uploadNickQuery = "insert into Member_nickname (mem_idx,nickname) VALUES (?,?)";
        Object[] uploadNickParams = new Object[]{memIdx,patchMemReq.getNick()};
        this.jdbcTemplate.update(uploadNickQuery,uploadNickParams);
    }
    // 닉네임 변경횟수
    public int editNickNum(int memIdx){
        String editNickNumQuery = "select count(mem_idx) from Member_nickname where mem_idx = ?";

        int num = this.jdbcTemplate.queryForObject(editNickNumQuery, int.class, memIdx);
        return num;
    }
    //비밀번호 변경
    public void editPw(String pw,int memIdx){
        String editPwQuery = "update Member set mem_password = ? where mem_idx = ?";
        this.jdbcTemplate.update(editPwQuery,pw,memIdx);
    }
    //유저 조회
    public GetMemRes getMem(int memIdx) {
        String getMemQuery = "select * from Member where mem_idx = ?";
        return this.jdbcTemplate.queryForObject(getMemQuery,
                (rs, rowNum) -> new GetMemRes(
                        rs.getString("mem_email"),
                        rs.getString("mem_phone"),
                        rs.getString("mem_nickname"),
                        rs.getString("mem_profile_content"),
                        rs.getString("mem_birth"),
                        rs.getString("mem_address_code"),
                        rs.getString("mem_address"),
                        rs.getString("mem_address_detail"),
                        rs.getString("mem_profile_url")),
                memIdx);
    }
    //핸드폰번호 중복 확인
    public int checkPhone(String tel) {
        String checkPhoneQuery = "select count(*) from Member where mem_phone = ?";
        // 있으면 1 없으면 0
        return this.jdbcTemplate.queryForObject(checkPhoneQuery, int.class, tel);
    }

    //이메일 중복 확인
    public int checkEmail(String email) {
        String checkEmailQuery = "select count(*) from Member where mem_email = ?";
        // 있으면 1 없으면 0
        return this.jdbcTemplate.queryForObject(checkEmailQuery, int.class, email);
    }

    //인증 코드 발송(인증테이블에 저장)
    public int createAuth(int type, String key) {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = time.format(timeFormatter);

        String createAuthQuery = "insert into Member_auth (ma_key, ma_type, ma_generate_time, ma_expired) values (?, ?, ?, ?)";
        Object[] createAuthParams = new Object[]{key, Integer.toString(type), currentTime, false}; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(createAuthQuery, createAuthParams);    //인증 정보 생성

        String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        int authIdx = this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
        return authIdx;
    }

    // 인증 코드 찾기
    public Optional<PostAuthNumberReq> findByAuthIdx(int maIdx) {
        List<PostAuthNumberReq> result = this.jdbcTemplate.query("select * from Member_auth where ma_idx = ?", authRowMapper(), maIdx);

        return result.stream().findAny();
    }

    private static RowMapper<PostAuthNumberReq> authRowMapper() {
        return (rs, rowNum) -> {
            PostAuthNumberReq postAuthNumberReq = new PostAuthNumberReq();
            postAuthNumberReq.setAuthIdx(rs.getInt("ma_idx"));
            postAuthNumberReq.setType(rs.getString("ma_type"));
            postAuthNumberReq.setAuthNumber(rs.getString("ma_key"));
            postAuthNumberReq.setCreatedAt(rs.getTimestamp("ma_generate_time"));
            return postAuthNumberReq;
        };
    }
    //탈퇴하기
    public void postWithdraw(int memIdx)throws BaseException{
        try{
            String selectMemQuery = "select mem_idx, mem_email,mem_phone,mem_nickname from Member where mem_idx = ?";
            Member member = this.jdbcTemplate.queryForObject(selectMemQuery,
                    (rs, rowNum) -> new Member(
                            rs.getInt("mem_idx"),
                            rs.getString("mem_email"),
                            rs.getString("mem_phone"),
                            rs.getString("mem_nickname")
                    )
                    , memIdx);
            String postWithdrawQuery = "insert into Member_withdraw(mem_email,mem_phone,mem_nickname,mw_time) VALUES (?,?,?,now())";
            this.jdbcTemplate.update(postWithdrawQuery, member.getEmail(), member.getPhone(), member.getNickname());

            String deletMemberQuery = "delete from Member where mem_idx = ?";
            this.jdbcTemplate.update(deletMemberQuery, memIdx);
        }catch (EmptyResultDataAccessException e){
            throw new BaseException(POST_MEMBER_WITHDRAW);
        }
    }
    //사용자 차단하기
    public void postMemblock(PostMemblockReq postMemblockReq)throws BaseException{
        String findDoubleQuery = "select count(*) from Member_block where mem_idx = ? and blocked_mem_idx = ?";
        int result = this.jdbcTemplate.queryForObject(findDoubleQuery,Integer.class,postMemblockReq.getMemIdx(),postMemblockReq.getBlockmemIdx());

        if(result == 0){
            String postMemblockQuery = "insert into Member_block(mem_idx,blocked_mem_idx,block_time)VALUES(?,?,now())";
            this.jdbcTemplate.update(postMemblockQuery,postMemblockReq.getMemIdx(),postMemblockReq.getBlockmemIdx());
        }else{
            throw new BaseException(POST_NEMBER_BLOCK_DOUBLE);
        }
    }


    // 이메일 찾기
    public String findEmail(String phone) throws BaseException{
        try {
            String findEmailQuery = "select mem_email from Member where mem_phone = ?";
            return this.jdbcTemplate.queryForObject(findEmailQuery, String.class, phone);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.POST_AUTH_MEMBER_NOT_EXIST);
        }
    }

    // 비밀번호 재설정
    public void changePw(int memIdx, String pw) throws BaseException {
        String changePwQuery = "update Member set mem_password = ? where mem_idx = ?";
        this.jdbcTemplate.update(changePwQuery, pw, memIdx);
    }
    
    // 유저 인덱스 조회
    public int getMemIdx(String email) {
        String getMemIdxQuery = "select mem_idx from Member where mem_email = ?";
        return this.jdbcTemplate.queryForObject(getMemIdxQuery, Integer.class, email);
    }
}