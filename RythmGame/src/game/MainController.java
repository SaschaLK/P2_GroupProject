
package game;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.server.ServerCloneException;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class MainController {
	private MyJFrame view;
	private Timer timer;

	private int score = 0;
	private boolean running=false;
	private int color = 0;
	private Thread t = new Thread();
	private SongSelectDialog ssDialog;
	private JButton youChoseServer = new JButton("Server");
	private JButton youChoseClient = new JButton("Client");
	private JButton OkButton = new JButton("Okay");
	private JDialog selectDialog;
	private JDialog inputDialog;
	private JTextField ipAdresseTextField = new JTextField("",20);
	private JTextField portTextField = new JTextField("",20);

	private File file;
	
	// Keys
	
	// Song
	private Song selectedSong;
	private long songStartTime;
	
	// Server
	private boolean areYouTheServer = false;
	private ISocket socket;

	public MainController(MyJFrame view) {

		this.view = view;
		timer = new Timer(1, listener -> update());
		view.getStart().addActionListener(listener -> {
			if(!running){
				openSongSelectionDialog();
			}
			else{				
				reset();
			}

		});
		view.getMPlayer().addActionListener(listener -> {
			if(!running){
				selectMultiplayerRole();
			}
			else{				
				reset();
			}
		});
		youChoseServer.addActionListener(listener ->{
			areYouTheServer = true;
			startServerDialog();
			
		});
		youChoseClient.addActionListener(listener ->{
			areYouTheServer = false;
			startClientDialog();
		});
		OkButton.addActionListener(listener ->{
			inputDialog.setVisible(false);
			try {
				String ip = ipAdresseTextField.getText().trim();
				int port = Integer.valueOf(portTextField.getText().trim());
				
				if(ip.isEmpty()) ip = "localhost";
				
				System.out.println(ip + "  " + port + "  " + areYouTheServer);
				
				if(socket != null) socket.close();
				
				if(areYouTheServer) {
					socket = new ServerSocket(port);
				}
				else {
					socket = new ClientSocket(ip, port);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		});


		file = new File("test.txt");

		// Sollte in der Lage sein 2 Noten zu erfassen Thread
		view.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_D) {
					
				}
				if (e.getKeyCode() == KeyEvent.VK_F) {
					
				}
				if (e.getKeyCode() == KeyEvent.VK_J) {
					
				}
				if (e.getKeyCode() == KeyEvent.VK_K) {
					
				}
			}
		});

	}

	public void moveNotes() {
		timer.start();
		running=true;
		view.requestFocus();
		setStartButton();
	}

	private void update() {
		color++;
		
		long time = System.currentTimeMillis() - songStartTime;
		
		List<List<Note>> lanes = selectedSong.getNotes(time, 8);
		
		if (color == 30) {
			view.getPanel().setColor(Color.BLUE, 1);
			view.getPanel().setColor(Color.BLUE, 2);
			view.getPanel().setColor(Color.BLUE, 3);
			view.getPanel().setColor(Color.BLUE, 4);

		}
		
		view.updateView(lanes, time, 8);
	}

	private void checkh0() {
//		for (int i = 0; i < list.getNotesList().size(); i++) {
//			if (hitbox.hit0(list.getNotesList().get(i).getNote())) {
//				score += 10;
//				view.getPanel().setColor(Color.green, 1);
//				color = 0;
//				list.remove(list.getNotesList().get(i));
//				view.setScore(score);
//			}
//		}
	}

	private void checkh1() {
//		for (int i = 0; i < list.getNotesList().size(); i++) {
//			if (hitbox.hit1(list.getNotesList().get(i).getNote())) {
//				score += 10;
//				view.getPanel().setColor(Color.green, 2);
//				color = 0;
//				list.remove(list.getNotesList().get(i));
//				view.setScore(score);
//			}
//		}
	}

	private void checkh2() {
//		for (int i = 0; i < list.getNotesList().size(); i++) {
//			if (hitbox.hit2(list.getNotesList().get(i).getNote())) {
//				score += 10;
//				view.getPanel().setColor(Color.green, 3);
//				color = 0;
//				list.remove(list.getNotesList().get(i));
//				view.setScore(score);
//			}
//		}
	}

	private void checkh3() {
//		for (int i = 0; i < list.getNotesList().size(); i++) {
//			if (hitbox.hit3(list.getNotesList().get(i).getNote())) {
//				score += 10;
//				view.getPanel().setColor(Color.green, 4);
//				color = 0;
//				list.remove(list.getNotesList().get(i));
//				view.setScore(score);
//			}
//		}
	}
	
	public void reset(){
		timer.stop();
		score=0;
		selectedSong.stop();
		running=false;
		setStartButton();
	}
	
	public void playSong() {
		try {
			getSelectedSong().play();
			timer.start();
			running=true;
			view.requestFocus();
			
			songStartTime = System.currentTimeMillis();
		} catch (Exception ex) {
			System.out.println("Error with playing sound.");
			ex.printStackTrace();
		}
	}
	

	public void openSongSelectionDialog() {
		ssDialog = new SongSelectDialog(null, "Choose a song", true, this);
	}
	
	public void selectMultiplayerRole() {
		selectDialog = new JDialog(view, "Server or Client", true);
		selectDialog.setSize(300, 300);
		selectDialog.setLayout(new GridLayout(2, 1));
		selectDialog.setLocationRelativeTo(view);
		selectDialog.add(youChoseServer);
		selectDialog.add(youChoseClient);
		selectDialog.setVisible(true);

	}
	public void startServerDialog(){
		selectDialog.setVisible(false);
		inputDialog = new JDialog(view, "Enter server data", true);
		inputDialog.setSize(300, 200);
		inputDialog.setLocationRelativeTo(view);
		inputDialog.setLayout(new GridLayout(2, 1));
		JLabel enterPort = new JLabel("Enter the port");
		JPanel clientDialogPortPanel = new JPanel();
		clientDialogPortPanel.add(enterPort);
		clientDialogPortPanel.add(portTextField);
		
		inputDialog.add(clientDialogPortPanel);
		inputDialog.add(OkButton);
		
		inputDialog.setVisible(true);
	}
	public void startClientDialog(){
		selectDialog.setVisible(false);
		inputDialog = new JDialog(view, "Enter server data", true);
		inputDialog.setSize(300, 400);
		inputDialog.setLocationRelativeTo(view);
		inputDialog.setLayout(new GridLayout(3, 1));
		JLabel enterIp = new JLabel("Enter the IP address");
		JLabel enterPort = new JLabel("Enter the port");
		JPanel clientDialogIpPanel = new JPanel();
		JPanel clientDialogPortPanel = new JPanel();
		clientDialogIpPanel.add(enterIp);
		clientDialogIpPanel.add(ipAdresseTextField);		
		clientDialogPortPanel.add(enterPort);
		clientDialogPortPanel.add(portTextField);
		
		inputDialog.add(clientDialogIpPanel);
		inputDialog.add(clientDialogPortPanel);
		inputDialog.add(OkButton);
		
		inputDialog.setVisible(true);
	}

	public MyJFrame getView() {
		return view;
	}
	
	public void setStartButton(){
		if(running){
			String a = "stop";
		view.setButton(a);
		}
		else{
			String b = "Play Game";
			view.setButton(b);
			
		}	
	}

	public Song getSelectedSong() {
		return selectedSong;
	}

	public void setSelectedSong(Song selectedSong) {
		this.selectedSong = selectedSong;
	}
}
