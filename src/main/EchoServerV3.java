import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class EchoServerV3 {

	public static void main(String[] args) throws Exception{
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap b = new ServerBootstrap();			
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {	//<< �� �κ��� handler���� childHandler�� �ٲ���µ� 
				@Override											//childHandler�޼ҵ�� ���� ���� ä�η� ����� Ŭ���̾�Ʈ ä�ο� ������������ �����ϴ� ������ �Ѵ�
				public void initChannel(SocketChannel ch) {
					ChannelPipeline p = ch.pipeline();
					p.addLast(new LoggingHandler(LogLevel.DEBUG));	//�ۼ��ŵǴ� �����͸� LoggingHandler�� ó���Ѵ�.
					p.addLast(new EchoServerHandler());
					
				}
			});
			ChannelFuture f = b.bind(8000).sync();
            f.channel().closeFuture().sync();
			
		}
		finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

}
