package com.blueline.tool.proxy.tcp.services;

import com.blueline.tool.proxy.tcp.domain.CreateProxyRequest;
import com.blueline.tool.proxy.tcp.domain.ProxyDefinition;
import com.blueline.tool.proxy.tcp.domain.ProxyInfo;
import com.blueline.tool.proxy.tcp.runner.LoadProxyServersRunner;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class ProxyInfoStorageServiceJsonFileImpl implements ProxyInfoStorageService {

    private static final Logger logger= LoggerFactory.getLogger(LoadProxyServersRunner.class);


    @Value("${proxy.tcp.autoLoadDataPath:./proxy/}")
    String autoLoadDataPath;

    @Autowired
    private ServerService service;

    @Override
    public void loadProxyInfo() {
        File autoLoadData = new File(autoLoadDataPath);
        if(autoLoadData.exists()){
            if(autoLoadData.isFile()){
                logger.warn("The specified proxy information directory is not a folder:{}",autoLoadDataPath);
            }else{
                ObjectMapper objectMapper=new ObjectMapper();
                for (File file : autoLoadData.listFiles()) {
                    if(file.isFile()){
                        try {
                            ProxyDefinition proxyDefinition=service.createServer(objectMapper.readValue(file,CreateProxyRequest.class));
                            if(proxyDefinition.isActive()) {
                                service.startServer(proxyDefinition.getId());
                            }else{
                                service.stopServer(proxyDefinition.getId());
                            }
                            logger.info("Loaded proxy information successfully {}@{}->{}:{}",proxyDefinition.getAlias(),proxyDefinition.getLocalPort(),proxyDefinition.getRemoteHost(),proxyDefinition.getRemotePort());
                        } catch (IOException e) {
                            logger.warn("Not a valid proxy information file : {}",file);
                        }

                    }
                }
            }
        }else{
            try {
                logger.warn("There is no proxy information to load :{}",autoLoadData.getCanonicalPath());
            } catch (IOException e) {
                logger.warn("The specified proxy information directory does not exist :{}",autoLoadDataPath);
            }
        }
    }

    @Override
    public void saveProxyInfo(ProxyInfo proxyInfo) {
        File autoLoadData = new File(autoLoadDataPath);
        autoLoadData.mkdirs();
        try {
            File proxyInfoFile=new File(autoLoadData.getCanonicalPath()+"/"+proxyInfo.getLocalPort());
            if(proxyInfoFile.exists()){
                proxyInfoFile.delete();
            }
            ObjectMapper objectMapper=new ObjectMapper();
            objectMapper.writeValue(proxyInfoFile,proxyInfo);
        } catch (IOException e) {
            logger.warn("The specified proxy information directory does not exist :{}",autoLoadDataPath);
        }
    }
}
