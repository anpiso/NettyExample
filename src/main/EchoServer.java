import io.netty.bootstrap.*;
import io.netty.channel.*;
import io.netty.channel.nio.*;
import io.netty.channel.socket.*;
import io.netty.channel.socket.nio.*;

//�ڵ鷯���� ������ �����͸� �״�� ������ EchoServer ����


public class EchoServer {

	public static void main(String[] args) throws Exception{
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		//�̺�Ʈ ����

		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) {
					ChannelPipeline p = ch.pipeline();
					p.addLast(new EchoServerHandler());
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
