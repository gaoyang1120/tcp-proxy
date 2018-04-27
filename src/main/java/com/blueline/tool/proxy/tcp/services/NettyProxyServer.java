package com.blueline.tool.proxy.tcp.services;


import com.blueline.tool.proxy.tcp.domain.ConnectionStats;
import com.blueline.tool.proxy.tcp.domain.ProxyDefinition;
import com.blueline.tool.proxy.tcp.domain.TrafficShaping;
import io.netty.channel.EventLoopGroup;


public interface NettyProxyServer {
	ProxyDefinition getDefinition();
	ConnectionStats getStats();
	void start(EventLoopGroup bossGroup, EventLoopGroup workerGroup);
	void stop();
	void configureTraffic(TrafficShaping config);
}
