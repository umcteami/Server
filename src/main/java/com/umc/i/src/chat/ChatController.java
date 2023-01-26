package com.umc.i.src.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Controller
@ServerEndpoint("/chat")
@Slf4j
public class ChatController extends Socket {
    private static final List<Session> session = new ArrayList<Session>();
    @GetMapping("/")
    public String index(){
        return "chatting.html";
    }
    @OnOpen
    public void open(Session newUser){
        log.info("{}","연결됨");
        session.add(newUser);
        log.info("{}",newUser.getId());
    }
    @OnMessage
    public void getMsg(Session recieveSession,String msg){
        for(int i=0; i<session.size(); i++){
            if(!recieveSession.getId().equals(session.get(i).getId())){
                try {
                    session.get(i).getBasicRemote().sendText("상대:"+msg);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }else{
                try {
                    session.get(i).getBasicRemote().sendText("나:"+msg);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
