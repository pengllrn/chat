package com.yeta.chat.netty;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Netty配置
 * @author YETA
 * @date 2018/11/25/19:27
 */
public class NettyConfig {

    //存储每一个浏览器接入进来时的channel对象
    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //IP地址
    public static final String IP = "localhost";

    //端口号
    public static final int PORT = 8081;

    //路径
    public static final String URL = "/webSocket";
}
