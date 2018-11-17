import io.netty.bootstrap.*;
import io.netty.channel.*;
import io.netty.channel.nio.*;
import io.netty.channel.socket.*;
import io.netty.channel.socket.nio.*;

//핸들러에서 수신한 데이터를 그대로 돌려줄 EchoServer 생성


public class EchoServer {

	public static void main(String[] args) throws Exception{
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		//EventLoopGroup인터페이스에 NioEventLoopGroup클래스의 객체를 할당한다.
		//생성자에 입력된 스레드 수가 1이므로 단일 스레드로 동작하게 한다.
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		//EventLoopGroup인터페이스에 NioEventLoopGroup클래스의 객체를 할당한다.
		//생성자에 입력된 스레드 수가 없으므로 CPU코어 수에 따른 스레드로 동작하게 한다.
		//이벤트 루프

		try {
			ServerBootstrap b = new ServerBootstrap();	//ServerBootstrap을 설정한다.
			b.group(bossGroup, workerGroup)				//첫번째 인수는 부모 스레드로 클라이언트 연결 요청의 수락을 담당한다.
														//두번째 인수는 연결된 소켓에 대한 I/O처리를 담당하는 자식 스레드이다
			
			.channel(NioServerSocketChannel.class)		//서버소켓(부모 스레드)이 사용할 네트워크 입출력 모드를 설정한다.
														//위에서 Nio모드로 이벤트 그룹을 설정했기 때문에 여기서는 NioServerSocketChannel클래스를 설정한다.
			.option(ChannelOption.SO_LINGER, 0)			
			.option(ChannelOption.TCP_NODELAY, true)
			.childHandler(new ChannelInitializer<SocketChannel>() {		//자식채널의 초기화 방법을 설정한다.
				@Override												//ChannelInitializer는 클라이언트로부터 연결된 태널이 초기화될 때의 기본 동작이 지정된 추상클래스이다
				public void initChannel(SocketChannel ch) {				
					ChannelPipeline p = ch.pipeline();					//채널 파이프라인 객체를 생성하고
					p.addLast(new EchoServerHandler());					//채널 파이프라인에 EchoServerHandler클래스를 등록한다. 
																		//EchoServerHandler클래스는 이후에 클라이언트의 연결이 생성되었을 때 데이터 처리를 담당한다.
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
