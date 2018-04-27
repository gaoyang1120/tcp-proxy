package com.blueline.tool.proxy.tcp.handlers;


import com.blueline.tool.proxy.tcp.domain.ProxyDefinition;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProxyFrontendHandler extends ChannelInboundHandlerAdapter {

    static final Logger logger = LoggerFactory.getLogger(ProxyFrontendHandler.class);

    private ProxyDefinition proxyDefinition;
    private volatile Channel outboundChannel;

    public ProxyFrontendHandler(ProxyDefinition proxyDefinition) {
        this.proxyDefinition = proxyDefinition;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        proxyDefinition.getConnectionStats().increaseConnectionCount();
//        logger.info("channelActive connection count : {}", proxyDefinition.getConnectionStats().getConnectionCount());

        final Channel inboundChannel = ctx.channel();
        // Start the connection attempt.
        Bootstrap b = new Bootstrap();
        b.group(inboundChannel.eventLoop())
                .channel(ctx.channel().getClass())
                .handler(new ProxyBackendHandler(inboundChannel, proxyDefinition))
                .option(ChannelOption.AUTO_READ, false);
        ChannelFuture f = b.connect(proxyDefinition.getRemoteHost(), proxyDefinition.getRemotePort());
        outboundChannel = f.channel();
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    // connection complete start to read first data
                    inboundChannel.read();
                } else {
                    // Close the connection if the connection attempt has failed.
                    future.cause().printStackTrace();
                    inboundChannel.close();
                }
            }
        });
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
//        outboundChannel.isOpen()
        if (outboundChannel.isOpen()) {
            if (ByteBuf.class.isAssignableFrom(msg.getClass())) {
                proxyDefinition.getConnectionStats().appendBytesSent(((ByteBuf) msg).capacity());
            }
            outboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    if (future.isSuccess()) {
                        // was able to flush out data, start to read the next chunk
                        ctx.channel().read();
                    } else {
                        future.cause().printStackTrace();
                        future.channel().close();
                    }
                }
            });
        } else {

        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (outboundChannel != null) {
            closeOnFlush(outboundChannel);
        }
        proxyDefinition.getConnectionStats().decreaseConnectionCount();
//        logger.info("channelInactive connection count : {}", proxyDefinition.getConnectionStats().getConnectionCount());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.warn(proxyDefinition.getAlias(),cause);
        cause.printStackTrace();
        closeOnFlush(ctx.channel());
    }

    /**
     * Closes the specified channel after all queued write requests are flushed.
     */
    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }


}
