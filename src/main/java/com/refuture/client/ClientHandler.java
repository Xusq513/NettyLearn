package com.refuture.client;

import com.refuture.dto.TicketInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * <p>
 * Netty客户端处理器
 * </p>
 *
 * @author Michael Xu
 * @version 1.0
 * @date 2020/3/25 16:35
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        try {
//            ByteBuf bb = (ByteBuf)msg;
//            byte[] respByte = new byte[bb.readableBytes()];
//            bb.readBytes(respByte);
//            String respStr = new String(respByte, Constant.charset);
//            System.err.println("client--收到响应：" + respStr);

            // 直接转成对象
            handlerObject(ctx, msg);

        } finally {
            // 必须释放msg数据
            ReferenceCountUtil.release(msg);

        }

    }

    private void handlerObject(ChannelHandlerContext ctx, Object msg) {
        try {
            TicketInfo ticketInfo = (TicketInfo) msg;
            System.out.println("server 获取信息：" + ticketInfo);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }


    // 数据读取完毕的处理
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.channel().closeFuture();
        System.err.println("客户端读取数据完毕");
    }

    // 出现异常的处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("client 读取数据出现异常");
        ctx.close();
    }

}
