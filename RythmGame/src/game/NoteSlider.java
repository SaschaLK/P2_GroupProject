package game;

public class NoteSlider extends Note {
	private int duration;
	
	public NoteSlider(int time, int duration) {
		super(time);

		this.duration = duration;
	}
	
	public float getLengthInBeats(float beatsPerMillis) {
		return duration / beatsPerMillis;
	}

	public int getDuration() {
		return duration;
	}

	public boolean containsTime(long time) {
		return time >= this.getTime() && time <= this.getTime() + duration;
	}
}
