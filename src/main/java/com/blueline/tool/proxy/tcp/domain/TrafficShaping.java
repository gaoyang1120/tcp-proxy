package com.blueline.tool.proxy.tcp.domain;


public class TrafficShaping {
	private long writeLimit;
	private long readLimit;
	private long checkInterval=DEFAULT_CHECK_INTERVAL;
	private long maxTime=DEFAULT_MAX_TIME;

	public static final long DEFAULT_CHECK_INTERVAL = 1000l;
	public static final long DEFAULT_MAX_TIME = 1000l;

	public TrafficShaping(){
		this(0,0, DEFAULT_CHECK_INTERVAL,DEFAULT_MAX_TIME);
	}
	public TrafficShaping(long writeLimit, long readLimit) {
		this(writeLimit,readLimit, DEFAULT_CHECK_INTERVAL,DEFAULT_MAX_TIME);
	}

	public TrafficShaping(long writeLimit, long readLimit, long checkInterval, long maxTime) {
		this.writeLimit = writeLimit;
		this.readLimit = readLimit;
		this.checkInterval = checkInterval;
		this.maxTime = maxTime;
	}

	public long getWriteLimit() {
		return writeLimit;
	}

	public void setWriteLimit(long writeLimit) {
		this.writeLimit = writeLimit;
	}

	public long getReadLimit() {
		return readLimit;
	}

	public void setReadLimit(long readLimit) {
		this.readLimit = readLimit;
	}

	public long getCheckInterval() {
		return checkInterval;
	}

	public void setCheckInterval(long checkInterval) {
		this.checkInterval = checkInterval;
	}

	public long getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(long maxTime) {
		this.maxTime = maxTime;
	}
}
