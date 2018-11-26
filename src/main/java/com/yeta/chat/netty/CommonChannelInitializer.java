package com.yeta.chat.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 初始化连接时的各个组件
 * @author YETA
 * @date 2018/11/25/20:12
 */
public class CommonChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //HTTP消息编码解码
        socketChannel.pipeline().addLast("http-codec", new HttpServerCodec());
        //HTTP消息组装
        socketChannel.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
        //WebSocket通信支持
        socketChannel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
        //
        socketChannel.pipeline().addLast("handler", new CommonSimpleChannelInboundHandler());
    }
}
