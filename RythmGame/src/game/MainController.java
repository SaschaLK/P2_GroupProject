
package game;

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

public class MainController {
	private MyJFrame view;
	private Timer timer;

	private boolean running=false;
	private JComboBox<String> comboBox;
	private JButton youChoseServer = new JButton("Server");
	private JButton youChoseClient = new JButton("Client");
	private JButton OkButton = new JButton("Okay");
	private JDialog selectDialog;
	private JDialog inputDialog;
	private JDialog difficultyDialog;
	private JTextField ipAdresseTextField = new JTextField("localhost", 20);
	private JTextField portTextField = new JTextField("8888", 20);
	
	// Keys
	public static boolean[] KDown = new boolean[4];
	public static long[] KDownTime = new long[4];
	
	private AccuracyRating[] sliderStartRatings = new AccuracyRating[4];
	
	// Song
	private Play play;
	
	private Song selectedSong;
	private long songStartTime;
	
	private int approachRate = 8;
	private int difficulty = 8;
	
	private AccuracyRating lastRating;
	private long timeLastRating;
	
	private List<Note> hitNotes;
	
	private long timeLastSliderTick;
	
	// Mods
	private boolean auto = false;
	
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
			view.getMPlayer().setVisible(false);
			try {
				String ip = ipAdresseTextField.getText().trim();
				int port = Integer.valueOf(portTextField.getText().trim());
				
				if(ip.isEmpty()) ip = "localhost";
				
				System.out.println(ip + "  " + port + "  " + areYouTheServer);
				
				if(socket != null) socket.close();
				
				if(areYouTheServer) {
					socket = new ServerSocket(this, port);
					view.getMLabel().setText("Waiting for player to connect...");
				}
				else {
					socket = new ClientSocket(this, ip, port);
					view.getMLabel().setText("Connecting to server...");
				}
				view.getMLabel().setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		// Sollte in der Lage sein 2 Noten zu erfassen Thread
		view.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_D && !KDown[0] && !auto) {
					KDown[0] = true;
					playHitsound();
					
					noteHit(0, true);
				}
				if (e.getKeyCode() == KeyEvent.VK_F && !KDown[1] && !auto) {
					KDown[1] = true;
					playHitsound();
					
					noteHit(1, true);
				}
				if (e.getKeyCode() == KeyEvent.VK_J && !KDown[2] && !auto) {
					KDown[2] = true;
					playHitsound();
					
					noteHit(2, true);
				}
				if (e.getKeyCode() == KeyEvent.VK_K && !KDown[3] && !auto) {
					KDown[3] = true;
					playHitsound();
					
					noteHit(3, true);
				}
				
				if(e.getKeyCode() == KeyEvent.VK_F3) {
					approachRate++;
				}
				if(e.getKeyCode() == KeyEvent.VK_F4) {
					approachRate--;
				}
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE && running) {
					running = false;
					reset();
				}
			}

			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_D && KDown[0] && !auto) {
					KDown[0] = false;
					
					if(sliderStartRatings[0] != null) noteHit(0, false);
				}
				if (e.getKeyCode() == KeyEvent.VK_F && KDown[1] && !auto) {
					KDown[1] = false;
					
					if(sliderStartRatings[1] != null) noteHit(1, false);
				}
				if (e.getKeyCode() == KeyEvent.VK_J && KDown[2] && !auto) {
					KDown[2] = false;
					
					if(sliderStartRatings[2] != null) noteHit(2, false);
				}
				if (e.getKeyCode() == KeyEvent.VK_K && KDown[3] && !auto) {
					KDown[3] = false;
					
					if(sliderStartRatings[3] != null) noteHit(3, false);
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
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}

	private void update() {
		long time = System.currentTimeMillis() - songStartTime;

		view.updateProgress(time / (float) selectedSong.getEndTime(play.getDifficulty()));
		
		if(time >= selectedSong.getEndTime(play.getDifficulty())) {
			selectedSong.stop();
			reset();
			return;
		}
		
		List<List<Note>> lanes = selectedSong.getNotes(play.getDifficulty(), time, approachRate);
		
		view.updateView(this, lanes, selectedSong.getCurrentTiming(time, true), time, approachRate);
		
		boolean sliderTick = false;
		
		if(timeLastSliderTick + selectedSong.getCurrentTiming(time, true).getTimePerBeat() / 2.0D <= time) {
			timeLastSliderTick += selectedSong.getCurrentTiming(time, true).getTimePerBeat() / 2.0D;
			sliderTick = true;
		}
		
		for(int i = 0; i < 4; i++) {
			Note note = selectedSong.getNotes(play.getDifficulty(), i).stream().filter(x -> !hitNotes.contains(x)).findFirst().orElse(null);

			if(sliderStartRatings[i] != null && KDown[i] && note != null && note instanceof NoteSlider && ((NoteSlider) note).containsTime(time)) {
				if(sliderTick) {
					play.incrementCombo();
					setTimeLastRating(System.currentTimeMillis());
				}
			}
			
			if(auto && ((note != null && note instanceof NoteSlider && (System.currentTimeMillis() - songStartTime) - (note.getTime() + ((NoteSlider)note).getDuration()) >= 0 && sliderStartRatings[i] != null) || (System.currentTimeMillis() - KDownTime[i] >= 70 && sliderStartRatings[i] == null))) {
				KDown[i] = false;

				if(note instanceof NoteSlider && sliderStartRatings[i] != null) {
					AccuracyRating acc = play.getAccuracyForError(Math.abs((System.currentTimeMillis() - songStartTime) - (note.getTime() + ((NoteSlider)note).getDuration())));
					
					hitNotes.add(note);
						
					play.addSliderHit(sliderStartRatings[i], acc);
	
					setLastRating(acc);
						
					sliderStartRatings[i] = null;
					
					continue;
				}
			}
			
			if(note == null) continue;
			
			if(auto && note.getTime() - (System.currentTimeMillis() - songStartTime) <= 0) {
				if(note instanceof NoteSlider && sliderStartRatings[i] != null) continue; 
				
				KDown[i] = true;
				KDownTime[i] = System.currentTimeMillis();
				playHitsound();
				
				long error = note.getTime() - (System.currentTimeMillis() - songStartTime);
				
				AccuracyRating acc = null;
				
				if(!(note instanceof NoteSlider)) {
					hitNotes.add(note);
					
					acc = play.addHit(error);
				}
				else {
					acc = play.getAccuracyForError(error);
					
					sliderStartRatings[i] = acc;
					
					play.incrementCombo();
				}
				
				setLastRating(acc);
				continue;
			}
			
			// handle missed notes
			if(note instanceof NoteSlider) {
				if(note.getTime() - (System.currentTimeMillis() - songStartTime) < -(151 - (3 * difficulty)) && sliderStartRatings[i] == null) {
					sliderStartRatings[i] = AccuracyRating.MISS;
					
					play.sliderBreak();
				}
				if(note.getTime() + ((NoteSlider) note).getDuration() - (System.currentTimeMillis() - songStartTime) < -(151 - (3 * difficulty))) {
					hitNotes.add(note);
				
					AccuracyRating acc = play.addSliderHit(sliderStartRatings[i], AccuracyRating.MISS);
					
					sliderStartRatings[i] = null;
					
					setLastRating(acc);
				}
			}
			else if(note.getTime() - (System.currentTimeMillis() - songStartTime) < -(151 - (3 * difficulty))) {
				hitNotes.add(note);
				
				AccuracyRating acc = play.addHit(188 - (3 * difficulty));
				
				setLastRating(acc);
			}
		}
	}
	
	private void noteHit(int i, boolean isDownKey) {
		Note note = null;
		
		for(Note n : selectedSong.getNotes(play.getDifficulty(), i)) {
			if(!hitNotes.contains(n)) {
				note = n;
				break;
			}
		}
		
		if(note == null) return;
		
		if(note instanceof NoteSlider) {
			if(sliderStartRatings[i] == null) {
				long error = (System.currentTimeMillis() - songStartTime) - note.getTime();
				
				AccuracyRating acc = play.getAccuracyForError(Math.abs(error));
				
				if(acc == null) return;
				
				if(acc != AccuracyRating.MISS) {
					play.incrementCombo();
				}
				else {
					play.sliderBreak();
				}
				
				sliderStartRatings[i] = acc;
				
				setLastRating(acc);
			}
			else if(!isDownKey) {
				long error = (System.currentTimeMillis() - songStartTime) - (note.getTime() + ((NoteSlider)note).getDuration());
				
				AccuracyRating acc = play.getAccuracyForError(Math.abs(error));
				
				if(acc != null) {
					hitNotes.add(note);
					
					play.addSliderHit(sliderStartRatings[i], acc);

					setLastRating(acc);
					
					sliderStartRatings[i] = null;
				}
				else if(error < 0) {
					sliderStartRatings[i] = AccuracyRating.BAD;
					play.sliderBreak();
				}
			}
		}
		else {
			long error = (System.currentTimeMillis() - songStartTime) - note.getTime();
			
			AccuracyRating acc = play.addHit(error);
			
			if(acc == null) return;
			
			hitNotes.add(note);
			
			setLastRating(acc);
		}
	}
	
	public void reset(){
		timer.stop();
		selectedSong.stop();
		
		hitNotes = new ArrayList<Note>();
		lastRating = null;
		view.updateProgress(0.0f);
		
		view.GetScoreText().setVisible(false);
		view.getStart().setVisible(true);
		
		if(socket != null) {
			view.getMLabel().setVisible(false);
			view.getMPlayer().setVisible(true);
		}
	}

	public void startPlaying(String songName, String difficulty) {
		if(socket != null) {
			if(socket instanceof ServerSocket) socket.sendMapInfo(songName, difficulty, auto ? "auto" : "");

			socket.sendScore(0);
		}
		
		this.getView().requestFocus();
		
		this.setSelectedSong(new Song(new File("./maps/"+songName+"/"+songName+".wav")));
		
		this.playSong(difficulty);
		
		this.getView().getStart().setVisible(false);
		this.getView().GetScoreText().setVisible(true);
		this.getView().GetScoreText().setText("0");
		this.getView().getMPlayer().setVisible(false);
		this.getView().getMLabel().setVisible(true);
		this.getView().setAccuracy(play.getAccuracy());
	}
	
	public void playSong(String difficulty) {
		try {
			hitNotes = new ArrayList<Note>();
			play = new Play(this, selectedSong, difficulty);
			
			getSelectedSong().play(new LineListener() {
				public void update(LineEvent event) {
					if(event.getType() == Type.STOP) {
						running = false;
						reset();
					}
				}
			});
			
			songStartTime = System.currentTimeMillis() + 40;
			timer.start();
			running=true;
			
			view.requestFocus();
			
		} catch (Exception ex) {
			System.out.println("Error with playing sound.");
			ex.printStackTrace();
		}
	}
	

	public void openSongSelectionDialog() {
		JFileChooser fileChooser = new JFileChooser();
		
		fileChooser.setFileFilter(new FileFilter() {
			public String getDescription() {
				return "mapped WAV files";
			}
			
			public boolean accept(File f) {
				return f.getName().endsWith(".wav") || f.isDirectory();
			}
		});
		
		fileChooser.setCurrentDirectory(new File("./maps"));
		
		fileChooser.showOpenDialog(view);
		
		if(fileChooser.getSelectedFile() == null) return;
		
		selectedSong = new Song(fileChooser.getSelectedFile());
		
		startDifficultyDialog();
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
	public void startDifficultyDialog(){
		difficultyDialog = new JDialog(view, "Choose difficulty...", true);
		difficultyDialog.setSize(300, 150);
		difficultyDialog.setLocationRelativeTo(view);
		difficultyDialog.setLayout(new GridLayout(3, 1));

		comboBox = new JComboBox<String>();
		
		for(String difficulty : selectedSong.getDifficulties()) comboBox.addItem(difficulty);
		
		JButton button = new JButton("OK");
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				difficultyDialog.setVisible(false);
				
				startPlaying(selectedSong.getName(), (String) comboBox.getSelectedItem());
			}
		});
		
		JCheckBox checkBox = new JCheckBox();
		checkBox.setText("Auto");
		checkBox.setSelected(auto);
		checkBox.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				auto = !auto;
			}
		});
		
		JPanel panel1 = new JPanel(new GridBagLayout());
		panel1.add(comboBox);

		JPanel panel2 = new JPanel(new GridBagLayout());
		panel2.add(checkBox);
		
		JPanel panel3 = new JPanel(new GridBagLayout());
		panel3.add(button);
		
		difficultyDialog.add(panel1);
		difficultyDialog.add(panel2);
		difficultyDialog.add(panel3);
		
		difficultyDialog.setVisible(true);
	}

	public MyJFrame getView() {
		return view;
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
		setTimeLastRating(System.currentTimeMillis());
	}

	public void closeSocket() {
		socket.close();
		socket = null;
	}

	public boolean isPlaying() {
		return running;
	}

	public ISocket getSocket() {
		return socket;
	}

	public Play getPlay() {
		return play;
	}

	public int getHitDifficulty() {
		return difficulty;
	}

	public List<Note> GetHitNotes() {
		return hitNotes;
	}

	public void setAuto(boolean auto) {
		this.auto = auto;
	}
}
