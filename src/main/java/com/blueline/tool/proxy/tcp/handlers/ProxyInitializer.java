package com.blueline.tool.proxy.tcp.handlers;

import com.blueline.lang.Property;
import com.blueline.tool.proxy.tcp.domain.ProxyDefinition;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.traffic.AbstractTrafficShapingHandler;


public class ProxyInitializer extends ChannelInitializer<SocketChannel> {



	String nettyLogDataLevel=Property.get("logging.level.io.netty",null);

	private final ProxyDefinition proxyDefinition;
	private final AbstractTrafficShapingHandler trafficShapingHandler;

	public ProxyInitializer(ProxyDefinition proxyDefinition, AbstractTrafficShapingHandler trafficShapingHandler) {
		this.proxyDefinition = proxyDefinition;
		this.trafficShapingHandler = trafficShapingHandler;
	}



	@Override
	protected void initChannel(SocketChannel ch) {
		if(nettyLogDataLevel!=null&&proxyDefinition.isDebug()){
			LogLevel logLevel=LogLevel.valueOf(nettyLogDataLevel);
			ch.pipeline().addLast(new LoggingHandler(logLevel));
		}else if(proxyDefinition.isDebug()){
			ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
		}

		ch.pipeline().addLast(trafficShapingHandler,
				new ProxyFrontendHandler(proxyDefinition));

	}
}
