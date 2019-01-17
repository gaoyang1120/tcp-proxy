package com.blueline.tool.proxy.tcp.handlers;


import com.blueline.tool.proxy.tcp.domain.ProxyDefinition;
import com.blueline.tool.proxy.tcp.handlers.echo.BuffFormart;
import com.blueline.tool.proxy.tcp.handlers.utils.Formart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 后端Handler
 * @author Gaoyang
 *
 */
public class ProxyBackendHandler extends ChannelInboundHandlerAdapter {
//	static final Logger logger = LoggerFactory.getLogger(ProxyBackendHandler.class);
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
//		String convertByteBufToString = Formart.convertByteBufToString(msg);
//		logger.info("B-5 后端数据读取 核心转发到ctx"+ctx.toString()+" 远端信息msg:"+convertByteBufToString);
//		SocketAddress remoteAddress = ctx.channel().remoteAddress();
//		SocketAddress localAddress = ctx.channel().localAddress();
//		ChannelId channelId = ctx.channel().id();
//		boolean open = ctx.channel().isOpen();
//		boolean active = ctx.channel().isActive();
//		logger.info("B-2.4 remoteAddress:["+remoteAddress+"] --> "
//							+ "通过通道 channelId:["+channelId.asShortText()+", open:"+open+", active:"+active+" ] -->"
//							
//							+ " 连接到local["+localAddress+"] "
//							);
//		String format = BuffFormart.format(ctx, "WRITE", copy);
//		logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~format="+format);
		//记录数据大小
		if (ByteBuf.class.isAssignableFrom(msg.getClass())){
			proxyDefinition.getConnectionStats().appendBytesReceived(((ByteBuf)msg).capacity());
		}
		proxyFrontendHandler.getInboundChannel().writeAndFlush(msg);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		logger.debug("ProxyBackendHandler|channelInactive");
		logger.info("B3 channelInactive");
		proxyFrontendHandler.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.info("后端服务异常-----------------------");
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
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		super.userEventTriggered(ctx, evt);
		logger.info("用户可以传递一个自定义的对象当这个方法里"+evt);
	}
	
}
