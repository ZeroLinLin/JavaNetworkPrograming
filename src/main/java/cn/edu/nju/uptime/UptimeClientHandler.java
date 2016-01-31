package cn.edu.nju.uptime;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**     
 * 类名称：UptimeClientHandler    
 * 类描述：    
 *     
 */
public class UptimeClientHandler extends SimpleChannelInboundHandler<Object> {
	/**    
	 * 创建一个新的实例 UptimeClientHandler.    
	 *        
	 */
	public UptimeClientHandler() {
	}

	public long startTime = -1;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		if (startTime < 0) {
			startTime = System.currentTimeMillis();
		}
		System.out.println("连接到 " + ctx.channel().remoteAddress());
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if (!(evt instanceof IdleStateEvent)) {
			return;
		}
		IdleStateEvent event = (IdleStateEvent) evt;
		if (event.state() == IdleState.READER_IDLE) {
			System.out.println("未连接成功");
			ctx.close();
		}
	};

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("未连接成功，远程地址为" + ctx.channel().remoteAddress());
	};

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("睡眠   " + UptimeClient.RECONNECT_DELAY + 'S');
		final EventLoop loop = ctx.channel().eventLoop();
		loop.schedule(new Runnable() {

			@Override
			public void run() {
				System.out.println("重新连接到" + UptimeClient.HOST + ":"
						+ UptimeClient.PORT);
				UptimeClient.connect(UptimeClient.configureBootstrap(
						new Bootstrap(), loop));

			}
		}, UptimeClient.RECONNECT_DELAY, TimeUnit.SECONDS);

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	};

	public void println(String msg) {
		if (startTime < 0) {
			System.err.format("[SERVER IS DOWN] %s%n", msg);
		} else {
			System.err.format("[UPTIME: %ds] %s%n",
					(System.currentTimeMillis() - startTime) / 1000, msg);
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
