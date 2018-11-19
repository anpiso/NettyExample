import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.AsciiString;

public class HttpHelloWorldServerhandler extends ChannelInboundHandlerAdapter{
	private static final byte[] CONTENT = {'H', 'e', 'l', 'l', 'o'};
	
	private static final AsciiString CONTENT_TYPE = new AsciiString("Conttent-Type");
	private static final AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");
	private static final AsciiString CONNNECTION = new AsciiString("Connection");
	private static final AsciiString KEEP_ALIVE = new AsciiString("keep_Alive");
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if(msg instanceof HttpRequest) {
			HttpRequest req = (HttpRequest) msg;
			
			if(HttpHeaders.is100ContinueExpected(req)) {
				ctx.write(new DefaultFullHttpResponse(HTTP_1_1, continue));
			}
			boolean keepAlive = HttpHeaders.isKeepAlive(req);
			FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unlooped.wrappedBuffer(CONTENT));
			response.headers().set(CONTENT_TYPE, "text/plain");
			response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
			
			if(!keepAlive) {
				ctx.write(response).addListener(ChannelFutureListener.CLOSE);
			}
		}
		
	}

}
