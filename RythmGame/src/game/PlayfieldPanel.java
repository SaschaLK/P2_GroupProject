package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.JPanel;

public class PlayfieldPanel extends JPanel {
	private Rectangle background = new Rectangle(0, 0, 230, 600);

	private Color inactiveColor = Color.BLUE;
	private Color activeColor = Color.GREEN;
	
	private List<List<Note>> notes;
	private long time;
	private int approachRate;
	private TimingPoint timingPoint;

	private MainController controller;
	
	public PlayfieldPanel() {
		
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.lightGray);
		g.fillRect(background.x, background.y, background.width, background.height);

		if(controller == null) return;
		
		long timeMillis = time;
		float pixelsPerMillisecond = background.height / (float) ((10 - approachRate) * 150 + 450);
		
		if(notes != null) {
			long linesEnd = time + (10 - approachRate) * 150 + 450;

			// Drawing lines every half beat
			g.setColor(Color.DARK_GRAY);
			for(float offset = timingPoint.getOffset(); offset < linesEnd; offset += timingPoint.getTimePerBeat() / 2) {
				int delta = (int) ((offset - timeMillis) * pixelsPerMillisecond);
				
				g.fillRect(0, -delta + 600, 230, 1);
			}
			
			// Drawing notes and sliders
			g.setColor(Color.BLACK);
			for (int i = 0; i < notes.size(); i++) {
				for(Note note : notes.get(i)) {
					if(controller.getPlay().GetHitNotes().contains(note)) continue;
					
					int delta = (int) ((note.getTime() - timeMillis) * pixelsPerMillisecond);
					
					if(note instanceof NoteSlider) {
						int duration = ((NoteSlider) note).getDuration();

						g.setColor(Color.WHITE);
						g.fillRect(60 * i + 6, -delta + 600 - note.getNote().height - Math.round(duration * pixelsPerMillisecond), note.getNote().width - 12, Math.round(duration * pixelsPerMillisecond));
						
						g.setColor(Color.BLACK);
						g.fillRect(60 * i, -delta + 600 - note.getNote().height - Math.round(duration * pixelsPerMillisecond), note.getNote().width, note.getNote().height);
					}
					
					g.fillRect(60 * i, -delta + 600 - note.getNote().height, note.getNote().width, note.getNote().height);
				}
			}
		}

		// Draw keys
		for(int i = 0; i < 4; i++) {
			g.setColor(controller.keys[i] != null && controller.keys[i].isDown() ? activeColor : inactiveColor);
			g.fillRect(60 * i, background.height - 10, 50, 10);
		}
		
		// Draw rating and combo
		if(controller.getPlay() != null  && controller.getPlay().getLastRating() != null && System.currentTimeMillis() - controller.getPlay().getTimeLastRating() <= 1000) {
			Font font = new Font("Arial", 0, 24);
			g.setFont(font);
			g.setColor(Color.ORANGE);
			
			g.drawString(controller.getPlay().getLastRating().name(), background.width / 2 - g.getFontMetrics().stringWidth(controller.getPlay().getLastRating().name()) / 2, 200);
			if(controller.getPlay().getCombo() > 0) g.drawString(controller.getPlay().getCombo()+"", background.width / 2 - g.getFontMetrics().stringWidth(controller.getPlay().getCombo()+"") / 2, 230);
		}
	}

	public void updatePanel(List<List<Note>> nlist, TimingPoint timingPoint, long time, int approachRate) {
		this.notes = nlist;
		this.timingPoint = timingPoint;
		this.time = time;
		this.approachRate = approachRate;
		repaint();
	}
	
	public void setController(MainController controller) {
		this.controller = controller;
	}
}
