import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class HttpHelloWorldServer {
	static final boolean SSL = System.getProperty("ssl") != null;
	static final int PORT = Integer.parseInt(System.getProperty("port", SSL? "8443" : "8080"));

	EventLoopGroup bossGroup = new NioEventLoopGroup(1);
	EventLoopGroup workerGroup = new NioEventLoopGroup();
	
	try {
		ServerBootstrap b = new ServerBootstrap();
		b.option(ChannelOption.SO_BACKLOG, 1024);
		
		b.group(bossGroup, workerGroup)
		.channel(NioServerSocketChannel.class)
		.handler(new LoggingHandler(LogLevel.INFO))
		.childHandler(new HttpHelloWorldSereverInitializer<sslCtx>);
		
		Channel ch = b.bind().sync().channel();
		
		System.err.println("Open your web browser and navigate to" + (SSL? "https" : "http") + "://127.0.0.1:" + PORT + '/');
		
		ch.closeFuture().sync();
		
	}finally{
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}
	public static void main(String[] args) {

	}

}
