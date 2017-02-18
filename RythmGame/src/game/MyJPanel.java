package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.JPanel;

public class MyJPanel extends JPanel {
	private Hitbox hitbox = new Hitbox();
	private Rectangle background = new Rectangle(0, 0, 230, 600);

	private Color inactiveColor = Color.BLUE;
	private Color activeColor = Color.GREEN;
	
	private List<List<Note>> notes;
	private long time;
	private int approachRate;
	private TimingPoint timingPoint;

	private MainController controller;
	
	public MyJPanel() {
		
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.lightGray);
		g.fillRect(background.x, background.y, background.width, background.height);

		long timeMillis = time;
		float pixelsPerMillisecond = background.height / (float) ((10 - approachRate) * 150 + 450);
		
		if(notes != null) {
			long linesEnd = time + (10 - approachRate) * 150 + 450;

			g.setColor(Color.DARK_GRAY);
			for(float offset = timingPoint.getOffset(); offset < linesEnd; offset += timingPoint.getTimePerBeat() / 2) {
				int delta = (int) ((offset - timeMillis) * pixelsPerMillisecond);
				
				g.fillRect(0, -delta + 530, 230, 1);
			}
			
			g.setColor(Color.BLACK);
			for (int i = 0; i < notes.size(); i++) {
				for(Note note : notes.get(i)) {
					if(controller.GetHitNotes().contains(note)) continue;
					
					int delta = (int) ((note.getTime() - timeMillis) * pixelsPerMillisecond);
					
					g.fillRect(60 * i, -delta + 530 - note.getNote().height, note.getNote().width, note.getNote().height);
				}
			}
		}

		g.setColor(MainController.K1Down ? activeColor : inactiveColor);
		g.fillRect(hitbox.getHitbox0().x, hitbox.getHitbox0().y, hitbox.getHitbox0().width, hitbox.getHitbox0().height);

		g.setColor(MainController.K2Down ? activeColor : inactiveColor);
		g.fillRect(hitbox.getHitbox1().x, hitbox.getHitbox1().y, hitbox.getHitbox1().width, hitbox.getHitbox1().height);

		g.setColor(MainController.K3Down ? activeColor : inactiveColor);
		g.fillRect(hitbox.getHitbox2().x, hitbox.getHitbox2().y, hitbox.getHitbox2().width, hitbox.getHitbox2().height);

		g.setColor(MainController.K4Down ? activeColor : inactiveColor);
		g.fillRect(hitbox.getHitbox3().x, hitbox.getHitbox3().y, hitbox.getHitbox3().width, hitbox.getHitbox3().height);
		
		if(controller != null && controller.getLastRating() != null && System.currentTimeMillis() - controller.getTimeLastRating() <= 1000) {
			Font font = new Font("Arial", 0, 24);
			g.setFont(font);
			g.setColor(Color.ORANGE);
			
			g.drawString(controller.getLastRating().name(), background.width / 2 - g.getFontMetrics().stringWidth(controller.getLastRating().name()) / 2, 200);
			if(controller.getPlay().getCombo() > 0) g.drawString(controller.getPlay().getCombo()+"", background.width / 2 - g.getFontMetrics().stringWidth(controller.getPlay().getCombo()+"") / 2, 230);
		}
	}

	public void updatePanel(MainController controller, List<List<Note>> nlist, TimingPoint timingPoint, long time, int approachRate) {
		this.controller = controller;
		this.notes = nlist;
		this.timingPoint = timingPoint;
		this.time = time;
		this.approachRate = approachRate;
		repaint();
	}
}
