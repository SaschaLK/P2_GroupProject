package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.JPanel;

public class MyJPanel extends JPanel {
	private Hitbox hitbox = new Hitbox();
	private Rectangle background = new Rectangle(0, 0, 230, 600);

	private Color hcolor0 = Color.BLUE;
	private Color hcolor1 = Color.BLUE;
	private Color hcolor2 = Color.BLUE;
	private Color hcolor3 = Color.BLUE;
	
	private List<List<Note>> notes;
	private long time;
	private int approachRate;

	public MyJPanel() {
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.lightGray);
		g.fillRect(background.x, background.y, background.width, background.height);

		g.setColor(hcolor0);
		g.fillRect(hitbox.getHitbox0().x, hitbox.getHitbox0().y, hitbox.getHitbox0().width, hitbox.getHitbox0().height);

		g.setColor(hcolor1);
		g.fillRect(hitbox.getHitbox1().x, hitbox.getHitbox1().y, hitbox.getHitbox1().width, hitbox.getHitbox1().height);

		g.setColor(hcolor2);
		g.fillRect(hitbox.getHitbox2().x, hitbox.getHitbox2().y, hitbox.getHitbox2().width, hitbox.getHitbox2().height);

		g.setColor(hcolor3);
		g.fillRect(hitbox.getHitbox3().x, hitbox.getHitbox3().y, hitbox.getHitbox3().width, hitbox.getHitbox3().height);

		long timeMillis = time;
		float pixelsPerMillisecond = background.height / (float) ((10 - approachRate) * 150 + 450);
		
		if(notes == null) return;
		
		g.setColor(Color.BLACK);
		for (int i = 0; i < notes.size(); i++) {
			for(Note note : notes.get(i)) {
				int delta = (int) ((note.getTime() - timeMillis) * pixelsPerMillisecond);
				
				g.fillRect(60 * i, -delta + 480, note.getNote().width, note.getNote().height);
			}
		}
	}

	public void updatePanel(List<List<Note>> nlist, long time, int approachRate) {
		this.notes = nlist;
		this.time = time;
		this.approachRate = approachRate;
		repaint();
	}

	public void setColor(Color color, int i) {
		if (i == 1) {
			hcolor0 = color;
		} else if (i == 2) {
			hcolor1 = color;
		} else if (i == 3) {
			hcolor2 = color;
		} else if (i == 4) {
			hcolor3 = color;
		}
		repaint();
	}

}
