package game;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Key {
	/**
	 * The number of this key
	 */
	private final int keyNum;
	
	/**
	 * The ID of the key on the keyboard as defined in KeyEvent
	 */
	private final int keyId;
	
	/**
	 * Whether this key is pressed right now
	 */
	private boolean down = false;
	
	/**
	 * When the key started to be held down
	 */
	private long downTime = 0L;
	
	/**
	 * When a slider start was hit the rating for that hit will be saved here
	 */
	private AccuracyRating sliderStartRating;
	
	/**
	 * The Clip object for the hitsound for this key
	 */
	private Clip hitsound;

	public Key(int keyNum, int keyId) {
		this.keyNum = keyNum;
		this.keyId = keyId;
		
		try {
			hitsound = AudioSystem.getClip();
			hitsound.open(AudioSystem.getAudioInputStream(new File("hitsound.wav").getAbsoluteFile()));
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}

	public boolean isDown() {
		return down;
	}

	/**
	 *  Sets the state of this key and notes the downTime
	 */
	public void setDown(boolean down) {
		this.down = down;
		
		if(this.down) downTime = System.currentTimeMillis();
	}

	public long getDownTime() {
		return downTime;
	}
	
	public AccuracyRating getSliderStartRating() {
		return sliderStartRating;
	}

	public void setSliderStartRating(AccuracyRating sliderStartRating) {
		this.sliderStartRating = sliderStartRating;
	}
	
	public void playHitsound() {
		if(hitsound.isRunning()) {
			hitsound.stop();
		}
		hitsound.setFramePosition(0);
		hitsound.start();
	}

	public int getKeyId() {
		return keyId;
	}

	public int getKeyNum() {
		return keyNum;
	}
}
