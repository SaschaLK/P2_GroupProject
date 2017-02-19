package game;

public enum AccuracyRating {
	MARVELOUS(320, 32, 2, 16),
	PERFECT(300, 32, 1, 64),
	GREAT(200, 16, -8, 97),
	GOOD(100, 8, -24, 127),
	BAD(50, 4, -44, 151),
	MISS(0, 0, -100, 188);
	
	public final int hitValue;
	public final int hitBonusValue;
	public final int hitBonus;
	private final int error;
	
	AccuracyRating(int hitValue, int hitBonusValue, int hitBonus, int error) {
		this.hitValue = hitValue;
		this.hitBonusValue = hitBonusValue;
		this.hitBonus = hitBonus;
		this.error = error;
	}
	
	public int getError(int overallDifficulty) {
		return this == MARVELOUS ? error : error - 3 * overallDifficulty;
	}
}
