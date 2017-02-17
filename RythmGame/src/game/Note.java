package game;

import java.awt.Rectangle;

public class Note {
	private Rectangle note;
	private int time;

	public Note(int time) {
		note = new Rectangle(0, 0, 50, 15);
		this.time = time;
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
}
