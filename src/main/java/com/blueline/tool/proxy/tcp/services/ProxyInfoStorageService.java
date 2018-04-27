package com.blueline.tool.proxy.tcp.services;

import com.blueline.tool.proxy.tcp.domain.ProxyInfo;

public interface ProxyInfoStorageService {
    public void loadProxyInfo();
    public void saveProxyInfo(ProxyInfo proxyInfo);

}
