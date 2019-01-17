package com.blueline.tool.proxy.tcp.handlers.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class Formart {
	
	
	public static String convertByteBufToString(Object buf) {

		ByteBuf in = (ByteBuf) buf;

		ByteBuf copiedBuffer = Unpooled.copiedBuffer(in);
		String string = copiedBuffer.toString(CharsetUtil.US_ASCII);

		// RequestProcessor reprocessor = EchoServer.process(sentData);

		// ByteBuf result = (ByteBuf) buf;
		// byte[] bytes = ByteBufUtil.getBytes(result);
		// String string = result.toString(CharsetUtil.UTF_8);

		// for (int i = 0; i < result.capacity(); i++) {
		//// byte b = message.payload().getByte(i);
		// byte b = result.getByte(i);
		// System.out.print((char) b);
		// }
		// ByteBuf copy = result.copy();
		// byte[] array = copy.array();
		// for(int i=0; i<array.length; i++) {
		// System.out.print(array[i]);
		// }
		// String resultStr = result.readCharSequence(result.capacity(),
		// Charset.forName("US-ASCII")).toString();
		// byte[] result1 = new byte[result.readableBytes()];
		// msg中存储的是ByteBuf类型的数据，把数据读取到byte[]中
		// result.readBytes(result1);
		// String resultStr = new String(ByteBufUtil.getBytes(result),
		// Charset.forName("US-ASCII"));
		// System.out.println("Client said:" + resultStr);
		// 释放资源，这行很关键
		// result.release();
		// String response = "I am ok!";
		// // 在当前场景下，发送的数据必须转换成ByteBuf数组
		// ByteBuf encoded = ctx.alloc().buffer(4 * response.length());
		// encoded.writeBytes(response.getBytes());
		// ctx.write(encoded);
		// ctx.flush();
		return string;
	}
}
