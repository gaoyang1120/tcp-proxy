package com.blueline.tool.proxy.tcp.handlers.echo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter{
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Client handler: " + msg);
        ctx.writeAndFlush(msg);
    }
}
