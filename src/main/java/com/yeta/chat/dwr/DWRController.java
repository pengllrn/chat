package com.yeta.chat.dwr;

import com.alibaba.fastjson.JSON;
import com.yeta.chat.util.CommonRequest;
import com.yeta.chat.util.CommonResponse;
import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.proxy.dwr.Util;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author YETA
 * @date 2018/11/23/11:50
 */
@RestController
@RemoteProxy
public class DWRController {

    /**
     * 根据请求参数type处理不同类型请求
     * @param reqMessage
     */
    @RemoteMethod
    public void chat(String reqMessage) {
        //请求
        CommonRequest request = JSON.parseObject(reqMessage, CommonRequest.class);
        Integer type = request.getType();
        //返回
        CommonResponse response;
        //所有会话
        WebContext webContext = WebContextFactory.get();
        Collection<ScriptSession> sessions = webContext.getAllScriptSessions();
        //构建发送所需的JS脚本
        ScriptBuffer scriptBuffer = new ScriptBuffer();
        if (type == 1) {        //群聊消息
            //调用客户端的JS脚本函数
            scriptBuffer.appendScript("chat(");
            //这个message可以被过滤处理一下，或者做其他的处理操作。这视需求而定。
            response = new CommonResponse(1000, 1, request.getMessage());
            scriptBuffer.appendData(JSON.toJSONString(response));
            scriptBuffer.appendScript(")");
            Util util = new Util(sessions);     //sessions，群发
            util.addScript(scriptBuffer);
        } else if (type == 2) {        //私聊消息
            for (ScriptSession session : sessions) {
                if (session.getId().equals(request.getTarget())) {
                    //调用客户端的JS脚本函数
                    scriptBuffer.appendScript("chat(");
                    //这个message可以被过滤处理一下，或者做其他的处理操作。这视需求而定。
                    response = new CommonResponse(1000, 2, request.getMessage());
                    scriptBuffer.appendData(JSON.toJSONString(response));
                    scriptBuffer.appendScript(")");
                    Util util = new Util(session);     //session，单独发
                    util.addScript(scriptBuffer);
                }
            }
        } else if (type == 3) {     //返回所有会话id，用于私聊
            List<String> ids = new ArrayList<String>();
            for (ScriptSession session : sessions) {
                ids.add(session.getId());
            }
            //调用客户端的JS脚本函数
            scriptBuffer.appendScript("addTarget(");
            //这个message可以被过滤处理一下，或者做其他的处理操作。这视需求而定。
            response = new CommonResponse(1000, 4, ids);
            scriptBuffer.appendData(JSON.toJSONString(response));
            scriptBuffer.appendScript(")");
            Util util = new Util(sessions);     //sessions，群发
            util.addScript(scriptBuffer);
        }
    }
}
