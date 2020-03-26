package com.refuture.server;

import com.refuture.common.Constant;
import com.refuture.common.KafkaTools;
import com.refuture.dto.TicketInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

/**
 * <p>
 *    Netty服务端自定义Handler
 * </p>
 *
 * @author Michael Xu
 * @version 1.0
 * @date 2020/3/25 16:35
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    // 读取数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        handlerObject(ctx, msg);
    }

    /**
     * 将请求信息直接转成对象
     *
     * @param ctx
     * @param msg
     */
    private void handlerObject(ChannelHandlerContext ctx, Object msg) {
        // 需要序列化 直接把msg转成对象信息，一般不会用，可以用json字符串在不同语言中传递信息
        TicketInfo ticketInfo = (TicketInfo) msg;
        System.out.println("server 获取信息：" + ticketInfo);
        ticketInfo.setDealTime(LocalDateTime.now());
        KafkaTools.sendMessage("ticket",ticketInfo);
        ctx.writeAndFlush(ticketInfo);
    }


    /**
     * 最简单的处理
     *
     * @param ctx
     * @param msg
     * @throws UnsupportedEncodingException
     */
    public void simpleRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {

        ByteBuf bb = (ByteBuf) msg;
        // 创建一个和buf同等长度的字节数组
        byte[] reqByte = new byte[bb.readableBytes()];
        // 将buf中的数据读取到数组中
        bb.readBytes(reqByte);
        String reqStr = new String(reqByte, Constant.charset);
        System.err.println("server 接收到客户端的请求： " + reqStr);
        String respStr = new StringBuilder("来自服务器的响应").append(reqStr).append("$_").toString();

        // 返回给客户端响应                                                                                                                                                       和客户端链接中断即短连接，当信息返回给客户端后中断
        ctx.writeAndFlush(Unpooled.copiedBuffer(respStr.getBytes()));//.addListener(ChannelFutureListener.CLOSE);
        // 有了写操作（writeAndFlush）下面就不用释放msg
//      ReferenceCountUtil.release(msg);
    }

    /**
     * 有分隔符的请求信息分包情况处理，包含了转码
     *
     * @param ctx
     * @param msg
     */
    private void Delimiterread(ChannelHandlerContext ctx, Object msg) {
        // 如果把msg直接转成字符串，必须在服务中心添加 socketChannel.pipeline().addLast(new StringDecoder());
        String reqStr = (String) msg;
        System.err.println("server 接收到请求信息是：" + reqStr);
        String respStr = new StringBuilder("来自服务器的响应").append(reqStr).append("$_").toString();

        // 返回给客户端响应                                                                                                                                                       和客户端链接中断即短连接，当信息返回给客户端后中断
        ctx.writeAndFlush(Unpooled.copiedBuffer(respStr.getBytes())).addListener(ChannelFutureListener.CLOSE);
    }


    // 数据读取完毕的处理
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.err.println("服务端读取数据完毕");
    }

    // 出现异常的处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("server 读取数据出现异常");
        ctx.close();
    }

}
