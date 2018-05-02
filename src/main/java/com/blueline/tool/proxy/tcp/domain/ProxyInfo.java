package com.blueline.tool.proxy.tcp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProxyInfo{
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private String alias;
    private Integer remotePort;
    private String remoteHost;
    private boolean debug;
    private Integer localPort;
    private Qos qos;
    private boolean active;

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    private boolean delete;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    public Integer getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(Integer remotePort) {
        this.remotePort = remotePort;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public Integer getLocalPort() {
        return localPort;
    }

    public void setLocalPort(Integer localPort) {
        this.localPort = localPort;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Qos getQos() {
        return qos;
    }

    public void setQos(Qos qos) {
        this.qos = qos;
    }

    @Override
    public String toString() {
        return "ProxyInfo{" +
                "id='" + id + '\'' +
                ", alias='" + alias + '\'' +
                ", localPort=" + localPort +
                ", remotePort=" + remotePort +
                ", remoteHost='" + remoteHost + '\'' +
                ", debug=" + debug +
                ", active=" + active +
                '}';
    }
}
