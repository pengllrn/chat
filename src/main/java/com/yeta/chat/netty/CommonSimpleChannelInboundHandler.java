package com.yeta.chat.netty;

import com.alibaba.fastjson.JSON;
import com.yeta.chat.util.CommonRequest;
import com.yeta.chat.util.CommonResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 接收/处理/响应浏览器WebSocket请求的核心业务处理类
 * @author YETA
 * @date 2018/11/25/19:27
 */
public class CommonSimpleChannelInboundHandler extends SimpleChannelInboundHandler<Object> {

    //日志
    private static final Logger LOG = LoggerFactory.getLogger(CommonSimpleChannelInboundHandler.class);

    private WebSocketServerHandshaker handshaker;

    /**
     * 服务器处理浏览器WebSocket请求的核心方法
     * @param channelHandlerContext
     * @param o
     * @throws Exception
     */
    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        //处理浏览器向服务器发起HTTP握手请求
        if (o instanceof FullHttpRequest) {
            handleHttpRequest(channelHandlerContext, (FullHttpRequest) o);
        } else if (o instanceof WebSocketFrame) {       //处理WebSocket连接
            handleWebSocketFrame(channelHandlerContext, (WebSocketFrame) o);
        }
    }

    /**
     * 处理浏览器向服务器发起HTTP握手请求
     * @param ctx
     * @param req
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        if (!req.getDecoderResult().isSuccess() || !("websocket").equals(req.headers().get("Upgrade"))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(
                "ws://" + NettyConfig.IP + ":" + NettyConfig.PORT  + NettyConfig.URL,
                null,
                false);
        handshaker = factory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    /**
     * 服务器向浏览器发送消息
     * @param ctx
     * @param request
     * @param res
     */
    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, DefaultFullHttpResponse res) {
        if (res.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        //发送数据
        ChannelFuture future = ctx.channel().writeAndFlush(res);
        if (res.getStatus().code() != 200) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 处理WebSocket连接
     * @param ctx
     * @param frame
     */
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        //判断是否关闭WebSocket消息
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), ((CloseWebSocketFrame) frame).retain());
        }
        //判断是否是ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        //判断是否是二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            LOG.info("收到二进制消息");
            throw new RuntimeException(this.getClass().getName() + " 不支持二进制消息");
        }
        //获取浏览器向服务器发送的消息
        String reqMessage = ((TextWebSocketFrame) frame).text();
        CommonRequest request = JSON.parseObject(reqMessage, CommonRequest.class);
        Integer type = request.getType();
        CommonResponse response;
        TextWebSocketFrame twsf;
        if (type == 1) {       //群聊消息
            response = new CommonResponse(1000, 1, request.getMessage());
            twsf = new TextWebSocketFrame(JSON.toJSONString(response));
            NettyConfig.channels.writeAndFlush(twsf);
        } else if (type == 2) {        //私聊消息
            for (Channel channel : NettyConfig.channels) {
                if (channel.id().toString().equals(request.getTarget())) {
                    response = new CommonResponse(1000, 2, request.getMessage());
                    twsf = new TextWebSocketFrame(JSON.toJSONString(response));
                    channel.writeAndFlush(twsf);
                }
            }
        } else if (type == 3) {        //浏览器自己请求资源
            sendAllChannelIds();
        }
    }

    /**
     * 服务器主动推送所有channel id
     */
    public void sendAllChannelIds() {
        List<String> ids = new ArrayList<String>();
        for (Channel channel : NettyConfig.channels) {
            ids.add(channel.id().toString());
        }
        CommonResponse response = new CommonResponse(1000, 4, ids);
        TextWebSocketFrame twsf = new TextWebSocketFrame(JSON.toJSONString(response));
        NettyConfig.channels.writeAndFlush(twsf);
    }

    /**
     * 出现异常时调用
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 浏览器与服务器创建连接时调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyConfig.channels.add(ctx.channel());
        sendAllChannelIds();
    }

    /**
     * 浏览器与服务器断开连接时调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyConfig.channels.remove(ctx.channel());
        sendAllChannelIds();
    }

    /**
     * 服务器接收浏览器发送过来的数据结束之后调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
