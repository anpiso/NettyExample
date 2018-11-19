import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

public class HttpHelloWorldServerInitializer extends ChannelInitializer<SocketChannel> {
	private final SslContext sslCtx;
	
	
	public HttpHelloWorldServerInitializer(SslContext sslCtx) {
		
		this.sslCtx = sslCtx;
	} 


	@Override
	public void initChannel(SocketChannel ch) {
		ChannelPipeline p = ch.pipeline();
		if(sslCtx != null) {
			p.addLast(sslCtx.)
		}
	}
}
