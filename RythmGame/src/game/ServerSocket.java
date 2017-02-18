package game;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;

class ServerSocket implements ISocket {
	private int clientScore;

	private volatile ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
	
	private byte[] remainingBuffer = new byte[0];
	
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
					
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					
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
									
									controller.getView().getMLabel().setText("Player connected! Choose a map!");
								}
							}
							// If the client send something receive it here
							if(key.isReadable()) {
								SocketChannel client = (SocketChannel) key.channel();
								
								int read = client.read(buffer) + remainingBuffer.length;
								
								int readCursor = 0;
								byte[] data = ByteBuffer.allocate(read).put(remainingBuffer).put((ByteBuffer) buffer.flip()).array();
								
								buffer.clear();
								
								while(read > 0) {
									int msgLength = byteArrayToInt(Arrays.copyOfRange(data, readCursor, readCursor += 4));
									
									if(read - readCursor < msgLength) {
										readCursor -= 4;
										
										remainingBuffer = Arrays.copyOfRange(data, readCursor, read);
										break;
									}
									
									int msgId = byteArrayToInt(Arrays.copyOfRange(data, readCursor, readCursor += 4));
									
									if(msgId == 0) {
										clientScore = byteArrayToInt(Arrays.copyOfRange(data, readCursor, readCursor += 4));
										
										controller.getView().setRemoteScore(clientScore);
									}
								}
							}
							// Sending the score of all other clients to all clients
							if(key.isWritable()) {
								synchronized(sendBuffer)  {
									SocketChannel client = (SocketChannel) key.channel();
									
									sendBuffer.flip();
									
									client.write(sendBuffer);
									
									sendBuffer.clear();
								}
							}
						}
					}
					
					selector.close();
					socket.close();
				} catch (IOException e) {
					if(!controller.isPlaying()) {
						controller.getView().getMLabel().setText("Connection failed! Try again later!");
					}
					else {
						controller.getView().getMPlayer().setText(controller.getView().getMPlayer().getText().split(" \\| ")[0]);
					}
					
					controller.closeSocket();
					
					new Timer().schedule(new TimerTask() {
						public void run() {
							controller.getView().getMLabel().setText("");
							
							if(!controller.isPlaying()) {
								controller.getView().getMLabel().setVisible(false);
								controller.getView().getMPlayer().setVisible(true);
							}
						}
					}, 5000);
				}
			}
		});
		
		thread.start();
	}
	
	public void sendScore(int score) {
		synchronized(sendBuffer) {
			sendBuffer.putInt(8);
			sendBuffer.putInt(0);
			sendBuffer.putInt(score);
		}
	}
	
	public void sendMapName(String name, String difficulty) {
		synchronized(sendBuffer) {
			sendBuffer.putInt(name.length() + difficulty.length() + 4 + 1);
			sendBuffer.putInt(1);
			sendBuffer.put((name+":"+difficulty).getBytes());
		}
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