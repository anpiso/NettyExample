import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NonBlockingServer {
	private Map<SocketChannel, List<byte[]>> keepDataTrack = new HashMap<>();
	private ByteBuffer buffer = ByteBuffer.allocate(2 * 2014);

	private void startEchoServer() {
		try (	//try블록이 끝날 때 아래의 두 자원들을 자동으로 해제해준다
				Selector selector = Selector.open();
				//Selector는 자신에게 등록된 채널에 변경사항이 발생했는지 검사하고 변경사항이 발생한 채널에 접근하게 한다. 
				ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
				//블로킹 소켓의 ServerSocket에 대응하는 논블로킹 소켓의 서버 소켓 채널을 생성한다
				//블로킹 소켓과 다른점은, 소켓채널을 먼저 생성하고 포트로 바인딩한다.				
				){
			if((serverSocketChannel.isOpen()) && (selector.isOpen())) {	//Selector와 ServerSocketChannel이 제대로 생성되었는지 확인
				serverSocketChannel.configureBlocking(false);	//소켓채널의 블로킹을 풀어준다. 기본값은 true로 블로킹모드이다.
				serverSocketChannel.bind(new InetSocketAddress(8000));
				//연결 대기할 포트를 8000번으로 지정하고, 생성된 ServerSocketChannel에 할당한다.
				//이것으로 ServerSocketChannel이 포트로부터 클라이언트의 연결을 생성할 수 있다.
				
				serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
				//ServerSocketChannel객체를 Selector객체에 등록한다.Selector는 
				//연결요청 이벤트인 Selection.OP_ACCEPT를 감지한다.
				System.out.println("접속 대기중");
				
				while(true) {
					selector.select();	//Selector에 등록된 채널에서 변경사항이 있는지 검사한다.
										//아무런 I/O이벤트도 발생하지 않았다면 스레드는 이 부분에서 블로킹처리된다.
					Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
					//Selector에 등록된 채널 중에서 I/O이벤트가 발생한 채널들의 목록을 조회한다.
					
					while(keys.hasNext()) {
						SelectionKey key = (SelectionKey) keys.next();
						keys.remove();
						//I/O이벤트가 발생한 채널에서 동일한 이벤트가 감지되는 것을 방지하기 위해 조회된 목록에서 제거한다.

						if(!key.isValid()) {
							continue;
						}

						if(key.isAcceptable()) {			//조회된 이벤트의 종류가 연결요청인지 확인하고
							this.acceptOP(key,selector);	//연결요청이라면 연결처리 메소드acceptOP로 이롱
						}else if(key.isReadable()) {		//조회된 이벤트의 종류가 데이터 수신인지 확인하고
							this.readOP(key);				//데이터 수신이라면 데이터 읽이 처리 메소드readOP로 이동
						}else if(key.isWritable()) {		//조회된 이벤트의 종류가 데이터 쓰기 가능인지 확인하고
							this.writeOP(key);				//데이터 쓰기 가능이라면 데이터 쓰기 처리 메소드writeOP로 이동
						}

					}
				}
			}
			else {
				System.out.println("서버 소켓을 생성하지 못했습니다.");
			}
		}
		catch(IOException ex) {
			System.out.println(ex);
		}
	}

	private void acceptOP(SelectionKey key, Selector selector) throws IOException{
		ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
		//연결요청 이벤트가 발생하는 채널은 항상 ServerSocketChannel이므로 발생한 채널을 ServerSocketChannel로 캐스팅한다.
		SocketChannel socketChannel = serverChannel.accept();
		//ServerSocketChannel을 이용하여 클라이언트의 연결을 수락하고 연결된 소켓 채널을 가져온다.
		socketChannel.configureBlocking(false);
		//연결된 클라이언트 소켓 채널을 nonblocking으로 설정한다.

		System.out.println("클라이언트 연결됨 :" + socketChannel.getRemoteAddress());

		keepDataTrack.put(socketChannel, new ArrayList<byte[]>());
		socketChannel.register(selector, SelectionKey.OP_ACCEPT);
		//클라이언트 소켓 채널을 Selector에 등록하여 I/O이벤트를 감시한다.
	}

	private void readOP(SelectionKey key) {
		try {
			SocketChannel socketChannel = (SocketChannel) key.channel();
			buffer.clear();
			//채널read메소드를 사용하기 전에 버퍼를 클리어한다.
			int numRead = -1;
			try {
				numRead = socketChannel.read(buffer);
			}
			catch(IOException e) {
				System.err.println("데이터 읽기 에러");
			}
			if(numRead == -1) {
				this.keepDataTrack.remove(socketChannel);
				System.out.println("클라이언트 연결 종료 :" +socketChannel.getRemoteAddress());
				socketChannel.close();
				key.cancel();
				return;
			}

			byte[] data = new byte[numRead];
			System.arraycopy(buffer.array(), 0, data, 0, numRead);
			System.out.println(new String(data, "UTF-8") + "from" + socketChannel.getRemoteAddress());
			doEchoJob(key,data);
		}
		catch(IOException ex) {
			System.err.println(ex);
		}

	}

	private void writeOP(SelectionKey key) throws IOException{
		SocketChannel socketChannel = (SocketChannel) key.channel();

		List<byte[]> channelData = keepDataTrack.get(socketChannel);
		Iterator<byte[]> its = channelData.iterator();

		while(its.hasNext()) {
			byte[] it = its.next();
			its.remove();
			socketChannel.write(ByteBuffer.wrap(it));
		}

		key.interestOps(SelectionKey.OP_ACCEPT);
	}

	private void doEchoJob(SelectionKey key, byte[] data) {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		List<byte[]> channelData = keepDataTrack.get(socketChannel);
		channelData.add(data);

		key.interestOps(SelectionKey.OP_ACCEPT);
	}
	public static void main(String[] args) {
		NonBlockingServer main = new NonBlockingServer();
		main.startEchoServer();
	}

}
