import java.io.*;
import java.net.*;

import io.netty.handler.ssl.SslContext;

//Netty를 이용하지 않은 Blocking소켓의 구현

public class BlockingServer {

	public static void main(String[] args) throws Exception{
		BlockingServer server = new BlockingServer();
		server.run();	

		SslContext sl;
	}
	private void run() throws IOException{
		ServerSocket server = new ServerSocket(8000);
		System.out.println("접속 대기중");
		
		while(true) {
			Socket sock = server.accept();		//디버깅 모드로 실행하면 여기서 멈춤, 왜냐하면 연결되는 클라이언트가 없음
												//cmd에서 서버 접속 명령을 실행하면 
			System.out.println("클라이언트 연결됨");	//accept메소드가 실행되고 소켓을 생성하고
			
			OutputStream out = sock.getOutputStream();
			InputStream in = sock.getInputStream();
			
			while(true) {
				try {
					int request = in.read();	//여기서 다시 멈춤, 왜냐하면 클라이언트에게서 받은 데이터가 없음
					out.write(request);					
				}
				catch(IOException e) {
					break;
				}
			}
		}
	}

}
