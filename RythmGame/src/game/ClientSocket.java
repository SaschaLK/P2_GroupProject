package game;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;

public class ClientSocket implements ISocket {
	private int serverScore = 0;

	private volatile ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
	
	private byte[] remainingBuffer = new byte[0];
	
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
					
					controller.getView().getMLabel().setText("You connected! Waiting for the host...");
					controller.getView().getStart().setEnabled(false);

					ByteBuffer buffer = ByteBuffer.allocate(1024);
					
					while(!shouldClose) {
						selector.select();
						
						Set<SelectionKey> keys = selector.selectedKeys();
						
						for(SelectionKey key : keys) {
							if(key.isReadable()) {
								int read = socket.read(buffer) + remainingBuffer.length;
								
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
										serverScore = byteArrayToInt(Arrays.copyOfRange(data, readCursor, readCursor += 4));
										
										controller.getView().setRemoteScore(serverScore);
									}
									if(msgId == 1) {
										String mapInfo = new String(Arrays.copyOfRange(data, readCursor, readCursor += msgLength - 4));
										
										controller.setAuto(mapInfo.contains("auto"));
										
										controller.startPlay(mapInfo.split(":")[0], mapInfo.split(":")[1]);
									}
								}
							}
							
							synchronized(sendBuffer) {
								if(key.isWritable() && sendBuffer.position() > 0) {
									
									sendBuffer.flip();
									
									socket.write(sendBuffer);
									
									sendBuffer.clear();
								}
							}
						}
					}
					
					socket.close();
				} catch (IOException e) {
					if(!controller.isPlaying()) {
						controller.getView().getMLabel().setText("Connection failed! Try again later!");
					}
					else {
						controller.getView().getMPlayer().setText(controller.getView().getMPlayer().getText().split(" \\| ")[0]);
					}
					
					controller.getView().getStart().setEnabled(true);
					
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
		}).start();
	}
	
	public void sendScore(int score) {
		synchronized(sendBuffer) {
			sendBuffer.putInt(8);
			sendBuffer.putInt(0);
			sendBuffer.putInt(score);
		}
	}

	public void sendMapInfo(String name, String difficulty, String mods) {
		throw new IllegalStateException();
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