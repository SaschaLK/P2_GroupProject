package game;

import java.awt.Rectangle;

public class Notes {
	private Rectangle note;
	private int time;
	private int lane;

	public Notes(int x) {
		note = new Rectangle(0, 0, 50, 20);
		time = 0;
		lane = x;
		setNoteLane(x);
	}

	public Rectangle getNote() {
		return note;
	}

	public void setNote(int x) {
		note.y = note.y + x;
	}

	public void moveNote() {
		note.y = note.y + 1;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int x) {
		time = x;
	}

	public void setNoteLane(int x) {
		if (x == 0) {
			note = new Rectangle(0, 0, 50, 20);
		} else if (x == 1) {
			note = new Rectangle(60, 0, 50, 20);
		} else if (x == 2) {
			note = new Rectangle(120, 0, 50, 20);
		} else if (x == 3) {
			note = new Rectangle(180, 0, 50, 20);
		}
	}

	public int getLane() {
		return lane;
	}
}
