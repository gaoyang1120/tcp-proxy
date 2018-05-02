package com.blueline.tool.proxy.tcp.services;

import com.blueline.tool.proxy.tcp.domain.ConnectionStats;
import com.blueline.tool.proxy.tcp.domain.CreateProxyRequest;
import com.blueline.tool.proxy.tcp.domain.ProxyDefinition;
import com.blueline.tool.proxy.tcp.domain.TrafficShaping;

import java.util.Set;




public interface ServerService {
	ProxyDefinition createServer(CreateProxyRequest createProxyRequest);
	void startServer(String id);
	void stopServer(String id);
	ProxyDefinition getProxyDefinition(String id);
	Set<ProxyDefinition> listServers();
	ConnectionStats getStats(String id);
	void configureTraffic(String id, TrafficShaping config);

    void stopDebug(String id);

	void startDebug(String id);

	ProxyDefinition delete(String id);
}
