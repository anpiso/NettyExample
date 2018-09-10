import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public final class EchoClient {

	public static void main(String[] args) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		//�̺�Ʈ ����
		
		try {
			Bootstrap b = new Bootstrap();
			//��Ʈ��Ʈ��
			b.group(group)
			.channel(NioSocketChannel.class)
			//Ŭ���̾�Ʈ ���ø����̼��� �����ϴ� ä���� ������ �����Ѵ�. ���⼭�� NIO����ä���� NioSocketChannelŬ������ �����ߴ�.
			//��, ������ ����� Ŭ���̾�Ʈ�� ���� ä���� Nio�� �����ϰ� �ȴ�.
			.handler(new ChannelInitializer<SocketChannel>(){
				//Ŭ���̾�Ʈ ���������̼��̷� ä�� ������������ ������ �Ϲ� ���� ä�� Ŭ������ SocketChannel�� �����Ѵ�.
				@Override
				public void initChannel(SocketChannel ch) throws Exception{
					ChannelPipeline p  = ch.pipeline();
					p.addLast(new EchoClientHandler());
				}
			});
			
			ChannelFuture f = b.connect("localhost", 8000).sync();
			//�񵿱� ����� �޼ҵ��� connect�� ȣ���Ѵ�. connect�޼ҵ�� �޼ҵ��� ȣ�� ����� ChannelFuture��ü�� �����ִµ�,
			//�� ��ü�� ���ؼ� �񵿱� �޼ҵ��� ó�� ����� Ȯ���� �� �ִ�. ChannelFuture��ü�� sync�޼ҵ�� ChannelFuture��ü�� ��û�� �Ϸ�� �� ���� ����Ѵ�.
			//��, ��û�� �����ϸ� ���ܸ� ������. �� connect�޼ҵ� ó���� �Ϸ�� �� ���� ���� �������� �������� �ʴ´�.
			
			f.channel().closeFuture().sync();
		}
		finally {
			group.shutdownGracefully();
		}

	}

}
