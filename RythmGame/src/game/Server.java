import java.net.*;
import java.io.*;

class Server {
		ServerSocket socket = new ServerSocket(6000);
		
		try{
			Socket serverSocket = socket.accept();
			System.out.println("Server: Connection received from " + socket.getInetAddress().getHostName());
			InputStreamReader in = new InputStreamReader(serverSocket.getInputStream());
			BufferedReader eingabe = new BufferedReader(in);
			String nachricht = eingabe.readLine();
			PrintWriter ausgabe = new PrintWriter(serverSocket.getOutputStream(), true);
			ausgabe.println(nachricht);
			ausgabe.close();
				
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
			
	}
}