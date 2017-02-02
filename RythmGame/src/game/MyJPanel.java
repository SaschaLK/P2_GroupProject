package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JPanel;


public class MyJPanel extends JPanel{
	private Hitbox hitbox = new Hitbox();
	private Rectangle background = new Rectangle(0, 0, 230, 600);
	private NotesList list = new NotesList();
	private ArrayList <Notes> nlist = new ArrayList<>();
	public MyJPanel(){
		repaint();
	}
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		g.setColor(Color.lightGray);
		g.fillRect(background.x, background.y, background.width, background.height);
		
//		g.setColor(Color.PINK);
//		g.fillRect(note.getNote().x, note.getNote().y, note.getNote().width, note.getNote().height);
//	
		g.setColor(Color.BLUE);
		g.fillRect(hitbox.getHitbox0().x, hitbox.getHitbox0().y, hitbox.getHitbox0().width, hitbox.getHitbox0().height);
		
		g.setColor(Color.BLUE);
		g.fillRect(hitbox.getHitbox1().x, hitbox.getHitbox1().y, hitbox.getHitbox1().width, hitbox.getHitbox1().height);
	
		g.setColor(Color.BLUE);
		g.fillRect(hitbox.getHitbox2().x, hitbox.getHitbox2().y, hitbox.getHitbox2().width, hitbox.getHitbox2().height);
	
		g.setColor(Color.BLUE);
		g.fillRect(hitbox.getHitbox3().x, hitbox.getHitbox3().y, hitbox.getHitbox3().width, hitbox.getHitbox3().height);
		
		g.setColor(Color.PINK);
		for (Notes note : list.getNotesList()) {
			g.fillRect(note.getNote().x, note.getNote().y, note.getNote().width, note.getNote().height);
		}
	
	}
	public void updatePanel(ArrayList<Notes> nlist) {
		this.nlist=nlist;
		list.setList(nlist);
		repaint();
	}	
	
}
