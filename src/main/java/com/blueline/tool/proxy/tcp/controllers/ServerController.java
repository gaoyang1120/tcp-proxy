package com.blueline.tool.proxy.tcp.controllers;

import com.blueline.tool.proxy.tcp.domain.ConnectionStats;
import com.blueline.tool.proxy.tcp.domain.CreateProxyRequest;
import com.blueline.tool.proxy.tcp.domain.ProxyDefinition;
import com.blueline.tool.proxy.tcp.domain.TrafficShaping;
import com.blueline.tool.proxy.tcp.services.ServerService;
import com.blueline.tool.proxy.tcp.services.ProxyInfoStorageService;
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

	Logger logger= LoggerFactory.getLogger(ServerController.class);

	private ServerService service;

	@Autowired
	ProxyInfoStorageService storageService;

	@Autowired
	public ServerController(ServerService service) {
		this.service = service;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity createServer(@RequestBody CreateProxyRequest createProxyRequest){
		ProxyDefinition definition = service.createServer(createProxyRequest);
		storageService.saveProxyInfo(definition);
		ResponseEntity response = new ResponseEntity(definition, HttpStatus.CREATED);
		return response;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
	public ResponseEntity getProxyDefinition(@PathVariable("id") String id){
		return new ResponseEntity(service.getProxyDefinition(id),HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity listServers(){
		return new ResponseEntity(service.listServers(),HttpStatus.OK);
	}
	@RequestMapping(method = RequestMethod.POST, value = "/{id}/status")
	public ResponseEntity toggle(@RequestBody Map<String,Object> status, @PathVariable("id") String id){
		Boolean active = (Boolean) status.get("active");

		if(!active){
			service.stopServer(id);
		}else{
			service.startServer(id);
		}
        storageService.saveProxyInfo(service.getProxyDefinition(id));
		return new ResponseEntity(HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}/stats")
	public ResponseEntity getStats(@PathVariable("id") String id){
		ConnectionStats stats = service.getStats(id);

		return new ResponseEntity(stats,HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/{id}/traffic")
	public ResponseEntity updateTrafficConfiguration(@PathVariable("id") String id, @RequestBody TrafficShaping config){
		service.configureTraffic(id,config);
		ProxyDefinition definition = service.getProxyDefinition(id);
		storageService.saveProxyInfo(definition);
		return new ResponseEntity(definition,HttpStatus.OK);
	}

}
