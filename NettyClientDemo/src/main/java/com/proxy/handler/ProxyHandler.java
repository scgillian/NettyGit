package com.proxy.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class ProxyHandler extends SimpleChannelInboundHandler<FullHttpRequest>{
	 
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		
		if(msg.decoderResult() != DecoderResult.SUCCESS) {
			ctx.close();
			return;
		}
		String uri = msg.uri();
		System.out.println("uri:"+uri);
		ByteBuf data = msg.content();
		byte[] bytes = new byte[data.readableBytes()];
		data.readBytes(bytes);
        System.out.println(new String(bytes)); 
        // Send response back so the browser won't timeout
        ByteBuf responseBytes = ctx.alloc().buffer();
        responseBytes.writeBytes("Hello World".getBytes());

        FullHttpResponse response = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1, HttpResponseStatus.OK, responseBytes);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, 
                               "text/plain");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, 
                               response.content().readableBytes());

        response.headers().set(HttpHeaderNames.CONNECTION, 
        		"keep-alive");  
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		
	}

}
