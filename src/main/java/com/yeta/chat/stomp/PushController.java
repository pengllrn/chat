package com.yeta.chat.stomp;

import com.yeta.chat.util.CommonRequest;
import com.yeta.chat.util.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author YETA
 * @date 2018/11/22/23:22
 */
@RestController
public class PushController {

    //保存所有登陆的用户
    public static ConcurrentSkipListSet<String> users = new ConcurrentSkipListSet<String>();

    @Autowired
    private SimpMessagingTemplate smt;    //通过该对象向浏览器发送消息

    /**
     * 群发方法
     * @param request
     * @return
     */
    @MessageMapping(value = "/topic/pushAll/receive")       //服务器接收浏览器消息的地址
    @SendTo(value = "/topic/pushAll")       //浏览器订阅服务器消息的地址，服务器将会把消息推送到订阅了该地址的浏览器
    public CommonResponse pushAll(CommonRequest request) {
        return new CommonResponse(1000, 1, request.getMessage());
    }

    /**
     * 群发方法，同上面的方法效果一样
     * @param request
     * @return
     */
    @MessageMapping(value = "/topic/pushAll/receive1")
    public void pushAll1(CommonRequest request) {
        smt.convertAndSend("/topic/pushAll1", new CommonResponse(1000, 1, request.getMessage()));
    }

    /**
     * 推送在线用户
     * @return
     */
    @MessageMapping(value = "/topic/pushUsers")
    @SendTo(value = "/topic/pushAll")
    public CommonResponse pushUsers(Principal principal, CommonRequest request) {
        if (request.getType() == 5) {
            users.add(principal.getName());
        } else if (request.getType() == 6) {
            users.remove(principal.getName());
        }
        return new CommonResponse(1000, 4, users);
    }

    /**
     * 只推送自己方法
     * @param request
     * @return
     */
    @MessageMapping(value = "/queue/pushMyself/receive")
    @SendToUser(value = "/queue/pushMyself", broadcast = false)     //只推送给自己，broadcast=false表示如果一个账户登陆多个浏览器，只将消息推送给发出请求的浏览器
    public CommonResponse pushMyself(CommonRequest request) {
        return new CommonResponse(1000, 1, request.getMessage());
    }

    /**
     * 私发方法
     * @param principal
     * @param request
     * @return
     */
    @MessageMapping(value = "/queue/pushSomeone/receive")
    public CommonResponse pushSomeone(Principal principal, CommonRequest request) {
        String target = request.getTarget();
        for (String username : users) {
            if (username.equals(target)) {
                CommonResponse response = new CommonResponse(1000, 1, principal.getName() + " say: " + request.getMessage());
                smt.convertAndSendToUser(target, "/queue/pushSomeone", response);
            }
        }
        return new CommonResponse(1001, 2, "error");
    }
}
