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

	private JButton easy = new JButton("Easy Song");
	private JButton medium = new JButton("Medium Song");
	private JButton hard = new JButton("Dream - Night of Fire");

	private JLabel easyLabel = new JLabel("Casual");
	private JLabel mediumLabel = new JLabel("Pleb");
	private JLabel hardLabel = new JLabel("Standard");
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
		hard.addActionListener(new SongSelectActionListener(this, "Night_of_Fire.wav"));

		pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 3 - this.getSize().height / 2);

		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setVisible(true);
	}

	private class SongSelectActionListener implements ActionListener {

		private SongSelectDialog dialog;
		private String songNameTemp;

		public SongSelectActionListener(SongSelectDialog dialog, String songName) {
			this.dialog = dialog;
			songNameTemp = songName;
		}

		public void actionPerformed(ActionEvent e) {
			controller.setFile(songNameTemp);
			controller.playSound();
			
			dialog.setVisible(false);
			controller.getView().requestFocus();
			controller.moveNotes();
			
		}
	}
}
