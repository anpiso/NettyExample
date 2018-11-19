import java.io.*;
import java.net.*;

import io.netty.handler.ssl.SslContext;

//Netty�� �̿����� ���� Blocking������ ����

public class BlockingServer {

	public static void main(String[] args) throws Exception{
		BlockingServer server = new BlockingServer();
		server.run();	

		SslContext sl;
	}
	private void run() throws IOException{
		ServerSocket server = new ServerSocket(8000);
		System.out.println("���� �����");
		
		while(true) {
			Socket sock = server.accept();		//����� ���� �����ϸ� ���⼭ ����, �ֳ��ϸ� ����Ǵ� Ŭ���̾�Ʈ�� ����
												//cmd���� ���� ���� ����� �����ϸ� 
			System.out.println("Ŭ���̾�Ʈ �����");	//accept�޼ҵ尡 ����ǰ� ������ �����ϰ�
			
			OutputStream out = sock.getOutputStream();
			InputStream in = sock.getInputStream();
			
			while(true) {
				try {
					int request = in.read();	//���⼭ �ٽ� ����, �ֳ��ϸ� Ŭ���̾�Ʈ���Լ� ���� �����Ͱ� ����
					out.write(request);					
				}
				catch(IOException e) {
					break;
				}
			}
		}
	}

}
