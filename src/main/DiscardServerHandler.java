import io.netty.channel.*;


public class DiscardServerHandler extends SimpleChannelInboundHandler<Object>{
	@Override
	public void channelRead0 (ChannelHandlerContext ctx, Object msg) throws Exception{
		//�ƹ��͵� ����
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
	
}
