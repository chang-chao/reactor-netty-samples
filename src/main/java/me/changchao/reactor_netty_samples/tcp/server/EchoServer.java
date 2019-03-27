package me.changchao.reactor_netty_samples.tcp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.DefaultEventExecutor;
import reactor.netty.DisposableServer;
import reactor.netty.FutureMono;
import reactor.netty.NettyPipeline.SendOptions;
import reactor.netty.tcp.TcpServer;

public class EchoServer {
	final static Logger logger = LoggerFactory.getLogger(EchoServer.class);

	public static void main(String[] args) {
		int listenPort = 9999;
		ChannelGroup group = new DefaultChannelGroup(new DefaultEventExecutor());

		DisposableServer server = TcpServer.create().port(listenPort).doOnConnection(connection -> {
			// only when client is connected, this is OK
			logger.debug("connection:" + connection);
		}).wiretap(true).observe((connection, newState) -> {
			// only called when connection is connected and configured, but not disconnected
			// this is a BUG ?
			logger.debug("connection:" + connection + ", newState=" + newState);
		}).handle(
				(inbound, outbound) -> outbound.options(SendOptions::flushOnEach)
						.sendString(inbound.receive().asString()).neverComplete())
				.bindNow();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			FutureMono.from(group.close()).block();
			server.disposeNow();
		}));

		server.onDispose().block();
	}

}
