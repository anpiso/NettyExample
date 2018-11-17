import io.netty.bootstrap.*;
import io.netty.channel.*;
import io.netty.channel.nio.*;
import io.netty.channel.socket.*;
import io.netty.channel.socket.nio.*;

//�ڵ鷯���� ������ �����͸� �״�� ������ EchoServer ����


public class EchoServer {

	public static void main(String[] args) throws Exception{
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		//EventLoopGroup�������̽��� NioEventLoopGroupŬ������ ��ü�� �Ҵ��Ѵ�.
		//�����ڿ� �Էµ� ������ ���� 1�̹Ƿ� ���� ������� �����ϰ� �Ѵ�.
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		//EventLoopGroup�������̽��� NioEventLoopGroupŬ������ ��ü�� �Ҵ��Ѵ�.
		//�����ڿ� �Էµ� ������ ���� �����Ƿ� CPU�ھ� ���� ���� ������� �����ϰ� �Ѵ�.
		//�̺�Ʈ ����

		try {
			ServerBootstrap b = new ServerBootstrap();	//ServerBootstrap�� �����Ѵ�.
			b.group(bossGroup, workerGroup)				//ù��° �μ��� �θ� ������� Ŭ���̾�Ʈ ���� ��û�� ������ ����Ѵ�.
														//�ι�° �μ��� ����� ���Ͽ� ���� I/Oó���� ����ϴ� �ڽ� �������̴�
			
			.channel(NioServerSocketChannel.class)		//��������(�θ� ������)�� ����� ��Ʈ��ũ ����� ��带 �����Ѵ�.
														//������ Nio���� �̺�Ʈ �׷��� �����߱� ������ ���⼭�� NioServerSocketChannelŬ������ �����Ѵ�.
			.option(ChannelOption.SO_LINGER, 0)			
			.option(ChannelOption.TCP_NODELAY, true)
			.childHandler(new ChannelInitializer<SocketChannel>() {		//�ڽ�ä���� �ʱ�ȭ ����� �����Ѵ�.
				@Override												//ChannelInitializer�� Ŭ���̾�Ʈ�κ��� ����� �³��� �ʱ�ȭ�� ���� �⺻ ������ ������ �߻�Ŭ�����̴�
				public void initChannel(SocketChannel ch) {				
					ChannelPipeline p = ch.pipeline();					//ä�� ���������� ��ü�� �����ϰ�
					p.addLast(new EchoServerHandler());					//ä�� ���������ο� EchoServerHandlerŬ������ ����Ѵ�. 
																		//EchoServerHandlerŬ������ ���Ŀ� Ŭ���̾�Ʈ�� ������ �����Ǿ��� �� ������ ó���� ����Ѵ�.
					//Ŭ���̾�Ʈ�κ��� ó���� �ڵ鷯 ����

				}

			});
			
			ChannelFuture f = b.bind(8000).sync();	//bind�� ������ ��Ʈ ����
			//8000�� ��Ʈ�� Ŭ���̾�Ʈ ������ ����ϰ� Ŭ���̾�Ʈ�� ���� �����͸� DiscardServerHandler�� ó���ϵ��� ����
			
			f.channel().closeFuture().sync();
		}
		
		finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}


	}

}
