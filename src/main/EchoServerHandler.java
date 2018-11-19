import java.nio.charset.Charset;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;	//클라이언트로부터 수신한 데이터를 처리하는 이벤트 제공


public class EchoServerHandler extends ChannelInboundHandlerAdapter{
	//입력된 데이터를 처리하는 이벤트 핸들러인 ChannelInboundHandlerAdapter를 상속
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		//데이터 수신 이벤트 처리 메소드. 클라이언트로부터 데이터 수신이 이루어졌을 때 네티가 자동으로 호출하는 메소드
		String readMessage = ((ByteBuf)msg).toString(Charset.defaultCharset());
		//수신된 데이터를 가지고 있는 네티의 바이트 버퍼 객체로부터 문자열 데이터를 읽어온다.
		
		System.out.println("수신한 문자열 [" + readMessage + ']');
		//수신된 문자열을 콘솔로 출력.
		
		ctx.write(msg);
		//ctx는 ChannelHandlerContext인터페이스의 객체로서 채널 파이프라인에 대한 이벤트를 처리한다.
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
		//ChannelRead이벤트의 처리가 완료된 후 자동으로 수행되는 이벤트 메소드로서 채널 파이프라인에 저장된 버퍼를 전송하는 flush메소드를 호출
	}
	
}