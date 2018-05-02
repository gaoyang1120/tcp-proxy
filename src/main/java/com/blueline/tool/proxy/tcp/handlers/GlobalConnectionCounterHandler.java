package com.blueline.tool.proxy.tcp.handlers;

import com.blueline.tool.proxy.tcp.domain.ProxyDefinition;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GlobalConnectionCounterHandler extends ChannelInboundHandlerAdapter{

	static final Logger logger=LoggerFactory.getLogger(GlobalConnectionCounterHandler.class);

	ProxyDefinition proxyDefinition;

	public GlobalConnectionCounterHandler(ProxyDefinition proxyDefinition) {
		this.proxyDefinition=proxyDefinition;
	}


}
