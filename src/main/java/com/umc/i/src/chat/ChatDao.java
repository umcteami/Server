package com.umc.i.src.chat;
import com.umc.i.src.chat.model.ChatMessage;
import com.umc.i.src.chat.model.get.GetChatRoomRes;
import com.umc.i.src.chat.model.get.GetChatRoomsRes;
import com.umc.i.src.chat.model.post.PostChatRoom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
        String sendMsgQuery = "insert into Chatting(room_idx,mem_send_idx,chat_content,chat_time) values(?,?,?,now())";
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
    //하나의 채팅방 보기
    public List<GetChatRoomRes> getChatRoomIdx(int roomIdx) {
        String findRoomIdxQuery = "select mem_send_idx,chat_content,chat_time from Chatting where room_idx = ?";

        return this.jdbcTemplate.query(findRoomIdxQuery,
                (rs, rowNum) -> new GetChatRoomRes(
                        rs.getInt("mem_send_idx"),
                        rs.getString("chat_content"),
                        rs.getString("chat_time")
                ),roomIdx);
    }
    //이전에 작성한 채팅 가져오기
    public List<ChatMessage> getAllChat(int roomIdx){
        String getAllChatQuery = "select room_idx,mem_send_idx,chat_content from Chatting where room_idx = ?";
        return this.jdbcTemplate.query(getAllChatQuery,
                (rs, rowNum) -> new ChatMessage(
                        rs.getInt("room_idx"),
                        rs.getInt("mem_send_idx"),
                        rs.getString("chat_content")
                )
                ,roomIdx);
    }
}
