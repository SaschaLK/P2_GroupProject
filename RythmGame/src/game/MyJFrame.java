package game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

public class MyJFrame extends JFrame {
	private MyJPanel panel = new MyJPanel();
	private JLabel pscore = new JLabel("");
	private JMenuBar menu = new JMenuBar();
	private JPanel panelmenu = new JPanel();
	private JPanel menuGrid = new JPanel(new GridLayout(2,1));
	private JButton start = new JButton("Play Game");
	private JButton mPlayer = new JButton("Multiplayer");

	public MyJFrame() {
		setLayout(new BorderLayout());
		setJMenuBar(menu);
		panelmenu.add(pscore);
		panelmenu.add(start);
		menuGrid.add(panelmenu);
		menuGrid.add(mPlayer);
		add(menuGrid, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);
		
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
		setVisible(true);
	}

	public void setScore(int score) {
		pscore.setText(score);

		setVisible(true);
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
}
