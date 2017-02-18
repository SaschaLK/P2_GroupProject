package game;

import java.util.HashMap;
import java.util.Map;

public class Play {
	private Map<AccuracyRating, Integer> hitCounts = new HashMap<AccuracyRating, Integer>();
	
	private MainController controller;
	
	private final Song song;
	private final String difficulty;
	
	private double score = 0;
	private int bonus = 0;
	private int combo = 0;
	
	private double averageError = 0;
	
	public Play(MainController controller, Song song, String difficulty) {
		this.controller = controller;
		this.song = song;
		this.difficulty = difficulty;
		
		hitCounts.put(AccuracyRating.MARVELOUS, 0);
		hitCounts.put(AccuracyRating.PERFECT, 0);
		hitCounts.put(AccuracyRating.GREAT, 0);
		hitCounts.put(AccuracyRating.GOOD, 0);
		hitCounts.put(AccuracyRating.BAD, 0);
		hitCounts.put(AccuracyRating.MISS, 0);
	}
	
	public AccuracyRating addHit(long error) {
		AccuracyRating acc = getAccuracyForError(Math.abs(error));
		
		if(acc == null) return null;
		
		if(getTotalHits() == 0) {
			averageError = error;
		}
		else {
			averageError = (averageError * getTotalHits() + error) / (double) (getTotalHits() + 1);
		}
		
		hitCounts.put(acc, hitCounts.containsKey(acc) ? hitCounts.get(acc) + 1 : 1);
		
		if(acc != AccuracyRating.MISS) {
			combo++;
		}
		else {
			combo = 0;
		}
		
		int hitValue = 0;
		int hitBonus = 0;
		int hitBonusValue = 0;
		
		switch(acc) {
		case MARVELOUS:
			hitValue = 320;
			hitBonusValue = 32;
			hitBonus = 2;
			break;
		case PERFECT:
			hitValue = 300;
			hitBonusValue = 16;
			hitBonus = 1;
			break;
		case GREAT:
			hitValue = 200;
			hitBonusValue = 16;
			hitBonus = -8;
			break;
		case GOOD:
			hitValue = 100;
			hitBonusValue = 8;
			hitBonus = -24;
			break;
		case BAD:
			hitValue = 50;
			hitBonusValue = 4;
			hitBonus = -44;
			break;
		case MISS:
			hitValue = 0;
			hitBonusValue = 0;
			bonus = 0;
		}
		
		bonus = bonus + hitBonus;
		if(bonus > 100) bonus = 100;
		if(bonus < 0) bonus = 0;
		
		double baseScore = ((1000000 * 0.5 / (float)song.getNoteCount(difficulty)) * (hitValue / 320));
		double bonusScore = ((1000000 * 0.5 / (float)song.getNoteCount(difficulty)) * (hitBonusValue * Math.sqrt(bonus) / 320));
		
		if(baseScore + bonusScore > 0) {
			score += baseScore + bonusScore;
			
			if(controller.getSocket() != null) controller.getSocket().sendScore((int) score);
		}

		controller.getView().setScore((int) score);
		controller.getView().setAccuracy(getAccuracy());
		
		return acc;
	}
	
	private AccuracyRating getAccuracyForError(long error) {
		if(error <= 16) return AccuracyRating.MARVELOUS; 
		if(error <= 64 - (3 * controller.getHitDifficulty())) return AccuracyRating.PERFECT; 
		if(error <= 97 - (3 * controller.getHitDifficulty())) return AccuracyRating.GREAT; 
		if(error <= 127 - (3 * controller.getHitDifficulty())) return AccuracyRating.GOOD; 
		if(error <= 151 - (3 * controller.getHitDifficulty())) return AccuracyRating.BAD;
		
		if(error >= (188 - (3 * controller.getHitDifficulty())) * 2) return null;
		
		return AccuracyRating.MISS;
	}
	
	public float getAccuracy() {
		int totalPoints = hitCounts.get(AccuracyRating.BAD) * 50 +
						  hitCounts.get(AccuracyRating.GOOD) * 100 +
						  hitCounts.get(AccuracyRating.GREAT) * 200 +
						  hitCounts.get(AccuracyRating.PERFECT) * 300 +
						  hitCounts.get(AccuracyRating.MARVELOUS) * 300;
		
		int totalHits = getTotalHits();
		
		return totalHits == 0 ? 100.0F : (int) ((totalPoints / (float) (totalHits * 300)) * 1000) / 10.0f;
	}
	
	public int getTotalHits() {
		return hitCounts.get(AccuracyRating.BAD) +
		       hitCounts.get(AccuracyRating.GOOD) +
		       hitCounts.get(AccuracyRating.GREAT) +
		       hitCounts.get(AccuracyRating.PERFECT) +
		       hitCounts.get(AccuracyRating.MARVELOUS) +
		       hitCounts.get(AccuracyRating.MISS);
	}
	
	public Song getSong() {
		return song;
	}

	public int getCombo() {
		return combo;
	}

	public String getDifficulty() {
		return difficulty;
	}
}
