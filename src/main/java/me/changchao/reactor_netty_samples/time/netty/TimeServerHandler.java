package me.changchao.reactor_netty_samples.time.netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(final ChannelHandlerContext ctx) {
		// (1) the channelActive() method will be invoked when a connection is
		// established and ready to generate traffic.
		// Let's write a 32-bit integer that represents the current time in this method.

		// (2)To send a new message, we need to allocate a new buffer which will contain
		// the message.
		// We are going to write a 32-bit integer, and therefore we need a ByteBuf whose
		// capacity is at least 4 bytes.
		// Get the current ByteBufAllocator via ChannelHandlerContext.alloc() and
		// allocate a new buffer.

//		final ByteBuf time = ctx.alloc().buffer(4);
//		time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));
//
//		final ChannelFuture f = ctx.writeAndFlush(time); // (3)
//		f.addListener(new ChannelFutureListener() {
//			@Override
//			public void operationComplete(ChannelFuture future) {
//				assert f == future;
//				ctx.close();
//			}
//		}); // (4)
//		
        //or
        // f.addListener(ChannelFutureListener.CLOSE);
        ChannelFuture channelFuture = ctx.writeAndFlush(new UnixTime());
        channelFuture.addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
