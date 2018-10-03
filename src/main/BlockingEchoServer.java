import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

public class BlockingEchoServer {

	public static void main(String[] args) throws Exception{
			EventLoopGroup bossGroup = new OioEventLoopGroup(1);
			EventLoopGroup workerGroup = new OioEventLoopGroup();
			//이벤트 루프

			try {
				ServerBootstrap b = new ServerBootstrap();
				b.group(bossGroup, workerGroup)
				.channel(OioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) {
						ChannelPipeline p = ch.pipeline();
						p.addLast(new EchoServerHandler());
						//클라이언트로부터 처리할 핸들러 지정

					}

				});
				
				ChannelFuture f = b.bind(8000).sync();	//bind로 접속할 포트 지정
				//8000번 포트로 클라이언트 접속을 허용하고 클라이언트가 받은 데이터를 DiscardServerHandler가 처리하도록 지정
				
				f.channel().closeFuture().sync();
			}
			
			finally {
				workerGroup.shutdownGracefully();
				bossGroup.shutdownGracefully();
			}


		}


	}

}
