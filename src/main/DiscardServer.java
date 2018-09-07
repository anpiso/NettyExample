import io.netty.bootstrap.*;
import io.netty.channel.*;
import io.netty.channel.nio.*;
import io.netty.channel.socket.*;
import io.netty.channel.socket.nio.*;


public class DiscardServer {

	public static void main(String[] args) throws Exception{
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) {
					ChannelPipeline p = ch.pipeline();
					p.addLast(new DiscardServerHandler());
					//클라이언트로부터 처리할 핸들러 지정

				}

			});
			
			ChannelFuture f = b.bind(8888).sync();	//bind로 접속할 포트 지정
			//8888번 포트로 클라이언트 접속을 허용하고 클라이언트가 받은 데이터를 DiscardServerHandler가 처리하도록 지정
			
			f.channel().closeFuture().sync();
		}
		
		finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}


	}

}
