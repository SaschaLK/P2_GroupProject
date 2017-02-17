import java.net.*;

import javax.swing.Timer;

import java.io.*;

public class Client {

	private int i = 0;
	private Timer t = new Timer(10, listener -> update());

	public OsuClient3() {
		t.start();
	}

	private void update() {
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

	public static void main(String[] args) {
		new OsuClient3();
	}
}