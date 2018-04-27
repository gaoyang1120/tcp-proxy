package com.blueline.tool.proxy.tcp.runner;

import com.blueline.tool.proxy.tcp.services.ProxyInfoStorageService;
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

    @Override
    public void run(ApplicationArguments applicationArguments) {
        service.loadProxyInfo();
    }
}
