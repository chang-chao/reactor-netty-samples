package me.changchao.reactor_netty_samples.tcp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.netty.DisposableServer;
import reactor.netty.NettyPipeline.SendOptions;
import reactor.netty.tcp.TcpServer;

public class EchoServer {
	final static Logger logger = LoggerFactory.getLogger(EchoServer.class);

	public static void main(String[] args) {
		int listenPort = 9999;

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

		server.onDispose().block();
	}

}
