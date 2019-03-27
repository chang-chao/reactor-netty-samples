package me.changchao.reactor_netty_samples.tcp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import reactor.netty.DisposableServer;
import reactor.netty.NettyPipeline.SendOptions;
import reactor.netty.tcp.TcpServer;

public class EchoServer {
	final static Logger logger = LoggerFactory.getLogger(EchoServer.class);

	static class EchoServerConnectionHandler extends ChannelDuplexHandler {

		@Override
		public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
			logger.debug("channelUnregistered");
			super.channelUnregistered(ctx);
		}

		@Override
		public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
			logger.debug("channelRegistered");
			super.channelRegistered(ctx);
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			logger.debug("channelInactive");
			super.channelInactive(ctx);
		}

	}

	public static void main(String[] args) {
		int listenPort = 9999;

		DisposableServer server = TcpServer.create().port(listenPort).doOnConnection(connection -> {
			// only when client is connected
			logger.debug("connection:" + connection);
			connection.addHandlerLast(new EchoServerConnectionHandler());
		}).wiretap(true).observe((connection, newState) -> {
			// only when connected and configured, but no disconnected
			logger.debug("connection:" + connection + ", newState=" + newState);
		}).handle(
				(inbound, outbound) -> outbound.options(SendOptions::flushOnEach)
						.sendString(inbound.receive().asString()).neverComplete())
				.bindNow();

		server.onDispose().block();
	}

}
