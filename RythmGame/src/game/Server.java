import java.net.*;
import java.io.*;

class Server {
	public static void main(String[] args) throws IOException {
		ServerSocket socket = new ServerSocket(6000);
		while (true) {
			Socket serverSocket = socket.accept();
			System.out.println("Server: Connection received from " + socket.getInetAddress().getHostName());
			InputStreamReader in = new InputStreamReader(serverSocket.getInputStream());
			BufferedReader eingabe = new BufferedReader(in);
			String nachricht = eingabe.readLine();
			PrintWriter ausgabe = new PrintWriter(serverSocket.getOutputStream(), true);
			ausgabe.println(nachricht);
			ausgabe.close();
		}
	}
}