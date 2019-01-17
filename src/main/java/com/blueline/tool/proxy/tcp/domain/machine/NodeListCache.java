package com.blueline.tool.proxy.tcp.domain.machine;

import java.util.HashMap;
import java.util.Map;

public class NodeListCache {
	/**
	 * 策略相关列表
	 */
	private static Map<String, NodeCache> nodeListCache = new HashMap<String, NodeCache>();
	
	
	public static Map<String, NodeCache> getNodeListCache(){
		return nodeListCache;
	}
	
	public static void addNode(String name) {
		if(nodeListCache.containsKey(name)) {
			nodeListCache.replace(name, new NodeCache());
		}else {
			nodeListCache.put(name, new NodeCache());
		}
	}
}
