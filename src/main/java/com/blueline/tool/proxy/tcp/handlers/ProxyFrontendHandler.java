package com.blueline.tool.proxy.tcp.handlers;


import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.blueline.tool.proxy.tcp.domain.ProxyDefinition;
import com.blueline.tool.proxy.tcp.domain.machine.NodeListCache;
import com.blueline.tool.proxy.tcp.handlers.utils.Formart;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;

/**
 * 前端Handle
 * @author Gaoyang
 *
 */
public class ProxyFrontendHandler extends ChannelInboundHandlerAdapter {

    static final Logger logger = LoggerFactory.getLogger(ProxyFrontendHandler.class);

    private ProxyDefinition proxyDefinition;
    private Channel outboundChannel;

    public Channel getInboundChannel() {
        return inboundChannel;
    }

    private Channel inboundChannel;

    private LinkedList<Object> inboundMsgBuffer = new LinkedList<Object> ();

    private ConnectionStatus connectStatus = ConnectionStatus.init;

    public void setAutoRead(boolean b) {
        inboundChannel.config().setAutoRead(b);
    }

    enum ConnectionStatus{
        init,
        outBoundChnnlConnecting,      //inbound connected and outbound connecting
        outBoundChnnlReady,           //inbound connected and outbound connected
        closing                       //closing inbound and outbound connection
    }


    public ProxyFrontendHandler(ProxyDefinition proxyDefinition) {
        this.proxyDefinition = proxyDefinition;
    }

    /**
     * 激活远端通道
     * 
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    	//--------------
//    	File certChainFile = new File(".\\ssl\\server.crt");
//        File keyFile = new File(".\\ssl\\pkcs8_server.key");
//        File rootFile = new File(".\\ssl\\ca.crt");
//    	try {
//			SslContext sslCtx = SslContextBuilder.forServer(certChainFile, keyFile).trustManager(rootFile).clientAuth(ClientAuth.REQUIRE).build();
//		} catch (SSLException e) {
//			e.printStackTrace();
//		}
    	//------------
    	//增加连接数
        proxyDefinition.getConnectionStats().increaseConnectionCount();
//        logger.info("channelActive connection count : {}", proxyDefinition.getConnectionStats().getConnectionCount());
        logger.info("A-1.前端连接信息："+ ctx.toString());
        inboundChannel = ctx.channel();
        // Start the connection attempt.
        Bootstrap client = new Bootstrap();
        client.group(inboundChannel.eventLoop())
                .channel(ctx.channel().getClass())
                .handler(new ProxyBackendHandler(this, proxyDefinition))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30*1000)
//                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.AUTO_READ, false);


        // 绑定端口，开始接收进来的连接 TODO
        //这里使用remoteAddress 能解决阿里云 公网ip和私有ip映射关系导致 本地地址处理问题
        if("localhost".equals(proxyDefinition.getRemoteHost())) {
        	client = client.remoteAddress(new InetSocketAddress(proxyDefinition.getRemotePort()));
        }else {
        	client = client.remoteAddress(new InetSocketAddress(proxyDefinition.getRemoteHost(), proxyDefinition.getRemotePort()));
        }
        //connect只能用于外部网络 内部含有 私有ip会链接报错
        ChannelFuture f = client.connect();
        //设置状态
        connectStatus = ConnectionStatus.outBoundChnnlConnecting;
        outboundChannel = f.channel();

        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (!future.isSuccess()) {
                    // Close the connection if the connection attempt has failed.
                    future.cause().printStackTrace();
                    close();
                }else {
                	logger.info("A-3.前端成功建立连接，接受请求数据："+future.toString());
                	//分析数据
                }
            }
        });
    }
    
    
    
    public void channelActiveBak(ChannelHandlerContext ctx) {
    	//增加连接数
        proxyDefinition.getConnectionStats().increaseConnectionCount();
//        logger.info("channelActive connection count : {}", proxyDefinition.getConnectionStats().getConnectionCount());
        logger.info("前端连接信息："+ ctx.toString());
        inboundChannel = ctx.channel();
        // Start the connection attempt.
        Bootstrap b = new Bootstrap();
        b.group(inboundChannel.eventLoop())
                .channel(ctx.channel().getClass())
                .handler(new ProxyBackendHandler(this, proxyDefinition))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30*1000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.AUTO_READ, false);


        // 绑定端口，开始接收进来的连接
        ChannelFuture f = b.connect(proxyDefinition.getRemoteHost(), proxyDefinition.getRemotePort());
        //设置状态
        connectStatus = ConnectionStatus.outBoundChnnlConnecting;
        outboundChannel = f.channel();

        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (!future.isSuccess()) {
                    // Close the connection if the connection attempt has failed.
                    future.cause().printStackTrace();
                    close();
                }else {
                	logger.info("前端成功建立连接，接受请求数据："+future.toString());
                	//分析数据
                }
            }
        });
    }

    /**
     * 读取通道数据
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
    	SocketAddress remoteAddress = ctx.channel().remoteAddress();
		SocketAddress localAddress = ctx.channel().localAddress();
		ChannelId channelId = ctx.channel().id();
//    	logger.info("A-2. 通道数据:"+ ctx.channel().id().toString() + " connectStatus="+connectStatus);
        switch(connectStatus){
            case outBoundChnnlReady:
//            	String convertByteBufToString = Formart.convertByteBufToString(msg);
//            	logger.info("A-2.1尝试解析通道数据:"+ convertByteBufToString);
            	
            	//通道成功后心跳数据
//            	logger.info("A-2.1 通道ping ping ping");
            	NodeListCache.getNodeListCache().get(proxyDefinition.getId()).ping(channelId, remoteAddress);
            	//写数据
                outboundChannel.writeAndFlush(msg);
                break;
            case closing:
                release(msg);
                break;
            case init:
                logger.error("Bad connectStatus.");
                close();
                break;
            case outBoundChnnlConnecting:
            default:
                inboundMsgBuffer.add(msg);
                //通道连接获取登陆数据
				//判断包是否太长了 是否有分包 TODO
                String jsonStr = Formart.convertByteBufToString(msg);
                //{"id":"0","jsonrpc":"2.0","method":"login","params":{"agent":"grin-miner","login":"key.worker_01","pass":""}}\n
//                logger.info("A-2.2解析通道连接数据:"+ jsonStr);
//                String[] split = jsonStr.split("\n");
                String[] split = jsonStr.split("\\s+");
                for(String json:split) {
                	try {
                		JSONObject map2JsonStr = JSONObject.parseObject(json);
                		String method = map2JsonStr.getString("method");
                		logger.info("A-2.3 method========================"+ method);
                		if("login".equals(method)) {
                			//登陆信息，拿去登陆key
                			JSONObject jsonObject = map2JsonStr.getJSONObject("params");
                			String loginKey = jsonObject.getString("login");
                			boolean open = ctx.channel().isOpen();
                			boolean active = ctx.channel().isActive();
                			logger.info("A-2.4 remoteAddress:["+remoteAddress+"] --> "
                					
							+ "通过通道 channelId:["+channelId.asShortText()+", open:"+open+", active:"+active+" ] -->"
							
							+ " 连接到local["+localAddress+"] "
							+ "登陆信息loginKey:"+ loginKey);
                			//Cache信息
                			NodeListCache.getNodeListCache().get(proxyDefinition.getId()).putLogin(channelId, remoteAddress, loginKey);
                		}else if("getjobtemplate".equals(method)) {
                			//没有带登陆信息 不记录
                			logger.info("A-2.5 remoteAddress:["+remoteAddress+"] --> "
							+ "通过通道 channelId:["+channelId.asShortText()+" ] -->"
							+ " 连接到local["+localAddress+"] "
							);
//                			NodeListCache.getNodeListCache().get(proxyDefinition.getId()).putLogin(channelId, remoteAddress, null);
                		}
                	} catch (Throwable e) {
                		logger.error("获取数据["+jsonStr+"]错误", e);
                		e.printStackTrace();
                	}
                }
        }
    }
    
    

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
    	ChannelId channelId = ctx.channel().id();
    	logger.info("A3 channelInactive 通道["+channelId.asShortText()+"]问题 需要关闭");
    	//删除缓存数据
    	NodeListCache.getNodeListCache().get(proxyDefinition.getId()).setOffline(channelId);
//    	NodeListCache.getNodeListCache().get(proxyDefinition.getId()).getMachineIpCache().remove(channelId.asShortText());
    	
        close();
        proxyDefinition.getConnectionStats().decreaseConnectionCount();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	//远程连接断开
    	logger.info("前端通道断开-------------");
        logger.warn("FrontendHandler connection is abnormally disconnected {}:{}",proxyDefinition.getAlias(),cause.getMessage());
        logger.debug("Stack information:",cause.getMessage());
        close();
    }

    public void close() {
        connectStatus = ConnectionStatus.closing;
        for(Object obj : inboundMsgBuffer){
            release(obj);
        }
        inboundMsgBuffer.clear();
        closeOnFlush(inboundChannel);
        closeOnFlush(outboundChannel);
    }

    private void release(Object obj){
        if(obj instanceof ByteBuf){
            ((ByteBuf)obj).release();
        }
    }


    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    public void outBoundChannelReady() {
        inboundChannel.config().setAutoRead(true);
        outboundChannel.config().setAutoRead(true);
        connectStatus = ConnectionStatus.outBoundChnnlReady;
        for(Object obj : inboundMsgBuffer){
            outboundChannel.writeAndFlush(obj);
        }
        inboundMsgBuffer.clear();
    }


}
