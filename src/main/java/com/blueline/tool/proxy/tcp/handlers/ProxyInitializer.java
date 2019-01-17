package com.blueline.tool.proxy.tcp.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blueline.lang.Property;
import com.blueline.tool.proxy.tcp.domain.ProxyDefinition;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.traffic.AbstractTrafficShapingHandler;


public class ProxyInitializer extends ChannelInitializer<SocketChannel> {

	static final Logger logger = LoggerFactory.getLogger(ProxyInitializer.class);

	String nettyLogDataLevel=Property.get("logging.level.io.netty",null);

	private final ProxyDefinition proxyDefinition;
	private final AbstractTrafficShapingHandler trafficShapingHandler;

	public ProxyInitializer(ProxyDefinition proxyDefinition, AbstractTrafficShapingHandler trafficShapingHandler) {
		this.proxyDefinition = proxyDefinition;
		this.trafficShapingHandler = trafficShapingHandler;
	}
//	private final SslContext sslCtx;
//  public ProxyInitializer(SslContext sslCtx) {
//      this.sslCtx = sslCtx;
//  }


	@Override
	protected void initChannel(SocketChannel ch) {
		logger.info("C ProxyInitializer  initChannel");
		
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
