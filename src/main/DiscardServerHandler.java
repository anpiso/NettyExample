import io.netty.channel.*;


public class DiscardServerHandler extends SimpleChannelInboundHandler<Object>{
	@Override
	public void channelRead0 (ChannelHandlerContext ctx, Object msg) throws Exception{
		//DiscardServer���� ������ 8000�� ��Ʈ�� ������ Ŭ���̾�Ʈ�� �����͸� �����ϸ� 
		//channelRead0�޼ҵ尡 �ڵ����� ��������� ���⼭�¾ƹ��͵� ����
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
	
}
