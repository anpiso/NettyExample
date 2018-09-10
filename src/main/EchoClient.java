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
		//이벤트 루프
		
		try {
			Bootstrap b = new Bootstrap();
			//부트스트랩
			b.group(group)
			.channel(NioSocketChannel.class)
			//클라이언트 애플리케이션이 생성하는 채널의 종류를 설정한다. 여기서는 NIO소켓채널인 NioSocketChannel클래스를 설정했다.
			//즉, 서버에 연결된 클라이언트의 소켓 채널은 Nio로 동작하게 된다.
			.handler(new ChannelInitializer<SocketChannel>(){
				//클라이언트 애플이케이션이로 채널 파이프라인의 설정에 일반 소켓 채널 클래스인 SocketChannel을 설정한다.
				@Override
				public void initChannel(SocketChannel ch) throws Exception{
					ChannelPipeline p  = ch.pipeline();
					p.addLast(new EchoClientHandler());
				}
			});
			
			ChannelFuture f = b.connect("localhost", 8000).sync();
			//비동기 입출력 메소드인 connect를 호출한다. connect메소드는 메소드의 호출 결과로 ChannelFuture객체를 돌려주는데,
			//이 객체를 통해서 비동기 메소드의 처리 결과를 확인할 수 있다. ChannelFuture객체의 sync메소드는 ChannelFuture객체의 요청이 완료될 때 까지 대기한다.
			//단, 요청이 실패하면 예외를 던진다. 즉 connect메소드 처리가 완료될 때 까지 다음 라인으로 진행하지 않는다.
			
			f.channel().closeFuture().sync();
		}
		finally {
			group.shutdownGracefully();
		}

	}

}
