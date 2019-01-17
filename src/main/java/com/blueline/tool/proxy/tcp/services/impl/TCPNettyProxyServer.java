package com.blueline.tool.proxy.tcp.services.impl;


import com.blueline.tool.proxy.tcp.domain.ProxyDefinition;
import com.blueline.tool.proxy.tcp.handlers.ProxyInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class TCPNettyProxyServer extends AbstractNettyProxyServer {

	public TCPNettyProxyServer(ProxyDefinition definition) {
		super(definition);
	}

	@Override
	protected Channel doStart(EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
		ServerBootstrap bootstrap = new ServerBootstrap();
//		final ProxyFrontendHandler frontendHandler = new ProxyFrontendHandler(definition);
		ChannelFuture cf = bootstrap.group(bossGroup,workerGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 50)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30*1000)
				.option(ChannelOption.SO_REUSEADDR,true)
//				.option(ChannelOption.SO_KEEPALIVE,true)//TODO
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childHandler(new ProxyInitializer(definition,trafficHandler))
				.childOption(ChannelOption.AUTO_READ,true)
				.bind(definition.getLocalPort());

		return cf.channel();

	}
}
