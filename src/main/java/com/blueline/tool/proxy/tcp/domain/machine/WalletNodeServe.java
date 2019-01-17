package com.blueline.tool.proxy.tcp.domain.machine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
public class WalletNodeServe {
	Logger logger = LoggerFactory.getLogger(WalletNodeServe.class);

	/**
	 * 获取当前实时的 情况
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Throwable
	 */
	@Scheduled(cron = "0 0/1 * * * ? ")
	public void getIpListaction() throws Throwable {
		Set<String> pros = NodeListCache.getNodeListCache().keySet();
		Iterator<String> iteratorPro = pros.iterator();
		while (iteratorPro.hasNext()) {
			String key = iteratorPro.next();
			logger.info("循环查询是否掉线 task.........................."+key);
			calc(key);
		}
	}

	/**
	 * 机器在线列表 和 激活通道列表做个比对
	 * @param keyTmp
	 */
	private void calc(String keyTmp) {
		NodeCache node = NodeListCache.getNodeListCache().get(keyTmp);
		// 对最新数据进行分析
		Map<String, MachineInfo> RegesterMachineIpCache = node.getRegesterMachineIpCache();
		logger.info("当前注册机器信息"+RegesterMachineIpCache.size());
		List<String> activeChannelCache = node.getActiveChannelCache();
		logger.info("当前激活通道信息"+activeChannelCache.size());
		
		logger.info("检查机器掉线信息--------------------------------------------------");
		List<MachineInfo> listOFF = new ArrayList<MachineInfo>();
		// 检查掉线机器
		int offline = 0;
		int online = 0;
		int total = 0;
		Set<String> keySet = RegesterMachineIpCache.keySet();
		Iterator<String> iterator = keySet.iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			MachineInfo machineIp = RegesterMachineIpCache.get(key);
			String channelId = machineIp.getChannelId();
			if(activeChannelCache.contains(channelId)) {
				//机器在线 更新在线时间
				machineIp.setLasteTime(new Date());
				node.putRegesterMac(machineIp.getLoginKey(), machineIp);
			}else {
				//离线
				Date lasteTime = machineIp.getLasteTime();
				long xx = System.currentTimeMillis() - lasteTime.getTime();
				if (xx / 1000 > 60 * 5 || !machineIp.isOnline()) {
					// 掉线
					machineIp.setOnline(false);
					node.putRegesterMac(machineIp.getLoginKey(), machineIp);
					offline += 1;
					//放到掉线列表中去
					listOFF.add(machineIp);
				}
			}
		}

		total = keySet.size();
		online = total - offline;
		node.total = total;
		node.online = online;
		node.offline = offline;
		
		node.cleanOfflineMachineCache();
		node.addAllOfflineMachineCache(listOFF);
		logger.info("离线机器数量:"+node.getOfflineMachineIpCache().size());
		// 掉线机器很多 导致很多无用数据 开始清理，从最末端开始清理

//		List<MachineInfo> listOFF = new ArrayList<MachineInfo>();
//		Map<String, Integer> loginKeyList = new HashMap<String, Integer>();
//		logger.info("掉线机器很多 导致很多无用数据 开始清理，从最末端开始清理");
//		Map<String, MachineInfo> cache = node.getMachineIpCache();
//		Collection<MachineInfo> values = cache.values();
//		for (MachineInfo m : values) {
//			if (!m.isOnline()) {
//				// 掉线机器
//				listOFF.add(m);
//				String loginKey = m.getLoginKey();
//				if (loginKeyList.containsKey(m.getLoginKey())) {
//					Integer integer = loginKeyList.get(loginKey) == null ? 0 : loginKeyList.get(loginKey);
//					loginKeyList.put(loginKey, integer + 1);
//				}
//			}
//		}
//		
//		Collections.sort(listOFF);
		// 同一机器名删除重复的数据

//		for (MachineInfo mo : listOFF) {
//			String loginKey = mo.getLoginKey();
//			Integer integer = loginKeyList.get(loginKey);
//			if (integer != null && integer > 1) {
//				listOFF.remove(mo);
//				loginKeyList.put(loginKey, integer - 1);
//			}
//		}
//		node.getOfflineMachineIpCache().clear();
//		node.getOfflineMachineIpCache().addAll(listOFF);
		
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		//清理在线列表
//		NodeCache Tmpnode = NodeListCache.getNodeListCache().get(keyTmp);
//		Map<String, MachineInfo> machineIpCacheTmp = Tmpnode.getMachineIpCache();
//		Set<String> keySetTmp = machineIpCacheTmp.keySet();
//		Iterator<String> iteratorClean = keySetTmp.iterator();
//		while (iteratorClean.hasNext()) {
//			String key = iteratorClean.next();
//			MachineInfo machineIp = machineIpCacheTmp.get(key);
//			if(!machineIp.isOnline()) {
//				Tmpnode.getMachineIpCache().remove(key);
//			}
//		}
	}

}
