
package uptime;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Connects to a server periodically to measure and print the uptime of the
 * server.  This example demonstrates how to implement reliable reconnection
 * mechanism in Netty.
 */
public final class UptimeClient {

	static final String HOST = System.getProperty("host", "127.0.0.1");
	static final int PORT = Integer
			.parseInt(System.getProperty("port", "8089"));
	// Sleep 5 seconds before a reconnection attempt.
	static final int RECONNECT_DELAY = Integer.parseInt(System.getProperty(
			"reconnectDelay", "5"));
	// Reconnect when the server sends nothing for 10 seconds.
	static final int READ_TIMEOUT = Integer.parseInt(System.getProperty(
			"readTimeout", "10"));

	private static final UptimeClientHandler handler = new UptimeClientHandler();

	public static void main(String[] args) throws Exception {
		configureBootstrap(new Bootstrap()).connect();
	}

	private static Bootstrap configureBootstrap(Bootstrap b) {
		return configureBootstrap(b, new NioEventLoopGroup());
	}

	static Bootstrap configureBootstrap(Bootstrap b, EventLoopGroup g) {
		b.group(g).channel(NioSocketChannel.class).remoteAddress(HOST, PORT)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(
								new IdleStateHandler(READ_TIMEOUT, 0, 0),
								handler);
					}
				});

		return b;
	}

	static void connect(Bootstrap b) {
		b.connect().addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (future.cause() != null) {
					handler.startTime = -1;
					handler.println("Failed to connect: " + future.cause());
				}
			}
		});
	}
}
