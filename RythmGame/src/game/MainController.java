
package game;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.server.ServerCloneException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class MainController {
	private MyJFrame view;
	private Timer timer;

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
	public static boolean K1Down = false;
	public static boolean K2Down = false;
	public static boolean K3Down = false;
	public static boolean K4Down = false;
	
	// Song
	private Song selectedSong;
	private long songStartTime;
	
	private int approachRate = 8;
	private int difficulty = 8;
	
	private double score = 0;
	private int bonus = 0;
	private int combo = 0;
	
	private AccuracyRating lastRating;
	private long timeLastRating;
	
	private Clip hitsound;
	
	private List<Note> hitNotes;
	
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
		view.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_D) {
						K1Down = true;
						playHitsound();
						
						noteHit(0);
					}
					if (e.getKeyCode() == KeyEvent.VK_F) {
						K2Down = true;
						playHitsound();
						
						noteHit(1);
					}
					if (e.getKeyCode() == KeyEvent.VK_J) {
						K3Down = true;
						playHitsound();
						
						noteHit(2);
					}
					if (e.getKeyCode() == KeyEvent.VK_K) {
						K4Down = true;
						playHitsound();
						
						noteHit(3);
					}
				
				if(e.getKeyCode() == KeyEvent.VK_F3) {
					approachRate++;
				}
				if(e.getKeyCode() == KeyEvent.VK_F4) {
					approachRate--;
				}
			}

			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_D) {
					K1Down = false;
				}
				if (e.getKeyCode() == KeyEvent.VK_F) {
					K2Down = false;
				}
				if (e.getKeyCode() == KeyEvent.VK_J) {
					K3Down = false;
				}
				if (e.getKeyCode() == KeyEvent.VK_K) {
					K4Down = false;
				}
			}

			public void keyTyped(KeyEvent e) {
				
			}
		});

	}
	
	public void playHitsound() {
		try {
			Clip hitsound = AudioSystem.getClip();
			hitsound.open(AudioSystem.getAudioInputStream(new File("hitsound.wav").getAbsoluteFile()));
			hitsound.start();
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void update() {
		long time = System.currentTimeMillis() - songStartTime;
		
		List<List<Note>> lanes = selectedSong.getNotes(time, approachRate);
		
		view.updateView(this, lanes, selectedSong.getCurrentTiming(time, true), time, approachRate);
		
		for(int i = 0; i < 4; i++) {
			Note note = selectedSong.getNotes(i).stream().filter(x -> !hitNotes.contains(x)).findFirst().orElse(null);
			
			if(note == null) continue;
			
			if(note.getTime() - (System.currentTimeMillis() - songStartTime) < -(151 - (3 * difficulty))) {
				hitNotes.add(note);
				
				setCombo(0);
				bonus = 0;
				
				setLastRating(AccuracyRating.MISS);
				setTimeLastRating(System.currentTimeMillis());
			}
		}
	}
	
	private void noteHit(int i) {
		Note note = null;
		
		for(Note n : selectedSong.getNotes(i)) {
			if(!hitNotes.contains(n)) {
				note = n;
				break;
			}
		}
		
		long error = Math.abs((System.currentTimeMillis() - songStartTime) - note.getTime());
		
		AccuracyRating acc = getAccuracyForError(error);
		
		if(acc == null) return;
		
		hitNotes.add(note);
		
		setLastRating(acc);
		setTimeLastRating(System.currentTimeMillis());
		
		if(acc != AccuracyRating.MISS) {
			setCombo(getCombo() + 1);
		}
		else {
			setCombo(0);
		}
		
		int hitValue = 0;
		int hitBonus = 0;
		int hitBonusValue = 0;
		
		switch(acc) {
		case MARVELOUS:
			hitValue = 320;
			hitBonusValue = 32;
			hitBonus = 2;
			break;
		case PERFECT:
			hitValue = 300;
			hitBonusValue = 16;
			hitBonus = 1;
			break;
		case GREAT:
			hitValue = 200;
			hitBonusValue = 16;
			hitBonus = -8;
			break;
		case GOOD:
			hitValue = 100;
			hitBonusValue = 8;
			hitBonus = -24;
			break;
		case BAD:
			hitValue = 50;
			hitBonusValue = 4;
			hitBonus = -44;
			break;
		case MISS:
			hitValue = 0;
			hitBonusValue = 0;
			bonus = 0;
		}
		
		bonus = bonus + hitBonus;
		if(bonus > 100) bonus = 100;
		if(bonus < 0) bonus = 0;
		
		double baseScore = ((1000000 * 0.5 / (float)selectedSong.getNoteCount()) * (hitValue / 320));
		double bonusScore = ((1000000 * 0.5 / (float)selectedSong.getNoteCount()) * (hitBonusValue * Math.sqrt(bonus) / 320));
		
		score += baseScore + bonusScore;
		
		view.setScore((int) score);
	}
	
	private AccuracyRating getAccuracyForError(long error) {
		if(error <= 16) return AccuracyRating.MARVELOUS; 
		if(error <= 64 - (3 * difficulty)) return AccuracyRating.PERFECT; 
		if(error <= 97 - (3 * difficulty)) return AccuracyRating.GREAT; 
		if(error <= 127 - (3 * difficulty)) return AccuracyRating.GOOD; 
		if(error <= 151 - (3 * difficulty)) return AccuracyRating.BAD; 
		
		return null;
	}
	
	public void reset(){
		hitNotes = new ArrayList<Note>();
		timer.stop();
		selectedSong.stop();
		running=false;
		setStartButton();
	}
	
	public void playSong() {
		try {
			hitNotes = new ArrayList<Note>();
			score = 0;
			setCombo(0);
			
			getSelectedSong().play();
			songStartTime = System.currentTimeMillis();
			timer.start();
			running=true;
			
			view.requestFocus();
			
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

	public long getTimeLastRating() {
		return timeLastRating;
	}

	public void setTimeLastRating(long timeLastRating) {
		this.timeLastRating = timeLastRating;
	}

	public AccuracyRating getLastRating() {
		return lastRating;
	}

	public void setLastRating(AccuracyRating lastRating) {
		this.lastRating = lastRating;
	}

	public int getCombo() {
		return combo;
	}

	public void setCombo(int combo) {
		this.combo = combo;
	}
}
