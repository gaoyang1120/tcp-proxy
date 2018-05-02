package com.blueline.tool.proxy.tcp.controllers;

import com.blueline.tool.proxy.tcp.domain.ConnectionStats;
import com.blueline.tool.proxy.tcp.domain.CreateProxyRequest;
import com.blueline.tool.proxy.tcp.domain.ProxyDefinition;
import com.blueline.tool.proxy.tcp.services.ProxyInfoStorageService;
import com.blueline.tool.proxy.tcp.services.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping(value = "/servers")
public class ServerController {

    Logger logger = LoggerFactory.getLogger(ServerController.class);
    Logger configLogger = LoggerFactory.getLogger("Config");

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
            configLogger.info("{} - {}","CREATED",createProxyRequest);
        } catch (Exception e) {
            response = new ResponseEntity(createProxyRequest, HttpStatus.UNPROCESSABLE_ENTITY);
            logger.warn("{}:{}", e.getMessage(), createProxyRequest);
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    public ResponseEntity getProxyDefinition(@PathVariable("id") String id) {
        return new ResponseEntity(service.getProxyDefinition(id), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity listServers() {
        return new ResponseEntity(service.listServers(), HttpStatus.OK);
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
        configLogger.info("{} - {}","UPDATE",service.getProxyDefinition(id));
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
        configLogger.info("{} - {}","UPDATE",service.getProxyDefinition(id));
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
        configLogger.info("{} - {}","DELETE",proxyDefinition);
        return new ResponseEntity(HttpStatus.OK);
    }
}
