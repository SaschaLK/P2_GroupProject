package game;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.io.*;

public class ClientSocket implements ISocket {
	private int serverScore = 0;
	private int myScore;
	
	private boolean shouldSend;
	
	private boolean shouldClose;
	
	public ClientSocket(MainController controller, String ip, int port) {
		new Thread(new Runnable() {
			public void run() {
				try {
					System.out.println("Connecting to server on "+ip+":"+port+"...");
					
					Selector selector = Selector.open();
					
					SocketChannel socket = SocketChannel.open(new InetSocketAddress(ip, port));
					socket.configureBlocking(false);
					
				    socket.register(selector, socket.validOps());
					
					System.out.println("Connected to server!");
					
					while(!shouldClose) {
						selector.select();
						
						Set<SelectionKey> keys = selector.selectedKeys();
						
						for(SelectionKey key : keys) {
							if(key.isReadable()) {
								ByteBuffer buffer = ByteBuffer.allocate(4);
								socket.read(buffer);
								
								serverScore = byteArrayToInt(buffer.array());
								controller.getView().setRemoteScore(serverScore);
								
								System.out.println("Score: "+serverScore);
							}
							
							if(key.isWritable() && shouldSend) {
								shouldSend = false;
								
								ByteBuffer buffer = ByteBuffer.allocate(4);
								buffer.putInt(myScore); // TODO: send score like this in a loop
								buffer.flip();
								socket.write(buffer);
							}
						}
					}
					
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void sendScore(int score) {
		myScore = score;
		shouldSend = true;
	}
	
	public void close() {
		shouldClose = true;
	}
	
	public static int byteArrayToInt(byte[] b) 
	{
	    int value = 0;
	    for (int i = 0; i < 4; i++) {
	        int shift = (4 - 1 - i) * 8;
	        value += (b[i] & 0x000000FF) << shift;
	    }
	    return value;
	}
}