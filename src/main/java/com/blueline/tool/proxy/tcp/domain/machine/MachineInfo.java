package com.blueline.tool.proxy.tcp.domain.machine;

import java.util.Date;


public class MachineInfo implements Comparable<MachineInfo>{
	private String address;
	//登陆key
	private String loginKey;
	//通道id
	private String channelId;
	
	
	private boolean online;
	//最近在线时间
	private Date lasteTime;
	
	private String lasteTimeStr;
	


	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public Date getLasteTime() {
		return lasteTime;
	}

	public void setLasteTime(Date lasteTime) {
		this.lasteTime = lasteTime;
	}

	public String getLasteTimeStr() {
		return DateUtilMMs.getStrForDate(lasteTime);
	}

	public String getLoginKey() {
		return loginKey;
	}

	public void setLoginKey(String loginKey) {
		this.loginKey = loginKey;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public int compareTo(MachineInfo mac) {
		//根据lasteTime升序排列，降序修改相减顺序即可
		Long l = this.lasteTime.getTime() - mac.getLasteTime().getTime();
		//根据lasteTime降序排列
//		Long l = mac.getLasteTime().getTime() - this.lasteTime.getTime();
		return l.intValue();
	}

	@Override
	public String toString() {
		return "MachineInfo [address=" + address + ", loginKey=" + loginKey + ", channelId=" + channelId + ", online="
				+ online + ", lasteTime=" + lasteTime + ", lasteTimeStr=" + lasteTimeStr + "]";
	}
	
}
