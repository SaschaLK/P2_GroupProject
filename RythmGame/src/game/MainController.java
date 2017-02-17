
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

	private NotesList list = new NotesList();
	private int time = 0;
	private Hitbox hitbox = new Hitbox();
	private int score = 0;
	private boolean running=false;
	private int color = 0;
	private Thread t1 = new Thread();
	private Thread t2 = new Thread();
	private Thread t3 = new Thread();
	private Thread t4 = new Thread();
	private String songFile;
	private Clip clip ;
	private SongSelectDialog ssDialog;
	private JButton youChoseServer = new JButton("Server");
	private JButton youChoseClient = new JButton("Client");
	private JButton OkButton = new JButton("Okay");
	private JDialog selectDialog;
	private JDialog inputDialog;
	private int port = 1234;
	private String ip = "127.0.0.1";
	private JTextField ipAdresseTextField = new JTextField("",20);
	private JTextField portTextField = new JTextField("",20);

	private File file;
	
	// Server
	private boolean areYouTheServer = false;
	private ISocket socket;

	public MainController(MyJFrame view) {

		this.view = view;
		timer = new Timer(5, listener -> update());
		view.getStart().addActionListener(listener -> {
			if(!running){
			selectSong();
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
				ip = ipAdresseTextField.getText();
				port = Integer.valueOf(portTextField.getText());
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

			@Override
			public void keyPressed(KeyEvent e) {
				t1 = new Thread(new Runnable() {

					@Override
					public void run() {
						if (e.getKeyCode() == KeyEvent.VK_D) {
//							checkh0();
							try(FileWriter fw = new FileWriter(file, true);
									BufferedWriter bw = new BufferedWriter(fw)) {
								bw.write("0");
								bw.newLine();
								bw.write(Integer.toString(time));
								bw.newLine();
								System.out.println(time);
							} catch (Exception e2) {
								e2.printStackTrace();
							}
						}

					}
				});
				t2 = new Thread(new Runnable() {

					@Override
					public void run() {
						if (e.getKeyCode() == KeyEvent.VK_F) {
//							checkh1();
							try(FileWriter fw = new FileWriter(file, true);
									BufferedWriter bw = new BufferedWriter(fw)) {
								bw.write("1");
								bw.newLine();
								bw.write(Integer.toString(time));
								bw.newLine();
								System.out.println(time);
							} catch (Exception e2) {
								e2.printStackTrace();
							}
						}

					}
				});
				t3 = new Thread(new Runnable() {

					@Override
					public void run() {
						if (e.getKeyCode() == KeyEvent.VK_J) {
//							checkh2();
							try(FileWriter fw = new FileWriter(file, true);
									BufferedWriter bw = new BufferedWriter(fw)) {
								bw.write("2");
								bw.newLine();
								bw.write(Integer.toString(time));
								bw.newLine();
								System.out.println(time);
							} catch (Exception e2) {
								e2.printStackTrace();
							}
						}

					}
				});
				t4 = new Thread(new Runnable() {

					@Override
					public void run() {
						if (e.getKeyCode() == KeyEvent.VK_K) {
//							checkh3();
							try(FileWriter fw = new FileWriter(file, true);
									BufferedWriter bw = new BufferedWriter(fw)) {
								bw.write("3");
								bw.newLine();
								bw.write(Integer.toString(time));
								bw.newLine();
								System.out.println(time);
							} catch (Exception e2) {
								e2.printStackTrace();
							}
						}

					}
				});
				t1.start();
				t2.start();
				t3.start();
				t4.start();
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
		time++;
		color++;
		list.checkTime(time);
		list.moveNotes();
		if (color == 30) {
			view.getPanel().setColor(Color.BLUE, 1);
			view.getPanel().setColor(Color.BLUE, 2);
			view.getPanel().setColor(Color.BLUE, 3);
			view.getPanel().setColor(Color.BLUE, 4);

		}
		view.updateView(list.getNotesList());

	}

	private void checkh0() {
		for (int i = 0; i < list.getNotesList().size(); i++) {
			if (hitbox.hit0(list.getNotesList().get(i).getNote())) {
				score += 10;
				view.getPanel().setColor(Color.green, 1);
				color = 0;
				list.remove(list.getNotesList().get(i));
				view.setScore(score);
			}
		}
	}

	private void checkh1() {
		for (int i = 0; i < list.getNotesList().size(); i++) {
			if (hitbox.hit1(list.getNotesList().get(i).getNote())) {
				score += 10;
				view.getPanel().setColor(Color.green, 2);
				color = 0;
				list.remove(list.getNotesList().get(i));
				view.setScore(score);
			}
		}
	}

	private void checkh2() {
		for (int i = 0; i < list.getNotesList().size(); i++) {
			if (hitbox.hit2(list.getNotesList().get(i).getNote())) {
				score += 10;
				view.getPanel().setColor(Color.green, 3);
				color = 0;
				list.remove(list.getNotesList().get(i));
				view.setScore(score);
			}
		}
	}

	private void checkh3() {
		for (int i = 0; i < list.getNotesList().size(); i++) {
			if (hitbox.hit3(list.getNotesList().get(i).getNote())) {
				score += 10;
				view.getPanel().setColor(Color.green, 4);
				color = 0;
				list.remove(list.getNotesList().get(i));
				view.setScore(score);
			}
		}
	}
	public void reset(){
		timer.stop();
		score=0;
		time=0;		
		clip.stop();
		running=false;
		setStartButton();
	}
	public void playSound() {
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(songFile).getAbsoluteFile());
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);			
			clip.start();			
		} catch (Exception ex) {
			System.out.println("Error with playing sound.");
			ex.printStackTrace();
		}
	}
	

	public void selectSong() {
		ssDialog = new SongSelectDialog(null, "Choose a song", true, this, list.getSong());
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

	public void setFile(String file) {
		this.songFile = file;
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
}
