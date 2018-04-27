package com.blueline.tool.proxy.tcp.handlers;


import com.blueline.tool.proxy.tcp.domain.ProxyDefinition;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;


public class ProxyBackendHandler extends ChannelInboundHandlerAdapter {

	private final Channel inboundChannel;
	private final ProxyDefinition proxyDefinition;

	public ProxyBackendHandler(Channel inboundChannel, ProxyDefinition proxyDefinition) {
		this.inboundChannel = inboundChannel;
		this.proxyDefinition = proxyDefinition;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ctx.read();
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object msg) {
		if (ByteBuf.class.isAssignableFrom(msg.getClass())){
			proxyDefinition.getConnectionStats().appendBytesReceived(((ByteBuf)msg).capacity());
		}

		inboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) {
				if (future.isSuccess()) {
					ctx.channel().read();
				} else {
					future.channel().close();
				}
			}
		});
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		ProxyFrontendHandler.closeOnFlush(inboundChannel);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ProxyFrontendHandler.closeOnFlush(ctx.channel());
	}
}
