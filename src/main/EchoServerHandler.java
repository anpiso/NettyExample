import java.nio.charset.Charset;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;	//Ŭ���̾�Ʈ�κ��� ������ �����͸� ó���ϴ� �̺�Ʈ ����


public class EchoServerHandler extends ChannelInboundHandlerAdapter{
	//�Էµ� �����͸� ó���ϴ� �̺�Ʈ �ڵ鷯�� ChannelInboundHandlerAdapter�� ���
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		//������ ���� �̺�Ʈ ó�� �޼ҵ�. Ŭ���̾�Ʈ�κ��� ������ ������ �̷������ �� ��Ƽ�� �ڵ����� ȣ���ϴ� �޼ҵ�
		String readMessage = ((ByteBuf)msg).toString(Charset.defaultCharset());
		//���ŵ� �����͸� ������ �ִ� ��Ƽ�� ����Ʈ ���� ��ü�κ��� ���ڿ� �����͸� �о�´�.
		
		System.out.println("������ ���ڿ� [" + readMessage + ']');
		//���ŵ� ���ڿ��� �ַܼ� ���.
		
		ctx.write(msg);
		//ctx�� ChannelHandlerContext�������̽��� ��ü�μ� ä�� ���������ο� ���� �̺�Ʈ�� ó���Ѵ�.
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
		//ChannelRead�̺�Ʈ�� ó���� �Ϸ�� �� �ڵ����� ����Ǵ� �̺�Ʈ �޼ҵ�μ� ä�� ���������ο� ����� ���۸� �����ϴ� flush�޼ҵ带 ȣ��
	}
	
}