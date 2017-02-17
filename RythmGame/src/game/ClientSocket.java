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
	
	private SocketChannel socket;
	
	private boolean shouldClose;
	
	public ClientSocket(String ip, int port) {
		try {
			System.out.println("Connecting to server on "+ip+":"+port+"...");
			
			Selector selector = Selector.open();
			
			socket = SocketChannel.open(new InetSocketAddress(ip, port));
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
						
						System.out.println("Score: "+serverScore);
					}
					
					if(key.isWritable()) {
						ByteBuffer buffer = ByteBuffer.allocate(4);
						buffer.putInt(88); // TODO: send score like this in a loop
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