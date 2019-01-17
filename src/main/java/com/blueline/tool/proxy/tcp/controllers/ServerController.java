package com.blueline.tool.proxy.tcp.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.blueline.tool.proxy.tcp.domain.ConnectionStats;
import com.blueline.tool.proxy.tcp.domain.CreateProxyRequest;
import com.blueline.tool.proxy.tcp.domain.ProxyDefinition;
import com.blueline.tool.proxy.tcp.domain.machine.NodeCache;
import com.blueline.tool.proxy.tcp.domain.machine.NodeListCache;
import com.blueline.tool.proxy.tcp.services.ProxyInfoStorageService;
import com.blueline.tool.proxy.tcp.services.ServerService;


@RestController
@RequestMapping(value = "/servers")
public class ServerController {

    Logger logger = LoggerFactory.getLogger(ServerController.class);
//    Logger configLogger = LoggerFactory.getLogger("Config");

    private ServerService service;

    @Autowired
    ProxyInfoStorageService storageService;

    @Autowired
    public ServerController(ServerService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createServer(@RequestBody CreateProxyRequest createProxyRequest) {
        ResponseEntity response;
        try {
            ProxyDefinition definition = service.createServer(createProxyRequest);
            storageService.saveProxyInfo(definition);
            response = new ResponseEntity(definition, HttpStatus.CREATED);
            logger.info("{} - {}","CREATED",createProxyRequest);
            NodeListCache.addNode(definition.getId());
        } catch (Exception e) {
            response = new ResponseEntity(createProxyRequest, HttpStatus.UNPROCESSABLE_ENTITY);
            logger.warn("{}:{}", e.getMessage(), createProxyRequest);
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    public ResponseEntity getProxyDefinition(@PathVariable("id") String id) {
    	logger.info("获取单个策略----------需要找到机器列表");
    	ProxyDefinition proxyDefinition = service.getProxyDefinition(id);
    	logger.info("info:"+ proxyDefinition.toString());
        return new ResponseEntity(proxyDefinition, HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/workers", produces = "application/json")
    public ResponseEntity getWorkers(@PathVariable("id") String id) {
    	logger.info("获取----------------------需要找到机器列表");
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("total", NodeListCache.getNodeListCache().get(id).total);
    	map.put("offline", NodeListCache.getNodeListCache().get(id).offline);
    	map.put("online", NodeListCache.getNodeListCache().get(id).online);
    	map.put("ChannelSize", NodeListCache.getNodeListCache().get(id).getActiveChannelCache().size());
    	map.put("machines", NodeListCache.getNodeListCache().get(id).getRegesterMachineIpCache());
    	map.put("offlineMachines", NodeListCache.getNodeListCache().get(id).getOfflineMachineIpCache());
        return new ResponseEntity(map, HttpStatus.OK);
    }
    

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity listServers() {
    	logger.info("获取策略列表");
    	Set<ProxyDefinition> listServers = service.listServers();
    	for(ProxyDefinition p:listServers) {
    		logger.info("info:"+ p.toString());
    	}
        return new ResponseEntity(listServers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{id}/status")
    public ResponseEntity toggle(@RequestBody Map<String, Object> status, @PathVariable("id") String id) {
        Boolean active = (Boolean) status.get("active");

        if (!active) {
            service.stopServer(id);
        } else {
            service.startServer(id);
        }

        storageService.saveProxyInfo(service.getProxyDefinition(id));
        logger.info("{} - {}","UPDATE",service.getProxyDefinition(id));
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{id}/debug")
    public ResponseEntity debug(@RequestBody Map<String, Object> status, @PathVariable("id") String id) {

        Boolean debug = (Boolean) status.get("debug");

        if (!debug) {
            service.stopDebug(id);
        } else {
            service.startDebug(id);
        }

        storageService.saveProxyInfo(service.getProxyDefinition(id));
        logger.info("{} - {}","UPDATE",service.getProxyDefinition(id));
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/stats")
    public ResponseEntity getStats(@PathVariable("id") String id) {
        ConnectionStats stats = service.getStats(id);

        return new ResponseEntity(stats, HttpStatus.OK);
    }
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity deleteServer(@PathVariable("id") String id) {
        ProxyDefinition proxyDefinition=service.delete(id);
        proxyDefinition.setDelete(true);
        storageService.deleteProxyInfo(proxyDefinition);
        logger.info("{} - {}","DELETE",proxyDefinition);
        return new ResponseEntity(HttpStatus.OK);
    }
}
