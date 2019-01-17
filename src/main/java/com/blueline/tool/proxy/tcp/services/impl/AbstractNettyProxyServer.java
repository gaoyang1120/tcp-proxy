package com.blueline.tool.proxy.tcp.services.impl;


import com.blueline.tool.proxy.tcp.domain.ConnectionStats;
import com.blueline.tool.proxy.tcp.domain.ProxyDefinition;
import com.blueline.tool.proxy.tcp.domain.TrafficShaping;
import com.blueline.tool.proxy.tcp.services.NettyProxyServer;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.traffic.AbstractTrafficShapingHandler;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractNettyProxyServer implements NettyProxyServer {

	protected final ProxyDefinition definition;
	protected AbstractTrafficShapingHandler trafficHandler;
	protected Channel serverChannel;
	protected volatile boolean running = false;
	private Logger logger = LoggerFactory.getLogger(getClass());

	public AbstractNettyProxyServer(ProxyDefinition definition) {
		this.definition = definition;
	}


	protected abstract Channel doStart(EventLoopGroup bossGroup, EventLoopGroup workerGroup);

	@Override
	public void start(EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
		createTrafficHandler(workerGroup);
		this.serverChannel = doStart(bossGroup, workerGroup);
		running = true;
		definition.setActive(true);
	}

	@Override
	public void stop() {
		if(serverChannel != null && running){
			logger.info("Shutting down server: {} on port {}",definition.getId(),definition.getLocalPort());
			definition.setActive(false);
			serverChannel.close().addListener(new GenericFutureListener<Future<? super Void>>() {
				@Override
				public void operationComplete(Future<? super Void> future) throws Exception {
					running = false;
					serverChannel = null;
					logger.info("Server {} shutdown complete",definition.getId());
				}
			});
		}
	}

	public ConnectionStats getStats(){
		if(!running)
			return new ConnectionStats(0,0,0);

		return new ConnectionStats(trafficHandler.trafficCounter().lastWrittenBytes(),trafficHandler.trafficCounter().lastReadBytes(),definition.getConnectionStats().getConnectionCount());
	}

	private void createTrafficHandler(EventLoopGroup workerGroup){
		if(definition.getQos().getTrafficShaping() != null){
			TrafficShaping tf = definition.getQos().getTrafficShaping();
			this.trafficHandler = new GlobalChannelTrafficShapingHandler(workerGroup,tf.getWriteLimit(),tf.getReadLimit(),0,0,tf.getCheckInterval(),tf.getMaxTime());
		}else{
			this.trafficHandler = new GlobalChannelTrafficShapingHandler(workerGroup,0,0,0,0,1000,1000);
		}
	}

	@Override
	public void configureTraffic(TrafficShaping config) {
		this.trafficHandler.configure(config.getWriteLimit(),config.getReadLimit());
	}

	@Override
	public ProxyDefinition getDefinition() {
		return definition;
	}
}
