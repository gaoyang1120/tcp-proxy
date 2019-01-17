package com.blueline.tool.proxy.tcp.handlers.echo;

import com.fasterxml.jackson.databind.util.JSONPObject;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class Client {
	static String host = "127.0.0.1";
    static int port = 9001;
    
//    public static void main(String[] args) {
//        EventLoopGroup group = new NioEventLoopGroup();
//        Bootstrap b = new Bootstrap();
//        b.group(group)
//                .channel(NioSocketChannel.class)
//                .option(ChannelOption.TCP_NODELAY,true)
//                .handler(new ChannelInitializer<SocketChannel>() {
//                    @Override
//                    protected void initChannel(SocketChannel ch) throws Exception {
//                        //这里一定要加入这两个类，是用来给object编解码的，如果没有就无法传送对象
//                        //并且，实体类要实现Serializable接口，否则也无法传输
//                        ch.pipeline().addLast(new ObjectEncoder());
//                        ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE,
//                                ClassResolvers.weakCachingConcurrentResolver(null))); // 最大长度
//                        ch.pipeline().addLast(new ClientHandler());
//                    }
//                });
//        try {
//        	
//            ChannelFuture f =b.connect(host,port).sync();
//            
////            Simple s = new Simple();
////            s.setA("gggg");
//            f.channel().writeAndFlush("{'a':'b'}");
//            
//            f.channel().closeFuture().sync();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }finally {
//            group.shutdownGracefully();
//        }
//    }

}
