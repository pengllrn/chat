package com.yeta.chat.stomp;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @author YETA
 * @date 2018/11/22/23:22
 */
@Configuration
@EnableWebSocketMessageBroker   //开启STOMP协议来传输基于代理（message broker）的消息，这时控制器支持@MessageMapping
public class CommonWebSocketMessageBrokerConfigurer implements WebSocketMessageBrokerConfigurer {

    /**
     * 注册STOMP协议的节点（endpoint），并映射指定的URL
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //指定使用SocketJS协议
        registry.addEndpoint("/endpoint").withSockJS();
    }

    /**
     * 配置消息代理
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        //广播式应配置一个/topic消息代理
        //点对点式应配置一个/queue消息代理
        registry.enableSimpleBroker("/topic", "/queue");
    }
}
