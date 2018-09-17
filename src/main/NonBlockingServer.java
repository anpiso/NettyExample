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
		try (	//try����� ���� �� �Ʒ��� �� �ڿ����� �ڵ����� �������ش�
				Selector selector = Selector.open();
				//Selector�� �ڽſ��� ��ϵ� ä�ο� ��������� �߻��ߴ��� �˻��ϰ� ��������� �߻��� ä�ο� �����ϰ� �Ѵ�. 
				ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
				//���ŷ ������ ServerSocket�� �����ϴ� ����ŷ ������ ���� ���� ä���� �����Ѵ�
				//���ŷ ���ϰ� �ٸ�����, ����ä���� ���� �����ϰ� ��Ʈ�� ���ε��Ѵ�.				
				){
			if((serverSocketChannel.isOpen()) && (selector.isOpen())) {	//Selector�� ServerSocketChannel�� ����� �����Ǿ����� Ȯ��
				serverSocketChannel.configureBlocking(false);	//����ä���� ���ŷ�� Ǯ���ش�. �⺻���� true�� ���ŷ����̴�.
				serverSocketChannel.bind(new InetSocketAddress(8000));
				//���� ����� ��Ʈ�� 8000������ �����ϰ�, ������ ServerSocketChannel�� �Ҵ��Ѵ�.
				//�̰����� ServerSocketChannel�� ��Ʈ�κ��� Ŭ���̾�Ʈ�� ������ ������ �� �ִ�.
				
				serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
				//ServerSocketChannel��ü�� Selector��ü�� ����Ѵ�.Selector�� 
				//�����û �̺�Ʈ�� Selection.OP_ACCEPT�� �����Ѵ�.
				System.out.println("���� �����");
				
				while(true) {
					selector.select();	//Selector�� ��ϵ� ä�ο��� ��������� �ִ��� �˻��Ѵ�.
										//�ƹ��� I/O�̺�Ʈ�� �߻����� �ʾҴٸ� ������� �� �κп��� ���ŷó���ȴ�.
					Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
					//Selector�� ��ϵ� ä�� �߿��� I/O�̺�Ʈ�� �߻��� ä�ε��� ����� ��ȸ�Ѵ�.
					
					while(keys.hasNext()) {
						SelectionKey key = (SelectionKey) keys.next();
						keys.remove();
						//I/O�̺�Ʈ�� �߻��� ä�ο��� ������ �̺�Ʈ�� �����Ǵ� ���� �����ϱ� ���� ��ȸ�� ��Ͽ��� �����Ѵ�.

						if(!key.isValid()) {
							continue;
						}

						if(key.isAcceptable()) {			//��ȸ�� �̺�Ʈ�� ������ �����û���� Ȯ���ϰ�
							this.acceptOP(key,selector);	//�����û�̶�� ����ó�� �޼ҵ�acceptOP�� �̷�
						}else if(key.isReadable()) {		//��ȸ�� �̺�Ʈ�� ������ ������ �������� Ȯ���ϰ�
							this.readOP(key);				//������ �����̶�� ������ ���� ó�� �޼ҵ�readOP�� �̵�
						}else if(key.isWritable()) {		//��ȸ�� �̺�Ʈ�� ������ ������ ���� �������� Ȯ���ϰ�
							this.writeOP(key);				//������ ���� �����̶�� ������ ���� ó�� �޼ҵ�writeOP�� �̵�
						}

					}
				}
			}
			else {
				System.out.println("���� ������ �������� ���߽��ϴ�.");
			}
		}
		catch(IOException ex) {
			System.out.println(ex);
		}
	}

	private void acceptOP(SelectionKey key, Selector selector) throws IOException{
		ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
		//�����û �̺�Ʈ�� �߻��ϴ� ä���� �׻� ServerSocketChannel�̹Ƿ� �߻��� ä���� ServerSocketChannel�� ĳ�����Ѵ�.
		SocketChannel socketChannel = serverChannel.accept();
		//ServerSocketChannel�� �̿��Ͽ� Ŭ���̾�Ʈ�� ������ �����ϰ� ����� ���� ä���� �����´�.
		socketChannel.configureBlocking(false);
		//����� Ŭ���̾�Ʈ ���� ä���� nonblocking���� �����Ѵ�.

		System.out.println("Ŭ���̾�Ʈ ����� :" + socketChannel.getRemoteAddress());

		keepDataTrack.put(socketChannel, new ArrayList<byte[]>());
		socketChannel.register(selector, SelectionKey.OP_ACCEPT);
		//Ŭ���̾�Ʈ ���� ä���� Selector�� ����Ͽ� I/O�̺�Ʈ�� �����Ѵ�.
	}

	private void readOP(SelectionKey key) {
		try {
			SocketChannel socketChannel = (SocketChannel) key.channel();
			buffer.clear();
			//ä��read�޼ҵ带 ����ϱ� ���� ���۸� Ŭ�����Ѵ�.
			int numRead = -1;
			try {
				numRead = socketChannel.read(buffer);
			}
			catch(IOException e) {
				System.err.println("������ �б� ����");
			}
			if(numRead == -1) {
				this.keepDataTrack.remove(socketChannel);
				System.out.println("Ŭ���̾�Ʈ ���� ���� :" +socketChannel.getRemoteAddress());
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
