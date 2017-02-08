package game;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class SongSelectDialog extends JDialog {

	private JButton easy = new JButton("Easy Song");
	private JButton medium = new JButton("Medium Song");
	private JButton hard = new JButton("Hard Song");

	private JLabel easyLabel = new JLabel("Easy");
	private JLabel mediumLabel = new JLabel("Medium");
	private JLabel hardLabel = new JLabel("Hard");
	private String songName;

	private MainController controller;

	public SongSelectDialog(Frame owner, String title, boolean modal, MainController controller) {
		super(owner, title, modal);

		this.controller = controller;

		setLayout(new GridLayout(3, 1));

		add(easyLabel);
		add(easy);
		add(mediumLabel);
		add(medium);
		add(hardLabel);
		add(hard);

		easy.addActionListener(new SongSelectActionListener(this, "fml.wav"));
		medium.addActionListener(new SongSelectActionListener(this, "Kalimba.wav"));
		hard.addActionListener(new SongSelectActionListener(this, "Sleep_Away.wav"));

		pack();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setVisible(true);
	}

	private class SongSelectActionListener implements ActionListener {

		private SongSelectDialog dialog;

		public SongSelectActionListener(SongSelectDialog dialog, String songName) {
			this.dialog = dialog;
			dialog.setSongName(songName);
		}

		public void actionPerformed(ActionEvent e) {
			controller.setFile(dialog.getSongName());
			controller.playSound();
			dialog.setVisible(false);
			controller.getView().requestFocus();
		}
	}

	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		this.songName = songName;
	}

}
