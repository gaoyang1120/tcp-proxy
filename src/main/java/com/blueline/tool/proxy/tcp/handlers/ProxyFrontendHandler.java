package com.blueline.tool.proxy.tcp.handlers;


import com.blueline.tool.proxy.tcp.domain.ProxyDefinition;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;


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

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        proxyDefinition.getConnectionStats().increaseConnectionCount();
//        logger.info("channelActive connection count : {}", proxyDefinition.getConnectionStats().getConnectionCount());

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



        ChannelFuture f = b.connect(proxyDefinition.getRemoteHost(), proxyDefinition.getRemotePort());
        connectStatus = ConnectionStatus.outBoundChnnlConnecting;
        outboundChannel = f.channel();

        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (!future.isSuccess()) {
                    // Close the connection if the connection attempt has failed.
                    future.cause().printStackTrace();
                    close();
                }
            }
        });
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {


        switch(connectStatus){
            case outBoundChnnlReady:
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
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {

        close();
        proxyDefinition.getConnectionStats().decreaseConnectionCount();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
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
