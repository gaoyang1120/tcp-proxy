package com.blueline.tool.proxy.tcp.services.impl;

import com.blueline.tool.proxy.tcp.domain.*;
import com.blueline.tool.proxy.tcp.services.NettyProxyServer;
import com.blueline.tool.proxy.tcp.services.ServerService;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.ConcurrentSet;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class NettyServerServiceImpl implements ServerService {

	private final EventLoopGroup bossGroup;
	private final EventLoopGroup workerGroup;
	private Map<String, NettyProxyServer> servers;

	public NettyServerServiceImpl(){
//		this.bossGroup = new NioEventLoopGroup(2);
//		this.workerGroup = new NioEventLoopGroup(16);
		//线程数 这里设置高些
		this.bossGroup = new NioEventLoopGroup(2);
		this.workerGroup = new NioEventLoopGroup(4);
		
		this.servers = new ConcurrentHashMap<String, NettyProxyServer>();
	}


	@Override
	public ProxyDefinition createServer(CreateProxyRequest createProxyRequest){

		ProxyDefinition proxyDefinition = new ProxyDefinition(createProxyRequest);
		if(!checkPortAvailable(proxyDefinition)){
			throw new IllegalStateException("Selected local port already in use");
		}
		servers.put(proxyDefinition.getId(),new TCPNettyProxyServer(proxyDefinition));
		return proxyDefinition;
	}

	@Override
	public void startServer(String id) {
		findServer(id).start(bossGroup,workerGroup);
	}

	@Override
	public void stopServer(String id) {
		findServer(id).stop();
	}

	@Override
	public void configureTraffic(String id, TrafficShaping config) {
		NettyProxyServer server = findServer(id);
		server.getDefinition().getQos().setTrafficShaping(config);
		server.configureTraffic(config);
	}

	@Override
	public void stopDebug(String id) {
		findServer(id).getDefinition().setDebug(false);
	}

	@Override
	public void startDebug(String id) {
		findServer(id).getDefinition().setDebug(true);
	}

	@Override
	public ProxyDefinition delete(String id) {
		NettyProxyServer nettyProxyServer=servers.remove(id);
		nettyProxyServer.stop();
		return nettyProxyServer.getDefinition();
	}

	@Override
	public ProxyDefinition getProxyDefinition(String id) {
		return findServer(id).getDefinition();
	}


	@Override
	public Set<ProxyDefinition> listServers(){
		Set<ProxyDefinition> proxyDefinitionSet=new ConcurrentSet<ProxyDefinition>();
		for (NettyProxyServer proxyServer : servers.values()) {
			proxyDefinitionSet.add(proxyServer.getDefinition());
		}
		return proxyDefinitionSet;

	}

	@Override
	public ConnectionStats getStats(String id) {
		return findServer(id).getStats();
	}


	private NettyProxyServer findServer(String id){
		NettyProxyServer server = servers.get(id);
		if(server == null)
			throw new ServerNotFoundException("Server not found: " + id);
		return server;
	}

	@PreDestroy
	public void shutdown(){
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}

	/**
	 * Check if there's already a proxy defined for this port.
	 * @param proxyDefinition
	 */
	private boolean checkPortAvailable(ProxyDefinition proxyDefinition) {
		boolean portAvailble = true;

		for(Map.Entry<String,NettyProxyServer> entry : servers.entrySet()){
			if(entry.getValue().getDefinition().getLocalPort().equals(proxyDefinition.getLocalPort())){
				portAvailble = false;
				break;
			}
		}
		return portAvailble;
	}
}
