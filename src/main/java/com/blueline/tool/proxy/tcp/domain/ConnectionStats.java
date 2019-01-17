package com.blueline.tool.proxy.tcp.domain;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 状态实体
 * @author Gaoyang
 *
 */
public class ConnectionStats {

	/**
	 * 连接数
	 */
	private AtomicInteger connectionCount = new AtomicInteger(0);
	/**
	 * 发送数据
	 */
	private AtomicLong bytesSent = new AtomicLong(0L);
	/**
	 * 接收数据
	 */
	private AtomicLong bytesReceived = new AtomicLong(0L);

	public ConnectionStats(){}
	public ConnectionStats(long bytesSent, long bytesReceived,int connctionCount){
		this.bytesSent.set(bytesSent);
		this.bytesReceived.set(bytesReceived);
		this.connectionCount.set(connctionCount);
	}

	/**
	 * connectionCount + 1
	 * 连接数加一
	 * @return
	 */
	public int increaseConnectionCount(){
		return connectionCount.incrementAndGet();
	}

	/**
	 * connectionCount - 1
	 * 连接数减一
	 * @return
	 */
	public int decreaseConnectionCount(){
		return connectionCount.decrementAndGet();
	}

	public long appendBytesSent(long delta){
		return bytesSent.getAndAdd(delta);
	}

	public long appendBytesReceived(long delta){
		return bytesReceived.getAndAdd(delta);
	}



	public AtomicLong getBytesReceived() {
		return bytesReceived;
	}

	public Long getBytesSent(){
		return bytesSent.get();
	}

	public Integer getConnectionCount(){
		return connectionCount.get();
	}
}


