package game;

import javax.swing.JButton;
import javax.swing.JDialog;

public class SongSelectDialog extends JDialog{
	
	public SongSelectDialog(String title, boolean modal){
		
		JButton test = new JButton("hallo");
		add(test);
		
		setSize(100, 100);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setVisible(true);
	}
}
