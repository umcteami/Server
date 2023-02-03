package com.umc.i.src.chat;
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
    public void postChatRoom(PostChatRoom postChatRoom){
        String postChatRoomQuery = "insert into Chatting_room(mem1_idx, mem2_idx,room_created_at) values (?,?,now())";
        this.jdbcTemplate.update(postChatRoomQuery, postChatRoom.getMemIdx1(), postChatRoom.getMemIdx2());
    }
    //메세지 보내기
    public void sendMsg(ChatMessage chatMsg){
        String sendMsgQuery = "insert into Chatting(room_idx,mem_send_idx,chat_content,chat_time,chat_image) values(?,?,?,now(),0)";
        this.jdbcTemplate.update(sendMsgQuery,chatMsg.getRoomIdx(),chatMsg.getSender(),chatMsg.getMessage());
        String moveChatRoomQuery = "update Chatting_room set room_recent_time = now(),room_recent_chat = ? where room_idx = ?";
        this.jdbcTemplate.update(moveChatRoomQuery,chatMsg.getMessage(),chatMsg.getRoomIdx());
    }
    //모든 채팅방 보기
    public List<GetChatRoomsRes> getChatRooms(int memIdx){
        String getChatRoomsQuery = "select room_idx,if(mem1_idx = ?,mem1_idx,mem2_idx) as sender,room_recent_time,room_recent_chat,mem_nickname,mem_profile_url\n" +
                "from Chatting_room join Member M on mem1_idx = ? or mem2_idx = ? where M.mem_idx = if(mem1_idx = ?,mem2_idx,mem1_idx)";

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

                    return getChatRoomsRes;
                },memIdx,memIdx,memIdx,memIdx);
    }
    //하나의 채팅방 보기+ 이미지(미완)
    public List<GetChatRoomRes> getChatRoomIdx(int roomIdx) {
        String findRoomIdxQuery = "select mem_send_idx,chat_content,chat_time from Chatting where room_idx = ?";

        return this.jdbcTemplate.query(findRoomIdxQuery,
                (rs, rowNum) -> new GetChatRoomRes(
                        rs.getInt("mem_send_idx"),
                        rs.getString("chat_content"),
                        rs.getString("chat_time")
                ),roomIdx);
    }

    //채팅방 나간 시점 등록 + 읽음여부 판단
    public void postRoomOut(PostChatRoomOutReq roomOut){
        String outChatRoomQuery = "update Chatting_room set if(mem1_idx = ?,room_quit1_time,room_quit2_time) = now() where room_idx = ?";
        this.jdbcTemplate.update(outChatRoomQuery,roomOut.getMemIdx(),roomOut.getRoomIdx());

        String readChatQuery = "update Chatting set chat_read = if(chat_time <\n" +
                "                                   (select if(mem1_idx = ?,room_quit1_time,room_quit2_time)from Chatting_room R where R.room_idx = ? ) ,0,1) where room_idx = ? and mem_send_idx = (select if(mem1_idx = ?,mem2_idx,mem1_idx) from Chatting_room where Chatting_room.room_idx = ?)";

        this.jdbcTemplate.update(readChatQuery,roomOut.getMemIdx(),roomOut.getRoomIdx(),roomOut.getRoomIdx(),roomOut.getMemIdx(),roomOut.getRoomIdx());

    }
    //채팅방 삭제 -- db 논의 필요 누가 방을 삭제했는지 표시해야함
    public void delChatRoom(int romIdx) {

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
