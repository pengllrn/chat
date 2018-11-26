package com.yeta.chat.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

/**
 * Netty启动类
 * @author YETA
 * @date 2018/11/25/20:18
 */
@Configuration
public class NettyService implements CommandLineRunner {

    //日志
    private static final Logger LOG = LoggerFactory.getLogger(NettyConfig.class);

    @Override
    public void run(String... args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(bossGroup, workGroup);
            sb.channel(NioServerSocketChannel.class);
            sb.childHandler(new CommonChannelInitializer());
            LOG.info("等待连接...");
            Channel channel = sb.bind(NettyConfig.PORT).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
