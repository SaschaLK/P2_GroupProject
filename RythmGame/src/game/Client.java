import java.net.*;

import javax.swing.Timer;

import java.io.*;

public class Client {

	private int i = 0;

	private void question() {
		try {
			i++;
			Socket socket = new Socket("localhost", 6000);
			PrintWriter ausgabe = new PrintWriter(socket.getOutputStream(), true);
			ausgabe.println(i);
			InputStreamReader portLeser = new InputStreamReader(socket.getInputStream());
			BufferedReader eingabe = new BufferedReader(portLeser);
			String nachricht = eingabe.readLine();
			System.out.println("Antwort vom Server: " + nachricht);
			socket.close();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

}