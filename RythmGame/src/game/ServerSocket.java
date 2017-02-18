package game;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.io.*;

class ServerSocket implements ISocket {
	private int clientScore;
	private int myScore;
	
	private boolean shouldSend;
	
	private boolean shouldClose;
	
	public ServerSocket(MainController controller, int port) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try{
					System.out.println("Starting up server on localhost:"+port+"...");
					Selector selector = Selector.open();
					
					ServerSocketChannel socket = ServerSocketChannel.open();
					socket.bind(new InetSocketAddress("localhost", port));
					socket.configureBlocking(false);
					
					socket.register(selector, socket.validOps(), null);
					
					System.out.println("Server is running!");
					
					while(!shouldClose) {
						selector.select();
						
						Set<SelectionKey> keys = selector.selectedKeys();
						
						for(SelectionKey key : keys) {
							// Connect client and grant read and write permissions
							if(key.isAcceptable()) {
								SocketChannel client = socket.accept();
								
								if(client != null) {
									client.configureBlocking(false);
									client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
									
									System.out.println("New connection!");
								}
							}
							// If the client send something receive it here
							if(key.isReadable()) {
								SocketChannel client = (SocketChannel) key.channel();
								ByteBuffer buffer = ByteBuffer.allocate(4);
								
								int read = client.read(buffer);
								
								if(read > 0) {
									clientScore = byteArrayToInt(buffer.array());
									controller.getView().setRemoteScore(clientScore);
									
									System.out.println("Score: "+clientScore);
								}
							}
							// Sending the score of all other clients to all clients
							if(key.isWritable() && shouldSend) {
								shouldSend = false;
								
								SocketChannel client = (SocketChannel) key.channel();
								ByteBuffer buffer = ByteBuffer.allocate(4);
								
								buffer.putInt(myScore);
								buffer.flip();
								
								client.write(buffer);
							}
						}
					}
					
					selector.close();
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		thread.start();
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