import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		//channelActive�̺�Ʈ�� ChannelInboundHandler�� ���ǵ� �̺�Ʈ�� ����ä���� ���ʷ� Ȱ��ȭ �� �� ����ȴ�.
		String sendMessage = "Hello, Netty";
		
		ByteBuf messageBuffer = Unpooled.buffer();
		messageBuffer.writeBytes(sendMessage.getBytes());
		
		StringBuilder builder = new StringBuilder();
		builder.append("������ ���ڿ� [");
		builder.append(sendMessage);
		builder.append("]");
		
		System.out.println(builder.toString());
		
		ctx.writeAndFlush(messageBuffer);
		//writeAndFlush�޼ҵ�� ���������� ������ ��ϰ� ������ �� ���� �޼ҵ带 ȣ���Ѵ�. 
		//ä�ο� �����͸� ����ϴ� write�޼ҵ带 ȣ���� ��, 
		//ä�ο� �ִ� �����͸� ������ �����ϴ� flush�޼ҵ��̴�.
	
		
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		//�����κ��� ���ŵ� �޼����� ���� �� ȣ��Ǵ� �̺�Ʈ�̴�.
		
		String readMessage = ((ByteBuf)msg).toString(Charset.defaultCharset());
		//�����κ��� ���ŵ� �����Ͱ� ����� msg��ü�� ���ڿ� �����ͷ� Ÿ��ĳ�����Ѵ�.
		
		StringBuilder builder = new StringBuilder();
		builder.append("������ ���ڿ� [");
		builder.append(readMessage);
		builder.append("]");
		
		System.out.println(builder.toString());
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		//���ŵ� �����͸� ��� �о��� �� �ڵ����� ȣ��Ǵ� �̺�Ʈ�̴�. 
		//channelRead�̺�Ʈ�� ������ �Ϸ�Ǽ� ���� ȣ��ȴ�.
		
		ctx.close();
		//���ŵ� �����͸� ��� ���� ��, ������ ����� ä���� �ݴ´�. ������ �ۼ��� ä���� ������ Ŭ���̾�Ʈ ���α׷��� ����ȴ�.
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();		
	}
}
