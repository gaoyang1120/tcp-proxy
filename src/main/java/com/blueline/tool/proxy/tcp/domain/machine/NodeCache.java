package com.blueline.tool.proxy.tcp.domain.machine;

import java.net.SocketAddress;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import io.netty.channel.ChannelId;

/**
 * 钱包节点相关信息
 * @author Gaoyang
 *
 */
public class NodeCache {
	/**
	 * 所有注册的机器机器列表
	 */
	private Map<String, MachineInfo> regesterMachineCache = Collections.synchronizedMap(new HashMap<String, MachineInfo>());
	/**
	 * 所有激活的通道
	 */
	private List<String> activeChannelCache = new CopyOnWriteArrayList<String>();
	
	/**
	 * 掉线机器列表
	 */
	private  List<MachineInfo> machineOfflineCache = new CopyOnWriteArrayList<MachineInfo>();
	
	public  int total = 0;
	public  int online = 0;
	public  int offline = 0;
	/**
	 * 获取注册机器信息
	 * @return
	 */
	public  Map<String, MachineInfo> getRegesterMachineIpCache(){
		return regesterMachineCache;
	}
	/**
	 * 获取离线机器
	 * @return
	 */
	public  List<MachineInfo> getOfflineMachineIpCache(){
		return machineOfflineCache;
	}
	/**
	 * 清理离线机器
	 */
	public void cleanOfflineMachineCache() {
		machineOfflineCache.clear();
	}
	public void addAllOfflineMachineCache(List<MachineInfo> list) {
		machineOfflineCache.addAll(list);
	}
	
	/**
	 * 获取激活通道信息 
	 * @return
	 */
	public  List<String> getActiveChannelCache(){
		return activeChannelCache;
	}
	
	/**
	 * 存放数据regesterMachineCache
	 * @param key
	 * @param mac
	 */
	public  void putRegesterMac(String key, MachineInfo mac) {
		if(regesterMachineCache.containsKey(key)) {
			//存在cache里 替换
			regesterMachineCache.replace(key, mac);
		}else {
			regesterMachineCache.put(key, mac);
		}
	}
	/**
	 * 激活通道
	 * @param channel
	 */
	public  void putActiveChn(String channel) {
		if(activeChannelCache.contains(channel)) {
			//先不处理
		}else {
			activeChannelCache.add(channel);
		}
	}
	/**
	 * 断开通道
	 * @param channel
	 */
	public  void delActiveChn(String channel) {
		activeChannelCache.remove(channel);
	}
	
	
	/**
	 * 存放method登陆数据 regesterMachineCache
	 * @param channelId
	 * @param remoteAddress
	 * @param loginKey null is defalut
	 */
	public  void putLogin(ChannelId channelId, SocketAddress remoteAddress, String loginKey) {
		if(loginKey == null) {
			loginKey = Long.toString(System.currentTimeMillis());
		}
		//Cache信息
		MachineInfo mac = new MachineInfo();
		mac.setChannelId(channelId.asShortText());
		mac.setLasteTime(new Date());
		mac.setLoginKey(loginKey);
		mac.setOnline(true);
		mac.setAddress(remoteAddress.toString());
		putRegesterMac(loginKey, mac);
	}
	/**
	 * 设置掉线
	 * activeChannelCache
	 * @param channelId
	 */
	public  void setOffline(ChannelId channelId) {
		String channelKey = channelId.asShortText();
//		Map<String, MachineInfo> machineIpCache = getRegesterMachineIpCache();
//		MachineInfo machineInfo = machineIpCache.get(channelKey);
//		machineInfo.setOnline(false);
//		machineInfo.setLasteTime(new Date());
//		putMac(channelKey, machineInfo);
		delActiveChn(channelKey);
	}
	
	/**
	 * 心跳
	 * @param channelId
	 * @param remoteAddress
	 */
	public void ping(ChannelId channelId, SocketAddress remoteAddress) {
		String channelKey = channelId.asShortText();
//		Map<String, MachineInfo> machineIpCache = getRegesterMachineIpCache();
//		MachineInfo machineInfo = machineIpCache.get(channelKey);
//		//更新在线时间
//		machineInfo.setLasteTime(new Date());
//		machineInfo.setOnline(true);
//		machineInfo.setAddress(remoteAddress.toString());
//		putMac(channelKey, machineInfo);
		putActiveChn(channelKey);
	}
	
}
