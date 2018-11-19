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
			.childHandler(new ChannelInitializer<SocketChannel>() {	//<< 이 부분이 handler에서 childHandler로 바뀌었는데 
				@Override											//childHandler메소드는 서버 소켓 채널로 연결된 클라이언트 채널에 파이프라인을 설정하는 역할을 한다
				public void initChannel(SocketChannel ch) {
					ChannelPipeline p = ch.pipeline();
					p.addLast(new LoggingHandler(LogLevel.DEBUG));	//송수신되는 데이터를 LoggingHandler가 처리한다.
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
