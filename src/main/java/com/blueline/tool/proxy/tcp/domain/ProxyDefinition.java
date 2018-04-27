package com.blueline.tool.proxy.tcp.domain;

import java.util.UUID;


public class ProxyDefinition extends ProxyInfo {
    //	private String alias;
    private final long startedTime;
//    private final String id;
    //	private Integer remotePort;
//	private String remoteHost;
//	private boolean debug;
//	private Integer localPort;
//	private boolean active = false;
    private ConnectionStats connectionStats;

    public ProxyDefinition(CreateProxyRequest request) {
        if (request.getId() != null&&!request.getId().isEmpty()) {
            setId(request.getId());
        }else{
            setId(UUID.randomUUID().toString());
        }

        startedTime = System.currentTimeMillis();
        connectionStats = new ConnectionStats();
        setQos(request.getQos() == null ? new Qos() : request.getQos());
        setRemoteHost(request.getRemoteHost());
        setRemotePort(request.getRemotePort());
        setLocalPort(request.getLocalPort());
        setAlias(request.getAlias());
        setActive(request.isActive());

    }

    public ConnectionStats getConnectionStats() {
        return connectionStats;
    }

    public void setConnectionStats(ConnectionStats connectionStats) {
        this.connectionStats = connectionStats;
    }

    public long getStartedTime() {
        return startedTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProxyDefinition that = (ProxyDefinition) o;

        return getId().equals(that.getId());

    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
