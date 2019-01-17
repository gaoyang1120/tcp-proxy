package com.blueline.tool.proxy.tcp.runner;

import com.blueline.tool.proxy.tcp.domain.ProxyDefinition;
import com.blueline.tool.proxy.tcp.domain.machine.NodeListCache;
import com.blueline.tool.proxy.tcp.services.ProxyInfoStorageService;
import com.blueline.tool.proxy.tcp.services.ServerService;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 1)
public class LoadProxyServersRunner implements ApplicationRunner {

    @Autowired
    ProxyInfoStorageService service;
    @Autowired
    private ServerService config;
    
    @Override
    public void run(ApplicationArguments applicationArguments) {
        //加载配置文件
    	service.loadProxyInfo();
        
        //获取数据
    	Set<ProxyDefinition> listServers = config.listServers();
    	for(ProxyDefinition p:listServers) {
    		NodeListCache.addNode(p.getId());
    	}
        
    }
}
