package game;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;


public class MyJFrame extends JFrame{
	private MyJPanel panel;
	private JLabel pscore = new JLabel("Your score:");
	private JMenuBar menu = new JMenuBar();
	private JPanel panelmenu = new JPanel();
	
	public MyJFrame(){
		
		setLayout(new BorderLayout());
		panel = new MyJPanel();
		setJMenuBar(menu);
		panelmenu.add(pscore);
		add(panelmenu,BorderLayout.NORTH);
		add(panel,BorderLayout.CENTER);
		setSize(245,650);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	public void updateView (ArrayList<Notes> list) {
		panel.updatePanel(list);
		setVisible(true);

	}
	public void setScore(int score) {
		
		pscore.setText("Your Score: "+score);
			
		setVisible(true);
	}
	
	
}
