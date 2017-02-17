package game;

import java.awt.Rectangle;

public class Hitbox {
	private Rectangle hitbox0;
	private Rectangle hitbox1;
	private Rectangle hitbox2;
	private Rectangle hitbox3;

	public Hitbox() {
		hitbox0 = new Rectangle(0, 530, 50, 10);
		hitbox1 = new Rectangle(60, 530, 50, 10);
		hitbox2 = new Rectangle(120, 530, 50, 10);
		hitbox3 = new Rectangle(180, 530, 50, 10);
	}

	public Rectangle getHitbox0() {
		return hitbox0;
	}

	public Rectangle getHitbox1() {
		return hitbox1;
	}

	public Rectangle getHitbox2() {
		return hitbox2;
	}

	public Rectangle getHitbox3() {
		return hitbox3;
	}

	public boolean hit0(Rectangle rec) {
		if (hitbox0.intersects(rec)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean hit1(Rectangle rec) {
		if (hitbox1.intersects(rec)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean hit2(Rectangle rec) {
		if (hitbox2.intersects(rec)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean hit3(Rectangle rec) {
		if (hitbox3.intersects(rec)) {
			return true;
		} else {
			return false;
		}
	}
}
