package com.umc.i.src.chat;

import com.umc.i.src.chat.model.ChatRoom;
import com.umc.i.src.chat.model.post.PostChatRoomReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
@Slf4j
public class ChatDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /*public void postChatRoom(ChatRoom chatRoom){
        String postChatRoomQuery = "insert into Chatting_room(mem1_idx,mem2_idx) values (?,?)";
        this.jdbcTemplate.update(postChatRoomQuery,chatRoom.getMemIdx1(),chatRoom.getMemIdx2());
    }

    public ChatRoom findRoomIdx(int roomIdx) {
        String findRoomIdxQuery = "select mem1_idx,mem2_idx from Chatting_room where room_idx = ?";

        return this.jdbcTemplate.queryForObject(findRoomIdxQuery,
                (rs, rowNum) -> new ChatRoom(
                        rs.getInt("mem1_idx"),
                        rs.getInt("mem2_idx")
                ),roomIdx);
    }*/
}
