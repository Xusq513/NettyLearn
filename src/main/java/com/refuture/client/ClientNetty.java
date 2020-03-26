package com.refuture.client;

import com.refuture.common.Constant;
import com.refuture.common.MarshallingCodefactory;
import com.refuture.dto.TicketInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.time.LocalDateTime;

/**
 * <p>
 * Netty客户端
 * </p>
 *
 * @author Michael Xu
 * @version 1.0
 * @date 2020/3/25 16:35
 */
public class ClientNetty {

    // 要请求的服务器的ip地址
    private String ip;
    // 服务器的端口
    private int port;

    public ClientNetty(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    // 请求端主题
    private void action(){
        // 反应器线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 启动器
        Bootstrap bs = new Bootstrap();
        try {
            bs.group(bossGroup)
                    .channel(NioSocketChannel.class)
                    // 设置选项参数，启用心跳检测
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            // marshalling 序列化对象的解码
                            socketChannel.pipeline().addLast(MarshallingCodefactory.buildDecoder());
                            // marshalling 序列化对象的编码
                            socketChannel.pipeline().addLast(MarshallingCodefactory.buildEncoder());

                            // 处理来自服务端的响应信息
                            socketChannel.pipeline().addLast(new ClientHandler());
                        }
                    });

            // 客户端开启
            ChannelFuture cf = bs.connect(ip, port).sync();

            TicketInfo ticketInfo = new TicketInfo()
                    .setMobileNo("18734806417")
                    .setStartStation("青年路")
                    .setEndStation("北运河西")
                    .setStartTime(LocalDateTime.now().minusHours(1L))
                    .setEndTime(LocalDateTime.now());
            cf.channel().writeAndFlush(ticketInfo);


            // 等待直到连接中断
            cf.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new ClientNetty("127.0.0.1", Constant.serverSocketPort).action();
    }

}
