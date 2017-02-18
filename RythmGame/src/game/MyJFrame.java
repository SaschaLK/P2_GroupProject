package game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

public class MyJFrame extends JFrame {
	private MyJPanel panel = new MyJPanel();
	private JLabel pscore = new JLabel("");
	private JLabel mLabel = new JLabel("");
	private JMenuBar menu = new JMenuBar();
	private JPanel topMenu = new JPanel();
	private JPanel bottomMenu = new JPanel();
	private JPanel menuGrid = new JPanel(new GridLayout(2,1));
	private JButton start = new JButton("Play Game");
	private JButton mPlayer = new JButton("Multiplayer");

	public MyJFrame() {
		setLayout(new BorderLayout());
		setJMenuBar(menu);
		topMenu.add(pscore);
		topMenu.add(start);
		bottomMenu.add(mPlayer);
		bottomMenu.add(mLabel);
		menuGrid.add(topMenu);
		menuGrid.add(bottomMenu);
		add(menuGrid, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);
		
		panel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		
		mLabel.setVisible(false);
		pscore.setVisible(false);
		
		pscore.setFont(new Font("Arial", 0, 22));
		
		start.setSize(70, start.getHeight());

		setSize(245, 650);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 3 - this.getSize().height / 2);

		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void updateView(MainController controller, List<List<Note>> list, TimingPoint timingPoint, long time, int approachRate) {
		panel.updatePanel(controller, list, timingPoint, time, approachRate);
	}

	public void setScore(int score) {
		if(pscore.getText().contains("|")) {
			pscore.setText(score + " | " + pscore.getText().split(" \\| ")[1]);
		}
		else {
			pscore.setText(""+score);
		}
	}

	public JButton getStart() {
		return start;
	}
	public JButton getMPlayer() {
		return mPlayer;
	}

	public MyJPanel getPanel() {
		return panel;
	}
	public void setButton(String title){
		start.setText(title);
		setVisible(true);
		
	}
	public JLabel GetScoreText() {
		return pscore;
	}

	public void setRemoteScore(int remoteScore) {
		pscore.setText(pscore.getText().split(" \\| ")[0] + " | " + remoteScore);
	}

	public JLabel getMLabel() {
		return mLabel;
	}

	public void setmLabel(JLabel mLabel) {
		this.mLabel = mLabel;
	}

	public void setAccuracy(float accuracy) {
		mLabel.setText(accuracy+"%");
	}
}
