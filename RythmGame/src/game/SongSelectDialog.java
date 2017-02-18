package game;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class SongSelectDialog extends JDialog {

	private JButton easy = new JButton("Halozy - Deconstruction Star");
	private JButton medium = new JButton("The Hives - Hate to Say I Told You So");
	private JButton hard = new JButton("Dream - Night of Fire");

	private JLabel easyLabel = new JLabel("Casual");
	private JLabel mediumLabel = new JLabel("Pleb");
	private JLabel hardLabel = new JLabel("Standard");
	
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

		easy.addActionListener(new SongSelectActionListener(this, "Deconstruction Star"));
		medium.addActionListener(new SongSelectActionListener(this, "The_Hives_-_Hate_to_Say_I_Told_You_So"));
		hard.addActionListener(new SongSelectActionListener(this, "Night_of_Fire"));

		pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 3 - this.getSize().height / 2);

		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setVisible(true);
	}

	private class SongSelectActionListener implements ActionListener {

		private SongSelectDialog dialog;
		private String songName;

		public SongSelectActionListener(SongSelectDialog dialog, String songName) {
			this.dialog = dialog;
			this.songName = songName;
		}

		public void actionPerformed(ActionEvent e) {
			dialog.setVisible(false);
			controller.getView().requestFocus();
			
			controller.setSelectedSong(Song.songList.get(songName));
			controller.playSong();
			controller.getView().getStart().setVisible(false);
			controller.getView().GetScoreText().setVisible(true);
			controller.getView().GetScoreText().setText("0");
		}
	}
}
