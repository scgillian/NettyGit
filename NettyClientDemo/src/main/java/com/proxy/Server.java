package com.proxy;

import com.proxy.handler.ProxyHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;


public class Server {

	private int port;

	public Server(int port) {
		this.port = port;

	}

	public void run() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new HttpRequestDecoder())
							.addLast(new HttpResponseEncoder())
							//.addLast(new HttpServerCodec())//貌似等于上面两行
							.addLast(new HttpObjectAggregator(Short.MAX_VALUE))
							.addLast(new ProxyHandler());
							

						}

					});

			ChannelFuture f = b.bind(port).sync();

			f.channel().closeFuture().sync();

		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();

		}
	}
	public static void main(String[] args)  {
		// TODO Auto-generated method stub
		  int port = 8080;
	        if (args.length > 0) {
	            port = Integer.parseInt(args[0]);
	        }

	        try {
				new Server(port).run();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

}
