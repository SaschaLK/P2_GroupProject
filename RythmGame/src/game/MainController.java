
package game;

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

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
	private GameFrame view;
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
	public Key[] keys = new Key[4];
	
	// Song
	private Play play;
	
	// Mods
	private boolean auto = false;
	
	// Server
	private boolean areYouTheServer = false;
	private ISocket socket;

	public MainController(GameFrame view) {
		this.view = view;
		
		view.setController(this);
		
		keys[0] = new Key(0, KeyEvent.VK_D);
		keys[1] = new Key(1, KeyEvent.VK_F);
		keys[2] = new Key(2, KeyEvent.VK_J);
		keys[3] = new Key(3, KeyEvent.VK_K);
		
		timer = new Timer(1, listener -> update());
		
		view.getStart().addActionListener(listener -> {
			if(!isRunning()){
				openSongSelectionDialog();
			}
			else{				
				reset();
			}

		});
		view.getMPlayer().addActionListener(listener -> {
			if(!isRunning()){
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

		view.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				for(Key key : keys) {
					if (e.getKeyCode() == key.getKeyId() && !key.isDown() && !auto) {
						key.setDown(true);
						key.playHitsound();
						
						play.keyHit(key);
					}
				}
				
				if(e.getKeyCode() == KeyEvent.VK_F3) {
					play.setApproachRate(play.getApproachRate() + 1);
				}
				if(e.getKeyCode() == KeyEvent.VK_F4) {
					play.setApproachRate(play.getApproachRate() - 1);
				}
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE && isRunning()) {
					setRunning(false);
					reset();
				}
			}

			public void keyReleased(KeyEvent e) {
				for(Key key : keys) {
					if (e.getKeyCode() == key.getKeyId() && key.isDown() && !auto) {
						key.setDown(false);
						
						if(key.getSliderStartRating() != null) play.keyHit(key);
					}
				}
			}

			public void keyTyped(KeyEvent e) {
				
			}
		});
	}

	public void update() {
		if(!isRunning()) return;
		
		play.update(this);
		
		// Updating progress bar
		view.updateProgress(play.getSongTime() / (float) play.getSong().getEndTime(play.getDifficulty()));
		
		// Render the playfield
		play.renderPlayfield(view);
	}
	
	/**
	 * Resets the current play state to get ready for a new one
	 */
	public void reset(){
		timer.stop();
		
		play.getSong().stop();
		
		view.updateProgress(0.0f);
		
		view.GetScoreText().setVisible(false);
		view.getStart().setVisible(true);
		
		if(socket == null) {
			view.getMLabel().setVisible(false);
			view.getMPlayer().setVisible(true);
		}
	}

	public void startPlay(String songName, String difficulty, Mod...mods) {
		// If Multiplayer send info
		if(socket != null) {
			if(socket instanceof ServerSocket) socket.sendMapInfo(songName, difficulty, auto ? "auto" : "");

			socket.sendScore(0);
		}
		
		// Start play
		play = new Play(this, new Song(new File("./maps/"+songName+"/"+songName+".wav")), difficulty, mods);
		
		play.start(this);
		
		timer.start();
		setRunning(true);
		
		// Setting up controls
		this.getView().requestFocus();
		this.getView().getStart().setVisible(false);
		this.getView().GetScoreText().setVisible(true);
		this.getView().GetScoreText().setText("0");
		this.getView().getMPlayer().setVisible(false);
		this.getView().getMLabel().setVisible(true);
		this.getView().setAccuracy(play.getAccuracy());
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
		
		Song selectedSong = new Song(fileChooser.getSelectedFile());
		
		startDifficultyDialog(selectedSong);
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
	
	/**
	 * Opens a dialog that let's the player set difficulty and starts the play
	 */
	public void startDifficultyDialog(Song selectedSong){
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
				
				startPlay(selectedSong.getName(), (String) comboBox.getSelectedItem(), auto ? Mod.AUTO : null);
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

	// getter/setter
	public GameFrame getView() {
		return view;
	}

	public void closeSocket() {
		socket.close();
		socket = null;
	}

	public boolean isPlaying() {
		return isRunning();
	}

	public ISocket getSocket() {
		return socket;
	}

	public Play getPlay() {
		return play;
	}

	public void setAuto(boolean auto) {
		this.auto = auto;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
}
