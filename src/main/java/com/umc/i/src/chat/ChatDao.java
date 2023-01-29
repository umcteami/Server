package com.umc.i.src.chat;

import com.umc.i.src.chat.model.ChatMessage;
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

    public void postChatRoom(PostChatRoom postChatRoom){
        String postChatRoomQuery = "insert into Chatting_room(mem1_idx, mem2_idx) values (?,?)";
        this.jdbcTemplate.update(postChatRoomQuery, postChatRoom.getMemIdx1(), postChatRoom.getMemIdx2());
    }

    public void sendMsg(ChatMessage chatMsg){
        String sendMsgQuery = "insert into Chatting(room_idx,mem_send_idx,chat_content) values(?,?,?)";
        this.jdbcTemplate.update(sendMsgQuery,chatMsg.getRoomId(),chatMsg.getSender(),chatMsg.getMessage());
    }

    public List<GetChatRoomsRes> getChatRooms(){
        String getChatRoomsQuery = "select room_idx,mem1_idx,mem2_idx from Chatting_room";
        return this.jdbcTemplate.query(getChatRoomsQuery,
                (rs, rowNum) -> new GetChatRoomsRes(
                            rs.getInt("room_idx"),
                            rs.getInt("mem1_idx"),
                            rs.getInt("mem2_idx")
                    ));
    }
    /*public ChatRoom findRoomIdx(int roomIdx) {
        String findRoomIdxQuery = "select mem1_idx,mem2_idx from Chatting_room where room_idx = ?";

        return this.jdbcTemplate.queryForObject(findRoomIdxQuery,
                (rs, rowNum) -> new ChatRoom(
                        rs.getInt("mem1_idx"),
                        rs.getInt("mem2_idx")
                ),roomIdx);
    }*/
}
