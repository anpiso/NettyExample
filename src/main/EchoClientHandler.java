import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		//channelActive이벤트는 ChannelInboundHandler에 정의된 이벤트로 소켓채널이 최초로 활성화 될 때 실행된다.
		String sendMessage = "Hello, Netty";
		
		ByteBuf messageBuffer = Unpooled.buffer();
		messageBuffer.writeBytes(sendMessage.getBytes());
		
		StringBuilder builder = new StringBuilder();
		builder.append("전송한 문자열 [");
		builder.append(sendMessage);
		builder.append("]");
		
		System.out.println(builder.toString());
		
		ctx.writeAndFlush(messageBuffer);
		//writeAndFlush메소드는 내부적으로 데이터 기록과 전송의 두 가지 메소드를 호출한다. 
		//채널에 데이터를 기록하는 write메소드를 호출한 뒤, 
		//채널에 있는 데이터를 서버로 전송하는 flush메소드이다.
	
		
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		//서버로부터 수신된 메세지가 있을 때 호출되는 이벤트이다.
		
		String readMessage = ((ByteBuf)msg).toString(Charset.defaultCharset());
		//서버로부터 수신된 데이터가 저장된 msg객체를 문자열 데이터로 타입캐스팅한다.
		
		StringBuilder builder = new StringBuilder();
		builder.append("수신한 문자열 [");
		builder.append(readMessage);
		builder.append("]");
		
		System.out.println(builder.toString());
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		//수신된 데이터를 모두 읽었을 때 자동으로 호출되는 이벤트이다. 
		//channelRead이벤트의 수행이 완료되서 나서 호출된다.
		
		ctx.close();
		//수신된 데이터를 모두 읽은 후, 서버와 연결된 채널을 닫는다. 데이터 송수신 채널은 닫히고 클라이언트 프로그램은 종료된다.
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();		
	}
}
