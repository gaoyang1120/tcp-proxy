package com.blueline.tool.proxy.tcp.handlers;


import com.blueline.tool.proxy.tcp.domain.ProxyDefinition;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProxyBackendHandler extends ChannelInboundHandlerAdapter {
	static final Logger logger = LoggerFactory.getLogger(ProxyBackendHandler.class);
	private final ProxyFrontendHandler  proxyFrontendHandler;
	private final ProxyDefinition proxyDefinition;

	public ProxyBackendHandler(ProxyFrontendHandler proxyFrontendHandler, ProxyDefinition proxyDefinition) {
		this.proxyFrontendHandler = proxyFrontendHandler;
		this.proxyDefinition = proxyDefinition;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		proxyFrontendHandler.outBoundChannelReady();
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object msg) {
		if (ByteBuf.class.isAssignableFrom(msg.getClass())){
			proxyDefinition.getConnectionStats().appendBytesReceived(((ByteBuf)msg).capacity());
		}
		proxyFrontendHandler.getInboundChannel().writeAndFlush(msg);

	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		logger.debug("ProxyBackendHandler|channelInactive");
		proxyFrontendHandler.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.warn("BackendHandler connection is abnormally disconnected {}:{}",proxyDefinition.getAlias(),cause.getMessage());
		logger.debug("Stack information:",cause.getMessage());
		ProxyFrontendHandler.closeOnFlush(ctx.channel());
	}

	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
		if(ctx.channel().isWritable()){
			proxyFrontendHandler.setAutoRead(true);
		}else{
			proxyFrontendHandler.setAutoRead(false);
		}
	}
}
