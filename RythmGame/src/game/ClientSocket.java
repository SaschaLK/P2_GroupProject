package game;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import java.io.*;

public class ClientSocket implements ISocket {
	private SocketChannel socket;
	
	public ClientSocket(String ip, int port) {
		try {
			System.out.println("Connecting to server on "+ip+":"+port+"...");
			
			socket = SocketChannel.open(new InetSocketAddress(ip, port));
			
			System.out.println("Connected to server!");
			
			ByteBuffer buffer = ByteBuffer.allocate(4);
			buffer.putInt(88); // TODO: send score like this in a loop
			socket.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}