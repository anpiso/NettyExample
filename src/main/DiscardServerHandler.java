import io.netty.channel.*;


public class DiscardServerHandler extends SimpleChannelInboundHandler<Object>{
	@Override
	public void channelRead0 (ChannelHandlerContext ctx, Object msg) throws Exception{
		//DiscardServer에서 지정한 8000번 포트로 접속한 클라이언트가 데이터를 전송하면 
		//channelRead0메소드가 자동으로 실행되지만 여기서는아무것도 안함
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
	
}
