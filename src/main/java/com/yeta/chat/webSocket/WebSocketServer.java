package com.yeta.chat.webSocket;

import com.alibaba.fastjson.JSON;
import com.yeta.chat.util.CommonRequest;
import com.yeta.chat.util.CommonResponse;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author YETA
 * @date 2018/11/22/21:20
 */
@ServerEndpoint("/webSocket")
@Component
public class WebSocketServer {

    //保存所有的会话
    private static ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<String, Session>();

    /**
     * 建立连接
     * @param session
     * @throws IOException
     */
    @OnOpen
    public void onOpen(Session session) throws IOException {
        //保存会话
        if (sessions.get(session.getId()) == null) {
            sessions.put(session.getId(), session);
        }
        //服务器主动推送所有会话id
        for (Session s : sessions.values()) {
            String ids = sessions.keySet().toString();
            CommonResponse response = new CommonResponse(1000, 4, ids);
            s.getBasicRemote().sendText(JSON.toJSONString(response));
        }
    }

    /**
     * 关闭连接
     * @param session
     * @throws IOException
     */
    @OnClose
    public void onClose(Session session) throws IOException {
        sessions.remove(session.getId(), session);
        //服务器主动推送所有会话id
        for (Session s : sessions.values()) {
            String ids = sessions.keySet().toString();
            CommonResponse response = new CommonResponse(1000, 4, ids);
            s.getBasicRemote().sendText(JSON.toJSONString(response));
        }
    }

    /**
     * 收到消息
     * @param session
     * @param sRequest
     * @throws IOException
     */
    @OnMessage
    public void onMessage(Session session, String sRequest) throws IOException {
        CommonRequest request = JSON.parseObject(sRequest, CommonRequest.class);
        String message = request.getMessage();
        CommonResponse response;
        if (request.getType() == 1) {       //群聊消息
            for (Session s : sessions.values()) {
                response = new CommonResponse(1000, 1, message);
                s.getBasicRemote().sendText(JSON.toJSONString(response));
            }
        } else if (request.getType() == 2) {        //私聊消息
            String target = request.getTarget();
            for (Session s : sessions.values()) {
                if (s.getId().equals(target)) {
                    response = new CommonResponse(1000, 1, message);
                    s.getBasicRemote().sendText(JSON.toJSONString(response));
                }
            }
        } else if (request.getType() == 3) {        //浏览器自己请求资源
            response = new CommonResponse(1000, 3, message);
            session.getBasicRemote().sendText(JSON.toJSONString(response));
        }
    }

    /**
     * 出现错误
     * @param session
     * @param e
     * @throws IOException
     */
    @OnError
    public void onError(Session session, Throwable e) throws IOException {
        CommonResponse response = new CommonResponse(1001, 3, e.getMessage());
        session.getBasicRemote().sendText(JSON.toJSONString(response));
        e.printStackTrace();
    }
}
