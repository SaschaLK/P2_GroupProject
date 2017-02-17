package game;

public class TimingPoint {
	private final int offset;
	private final float timePerBeat;
	
	private final boolean inherited;
	
	public TimingPoint(int offset, float timePerBeat, boolean inherited) {
		this.offset = offset;
		this.timePerBeat = timePerBeat;
		this.inherited = inherited;
	}

	public int getOffset() {
		return offset;
	}

	public float getTimePerBeat() {
		return timePerBeat;
	}

	public boolean isInherited() {
		return inherited;
	}
}
