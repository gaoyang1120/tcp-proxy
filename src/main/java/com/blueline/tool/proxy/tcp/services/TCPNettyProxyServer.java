package com.blueline.tool.proxy.tcp.services;


import com.blueline.tool.proxy.tcp.domain.ProxyDefinition;
import com.blueline.tool.proxy.tcp.handlers.ProxyFrontendHandler;
import com.blueline.tool.proxy.tcp.handlers.ProxyInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


public class TCPNettyProxyServer extends AbstractNettyProxyServer {

	public TCPNettyProxyServer(ProxyDefinition definition) {
		super(definition);
	}

	@Override
	protected Channel doStart(EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
		ServerBootstrap bootstrap = new ServerBootstrap();
		final ProxyFrontendHandler frontendHandler = new ProxyFrontendHandler(definition);
		ChannelFuture cf = bootstrap.group(bossGroup,workerGroup)
				.option(ChannelOption.SO_REUSEADDR,false)
				.channel(NioServerSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new ProxyInitializer(definition,trafficHandler))
				.childOption(ChannelOption.AUTO_READ,true)
				.bind(definition.getLocalPort());

		return cf.channel();

	}
}
