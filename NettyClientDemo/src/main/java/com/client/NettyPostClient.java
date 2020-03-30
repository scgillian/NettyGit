package com.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

public class NettyPostClient {
	
	public static void main(String[] args) {
		Bootstrap bootstrap = new Bootstrap();
		EventLoopGroup group = new NioEventLoopGroup();
		bootstrap.group(group).channel(NioSocketChannel.class).
		option(ChannelOption.SO_KEEPALIVE, true).
        handler(
				new ChannelInitializer<NioSocketChannel>(){

			@Override
			protected void initChannel(NioSocketChannel ch) throws Exception {
				ch.pipeline().addLast(new HttpClientCodec());
				//ch.pipeline().addLast(new HttpObjectAggregator(1048576));
				ch.pipeline().addLast(new ClientHandler());
				
			}
			
		});	
		try {
			URI uri = new URI("http://localhost:8080/abc");
			String scheme = uri.getScheme() == null? "http" : uri.getScheme();
		    String host = uri.getHost() == null? "127.0.0.1" : uri.getHost();
		    int port = uri.getPort();
	        if (port == -1) {
	            if ("http".equalsIgnoreCase(scheme)) {
	                port = 80;
	            } else if ("https".equalsIgnoreCase(scheme)) {
	                port = 443;
	            }
	        }
	        
	        
	        
	        //post can not use request of 4th parameter Unpooled.EMPTY_BUFFER
			FullHttpRequest request = new DefaultFullHttpRequest(
		            HttpVersion.HTTP_1_1, HttpMethod.POST,uri.getRawPath());
			ChannelFuture cf = bootstrap.connect(host,port).sync();
			request.headers().set(HttpHeaderNames.HOST, host);
	        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
	        request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
	        request.headers().add(HttpHeaderNames.CONTENT_TYPE, "application/json");
	        ByteBuf bbuf = Unpooled.copiedBuffer("{\"color\":\"bdd\",\"id\":1}", StandardCharsets.UTF_8);
	        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, bbuf.readableBytes());
	        request.content().clear().writeBytes(bbuf);

			cf.channel().writeAndFlush(request); ;
			cf.channel().closeFuture().sync();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			group.shutdownGracefully();
		}
		
	}

}
