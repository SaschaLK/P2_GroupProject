package game;

public enum AccuracyRating {
	MARVELOUS(320),
	PERFECT(300),
	GREAT(200),
	GOOD(100),
	BAD(50),
	MISS(0);
	
	public final int hitValue;
	
	AccuracyRating(int hitValue) {
		this.hitValue = hitValue;
	}
}
