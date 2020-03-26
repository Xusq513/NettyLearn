package com.refuture.server;

import com.refuture.common.Constant;
import com.refuture.common.MarshallingCodefactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * <p>
 * Netty服务端
 * </p>
 *
 * @author Michael Xu
 * @version 1.0
 * @date 2020/3/25 16:35
 */
public class ServerNetty {

    private int port;

    public ServerNetty(int port) {
        this.port = port;
    }

    // netty 服务端启动
    public void action() throws InterruptedException {

        // 用来接收进来的连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 用来处理已经被接收的连接，一旦bossGroup接收到连接，就会把连接信息注册到workerGroup上
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // nio服务的启动类
            ServerBootstrap sbs = new ServerBootstrap();
            // 配置nio服务参数
            sbs.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // 说明一个新的Channel如何接收进来的连接
                    .option(ChannelOption.SO_BACKLOG, 128) // tcp最大缓存链接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true) //保持连接
                    .handler(new LoggingHandler(LogLevel.INFO)) // 打印日志级别
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {

                            // marshalling 序列化对象的解码
//                            socketChannel.pipeline().addLast(MarshallingCodefactory.buildDecoder());
                            // marshalling 序列化对象的编码
                            socketChannel.pipeline().addLast(MarshallingCodefactory.buildEncoder());
                            // 处理接收到的请求
                            socketChannel.pipeline().addLast(new ServerHandler()); // 这里相当于过滤器，可以配置多个
                        }
                    });

            System.err.println("server 开启--------------");
            // 绑定端口，开始接受链接
            ChannelFuture cf = sbs.bind(port).sync();

            // 等待服务端口的关闭；在这个例子中不会发生，但你可以优雅实现；关闭你的服务
            cf.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    // 开启netty服务线程
    public static void main(String[] args) throws InterruptedException {
        new ServerNetty(Constant.serverSocketPort).action();
    }

}
