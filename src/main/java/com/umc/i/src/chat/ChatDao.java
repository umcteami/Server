package com.umc.i.src.chat;
import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.chat.model.ChatImg;
import com.umc.i.src.chat.model.ChatMessage;
import com.umc.i.src.chat.model.get.GetChatRoomRes;
import com.umc.i.src.chat.model.get.GetChatRoomsRes;
import com.umc.i.src.chat.model.post.PostChatRoom;
import com.umc.i.src.chat.model.post.PostChatRoomOutReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class ChatDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    //방생성
    public void postChatRoom(PostChatRoom postChatRoom) throws BaseException {
        String blockCheckQuery = "select count(*) from Member_block where (mem_idx = ? and blocked_mem_idx = ?) or\n" +
                "                                        (mem_idx = ? and blocked_mem_idx = ?)";
        int block = this.jdbcTemplate.queryForObject(blockCheckQuery,int.class,postChatRoom.getMemIdx1(),postChatRoom.getMemIdx2(),
                postChatRoom.getMemIdx2(),postChatRoom.getMemIdx1());
        if(block !=0){
            throw new BaseException(BaseResponseStatus.CHATTING_BLAME_NOTABLE);
        }else{
            String postChatRoomQuery = "insert into Chatting_room(mem1_idx, mem2_idx,room_created_at,room_quit1,room_quit2) values (?,?,now(),0,0)";
            this.jdbcTemplate.update(postChatRoomQuery, postChatRoom.getMemIdx1(), postChatRoom.getMemIdx2());
        }
    }
    //메세지 보내기
    public void sendMsg(ChatMessage chatMsg){
        String sendMsgQuery = "insert into Chatting(room_idx,mem_send_idx,chat_content,chat_time,chat_image,chat_read) values(?,?,?,now(),0,1)";
        this.jdbcTemplate.update(sendMsgQuery,chatMsg.getRoomIdx(),chatMsg.getSender(),chatMsg.getMessage());
        String moveChatRoomQuery = "update Chatting_room set room_recent_time = now(),room_recent_chat = ? where room_idx = ?";
        this.jdbcTemplate.update(moveChatRoomQuery,chatMsg.getMessage(),chatMsg.getRoomIdx());
    }
    //모든 채팅방 보기
    public List<GetChatRoomsRes> getChatRooms(int memIdx){
        String getChatRoomsQuery = "select room_idx,if(mem1_idx = ?,mem2_idx,mem1_idx) as sender,room_recent_time,room_recent_chat,mem_nickname,mem_profile_url\n" +
                "                from Chatting_room Cr join Member M on mem1_idx = ? or mem2_idx = ?\n" +
                "                where M.mem_idx = if(mem1_idx = ?,mem2_idx,mem1_idx) and if(mem1_idx = ?,Cr.room_quit1,room_quit2)=0";
        String getReadNumQuery = "select count(*)from Chatting C join Chatting_room Cr on C.room_idx = Cr.room_idx\n" +
                "                        where if(Cr.mem1_idx = ?,Cr.mem2_idx,Cr.mem1_idx) = C.mem_send_idx and chat_read = 1 and C.room_idx = ?";
        return this.jdbcTemplate.query(getChatRoomsQuery,
                (rs, rowNum) -> {
                    GetChatRoomsRes getChatRoomsRes = new GetChatRoomsRes(
                            rs.getInt("room_idx"),
                            rs.getInt("sender"),
                            rs.getString("mem_profile_url"),
                            rs.getString("mem_nickname")
                    );

                    getChatRoomsRes.setRecentChat(rs.getString("room_recent_chat"));
                    getChatRoomsRes.setRecentTime(rs.getString("room_recent_time"));
                    getChatRoomsRes.setNoReadNum(this.jdbcTemplate.queryForObject(getReadNumQuery,int.class,memIdx,getChatRoomsRes.getRoomIdx()));
                    return getChatRoomsRes;
                },memIdx,memIdx,memIdx,memIdx,memIdx);
    }
    //하나의 채팅방 보기+ 이미지(미완)
    public List<GetChatRoomRes> getChatRoomIdx(int roomIdx,int memIdx) throws BaseException {
        String findRoomIdxQuery = "select mem_send_idx,chat_content,chat_time,chat_read,chat_image,\n" +
                "       if(C.mem_send_idx != ?,(select mem_profile_url from Member where Member.mem_idx = C.mem_send_idx),null) as profile,\n" +
                "       if(C.mem_send_idx != ?,(select mem_nickname from Member where Member.mem_idx = C.mem_send_idx),null) as nick\n" +
                "from Chatting C where room_idx = ?";
        String findChatImgsQuery = "select image_url from Image_url join Chatting C on content_idx = C.chat_idx and content_category = 5 where C.chat_idx = ?";


        String blockCheckQuery = "select count(*) from Member_block join Chatting_room Cr on (mem_idx = Cr.mem1_idx and blocked_mem_idx = Cr.mem2_idx) or\n" +
                "                                        (mem_idx = Cr.mem2_idx and blocked_mem_idx = Cr.mem1_idx) where Cr.room_idx = ?;";
        int block = this.jdbcTemplate.queryForObject(blockCheckQuery,int.class,roomIdx);
        if(block != 0){
            throw new BaseException(BaseResponseStatus.CHATTING_BLAME_NOTABLE);
        }
        return this.jdbcTemplate.query(findRoomIdxQuery,
                (rs, rowNum) -> {

                    GetChatRoomRes getChatRoomRes = null;
                    if(rs.getInt("chat_image") == 0){
                        getChatRoomRes = new GetChatRoomRes(
                                rs.getInt("mem_send_idx"),
                                rs.getString("chat_content"),
                                rs.getString("chat_time"),
                                rs.getString("profile"),
                                rs.getString("nick"));
                    }else {
                        List<String> listImg = this.jdbcTemplate.query(findChatImgsQuery,
                               (rs1, rowNum1) -> String.valueOf(rs1.getString("image_url")),rs.getInt("C.chat_idx"));
                        getChatRoomRes = new GetChatRoomRes(
                                rs.getInt("mem_send_idx"),
                                rs.getString("chat_time"),
                                rs.getString("profile"),
                                rs.getString("nick"));
                        getChatRoomRes.setChatImg(listImg);
                    }

                    return getChatRoomRes;
                },memIdx,memIdx,roomIdx);
    }

    //채팅방 나간 시점 등록 + 읽음여부 판단
    public void postRoomOut(PostChatRoomOutReq roomOut){
        int mem1Idx = this.jdbcTemplate.queryForObject("select count(*)from Chatting_room C where C.room_idx = ? and C.mem1_idx = ?",
                int.class,roomOut.getRoomIdx(),roomOut.getMemIdx());
        //mysql 문법 오류
        if (mem1Idx == 1){
            String outChatRoomQuery = "update Chatting_room set room_quit1_time = now() where room_idx = ?";
            this.jdbcTemplate.update(outChatRoomQuery,roomOut.getRoomIdx());
        }else{
            String outChatRoomQuery = "update Chatting_room set room_quit2_time = now() where room_idx = ?";
            this.jdbcTemplate.update(outChatRoomQuery,roomOut.getRoomIdx());
        }

        String readChatQuery = "update Chatting set chat_read = if(chat_time <\n" +
                "                                   (select if(mem1_idx = ?,room_quit1_time,room_quit2_time)from Chatting_room R where R.room_idx = ? ) ,0,1) where room_idx = ? and mem_send_idx = (select if(mem1_idx = ?,mem2_idx,mem1_idx) from Chatting_room where Chatting_room.room_idx = ?)";

        this.jdbcTemplate.update(readChatQuery,roomOut.getMemIdx(),roomOut.getRoomIdx(),roomOut.getRoomIdx(),roomOut.getMemIdx(),roomOut.getRoomIdx());

    }
    //채팅방 삭제
    public void delChatRoom(PostChatRoomOutReq roomOut) {
        int mem1Idx = this.jdbcTemplate.queryForObject("select count(*)from Chatting_room C where C.room_idx = ? and C.mem1_idx = ?",
                int.class,roomOut.getRoomIdx(),roomOut.getMemIdx());
        if(mem1Idx == 1){
            String delChatRoomQuery = "update Chatting_room set room_quit1 = 1 where room_idx = ?";
            this.jdbcTemplate.update(delChatRoomQuery,roomOut.getRoomIdx());
        }else {
            String delChatRoomQuery = "update Chatting_room set room_quit2 = 1 where room_idx = ?";
            this.jdbcTemplate.update(delChatRoomQuery, roomOut.getRoomIdx());
        }
    }

    //이미지 저장-unchecked
    public void sendImg(ChatImg img){
        String sendMsgQuery = "insert into Chatting(room_idx,mem_send_idx,chat_content,chat_time,chat_image) values(?,?,null,now(),?)";
        this.jdbcTemplate.update(sendMsgQuery,img.getRoomIdx(),img.getSender(),img.getFiles().size());
        String lastIdxQuery = "select last_insert_id()";
        int lastIdx = this.jdbcTemplate.queryForObject(lastIdxQuery,int.class);

        String moveChatRoomQuery = "update Chatting_room set room_recent_time = now(),room_recent_chat = '사진을 보냈습니다.' where room_idx = ?";
        this.jdbcTemplate.update(moveChatRoomQuery,img.getRoomIdx());

        String sendImgQuery = "insert into Image_url(content_category,content_idx,image_url,image_order) values(5,?,?,?)";
        int order=0;
        for (String putImg : img.getFiles()){
            this.jdbcTemplate.update(sendImgQuery,lastIdx,putImg,order);
            order++;
        }
    }
}
