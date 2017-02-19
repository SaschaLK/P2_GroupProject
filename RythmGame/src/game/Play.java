package game;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineEvent.Type;

/**
 * This class represents a single playthrough through a song and
 * holds all the progress, difficulty and scoring data.
 */
public class Play {
	private MainController controller;
	
	// Song info
	/**
	 * The song object to play the map
	 */
	private final Song song;
	/**
	 * Describes the map that is played for this song
	 */
	private final String difficulty;

	/**
	 * The time in milliseconds when this song started playing
	 */
	private long songStartTime;
	
	/**
	 * The list of hit notes
	 */
	private List<Note> hitNotes = new LinkedList<Note>();
	/**
	 * The amount of hit notes per lane
	 */
	private int[] noteCount = new int[4];
	
	// Difficulty parameters
	/**
	 * How fast the notes are approaching to the keys.
	 * This doesn't affect song speed.
	 */
	private int approachRate = 8;
	/**
	 * How hard it is to get accurate hits.
	 */
	private int overallDifficulty = 8;
	
	/**
	 * The list of active mods
	 */
	private Mod[] mods;
	
	// Scoring
	/**
	 * The maximum score you can reach on each map
	 */
	public static final int MAX_SCORE = 1000000;
	
	/**
	 * The score that always maxes out at MAX_SCORE
	 */
	private double score = 0;
	/**
	 * The bonus value, which will punish you for getting continuously inaccurate hits.
	 * Will start at 100 and always be between 0 and 100. To see how each rating
	 * affects this value see AccuracyRating.hitBonus
	 */
	private float bonus = 100;
	/**
	 * The amount of continuous hits, meaning no misses and sliderbreaks inbetween.
	 * 
	 * Note: Doesn't affect score at all
	 */
	private int combo = 0;
	
	/**
	 * The different amounts of AccuracyRatings added up in a map
	 */
	private Map<AccuracyRating, Integer> hitCounts = new HashMap<AccuracyRating, Integer>();
	
	/**
	 * The last rating received for your last hit. Will be displayed for a set amount of time.
	 */
	private AccuracyRating lastRating;
	private long timeLastRating;

	/**
	 * Based on this value sliderTicks will be issued which will increase combo.
	 */
	private long timeLastSliderTick;

	// Extra info
	private double averageError = 0;
	
	public Play(MainController controller, Song song, String difficulty, Mod...mods) {
		this.controller = controller;
		this.song = song;
		this.difficulty = difficulty;
		this.mods = mods;
		
		hitCounts.put(AccuracyRating.MARVELOUS, 0);
		hitCounts.put(AccuracyRating.PERFECT, 0);
		hitCounts.put(AccuracyRating.GREAT, 0);
		hitCounts.put(AccuracyRating.GOOD, 0);
		hitCounts.put(AccuracyRating.BAD, 0);
		hitCounts.put(AccuracyRating.MISS, 0);
	}
	
	/**
	 * This method will be called continuously with the lowest amount of delay possible.
	 * It will update the state of the playfield (control auto-play, discard passed notes, etc.)
	 */
	public void update(MainController controller) {
		long time = getSongTime();

		// Stop the song when the notes stop
		if(time >= song.getEndTime(getDifficulty())) {
			song.stop();
			controller.reset();
			return;
		}
		
		// Determine if this is a slidertick frame
		boolean sliderTick = false;
		
		if(timeLastSliderTick + song.getCurrentTiming(time, true).getTimePerBeat() / 4.0D <= time) {
			timeLastSliderTick += song.getCurrentTiming(time, true).getTimePerBeat() / 4.0D;
			sliderTick = true;
		}
		
		for(Key key : controller.keys) {
			int i = key.getKeyNum();
			
			Note note = null;
			
			try {
				note = song.getNotes(getDifficulty(), i).get(noteCount[i]);
			} catch (Exception e) {}
			
			if(key.getSliderStartRating() != null && key.isDown() && note != null && note instanceof NoteSlider && ((NoteSlider) note).containsTime(time)) {
				if(sliderTick) {
					incrementCombo();
					timeLastRating = System.currentTimeMillis();
				}
			}
			
			if(isModActive(Mod.AUTO) && ((note != null && note instanceof NoteSlider && getSongTime() - (note.getTime() + ((NoteSlider)note).getDuration()) >= 0 && key.getSliderStartRating() != null) || (System.currentTimeMillis() - key.getDownTime() >= 70 && key.getSliderStartRating() == null))) {
				key.setDown(false);

				if(note instanceof NoteSlider && key.getSliderStartRating() != null) {
					AccuracyRating acc = getAccuracyForError(Math.abs(getSongTime() - (note.getTime() + ((NoteSlider)note).getDuration())));
					
					addSliderHit(key.getSliderStartRating(), acc);

					processNoteHit(key, note);
					
					continue;
				}
			}
			
			if(note == null) continue;
			
			if(isModActive(Mod.AUTO) && note.getTime() - getSongTime() <= 0) {
				if(note instanceof NoteSlider && key.getSliderStartRating() != null) continue; 
				
				key.setDown(true);
				key.playHitsound();
				
				long error = note.getTime() - getSongTime();
				
				if(!(note instanceof NoteSlider)) {
					addHit(error);
					
					processNoteHit(key, note);
				}
				else {
					key.setSliderStartRating(getAccuracyForError(error));
					
					incrementCombo();
				}
				
				continue;
			}
			
			// handle missed notes
			if(note instanceof NoteSlider) {
				if(isNotePassed(note.getTime() - getSongTime()) && key.getSliderStartRating() == null) {
					key.setSliderStartRating(AccuracyRating.MISS);
					
					sliderBreak();
				}
				if(isNotePassed(note.getTime() + ((NoteSlider) note).getDuration() - getSongTime())) {
					addSliderHit(key.getSliderStartRating(), AccuracyRating.MISS);
					
					processNoteHit(key, note);
				}
			}
			else if(isNotePassed(note.getTime() - getSongTime())) {
				addHit(note.getTime() - getSongTime());
				
				processNoteHit(key, note);
			}
		}
	}
	
	/**
	 * Cleans up the hit note and prepares the next one
	 */
	private void processNoteHit(Key key, Note note) {
		hitNotes.add(note);

		noteCount[key.getKeyNum()]++;
		
		if(note instanceof NoteSlider) {
			key.setSliderStartRating(null);
		}
	}

	private boolean isNotePassed(long error) {
		return error < 0 && (getAccuracyForError(error) == AccuracyRating.MISS || getAccuracyForError(error) == null);
	}

	public boolean isModActive(Mod m) {
		for(Mod mod : mods) if(m == mod) return true;
		
		return false;
	}

	/**
	 * Starts playing the song and sets songStartTime
	 */
	public void start(MainController controller) {
		song.play(new LineListener() {
			public void update(LineEvent event) {
				if(event.getType() == Type.STOP) {
					controller.setRunning(false);
					controller.reset();
				}
			}
		});
		
		songStartTime = System.currentTimeMillis() + 40;
	}
	
	/**
	 * Renders the notes to the given frame
	 */
	public void renderPlayfield(GameFrame frame) {
		List<List<Note>> lanes = song.getNotes(getDifficulty(), getSongTime(), approachRate);
		
		frame.updateView(lanes, song.getCurrentTiming(getSongTime(), true), getSongTime(), approachRate);
	}
	
	/**
	 * Fired when a key is pressed or released
	 */
	public void keyHit(Key key) {
		int i = key.getKeyNum();
		
		Note note = null;
		
		try {
			note = song.getNotes(difficulty, i).get(noteCount[i]);
		} catch (Exception e) {}
		
		if(note == null) return;
		
		if(note instanceof NoteSlider) {
			if(key.getSliderStartRating() == null) {
				long error = getSongTime() - note.getTime();
				
				AccuracyRating acc = getAccuracyForError(Math.abs(error));
				
				if(acc == null) return;
				
				if(acc != AccuracyRating.MISS) {
					incrementCombo();
				}
				else {
					sliderBreak();
				}
				
				key.setSliderStartRating(acc);
			}
			else if(!key.isDown()) {
				long error = getSongTime() - (note.getTime() + ((NoteSlider)note).getDuration());
				
				AccuracyRating acc = getAccuracyForError(Math.abs(error));
				
				if(acc != null) {
					addSliderHit(key.getSliderStartRating(), acc);
					
					processNoteHit(key, note);
				}
				else if(error < 0) {
					key.setSliderStartRating(AccuracyRating.BAD);
					sliderBreak();
				}
			}
		}
		else {
			long error = getSongTime() - note.getTime();
			
			AccuracyRating acc = addHit(error);
			
			if(acc == null) return;

			processNoteHit(key, note);
		}
	}

	public AccuracyRating addSliderHit(AccuracyRating accStart, AccuracyRating accEnd) {
		if(accStart == AccuracyRating.MISS ^ accEnd == AccuracyRating.MISS) return addHit(AccuracyRating.BAD);
		
		return addHit(accStart.hitValue >= accEnd.hitValue ? accEnd : accStart);
	}
	
	public AccuracyRating addHit(long error) {
		AccuracyRating acc = getAccuracyForError(Math.abs(error));
		
		if(acc == null) return null;
		
		// TODO slider hits arent counted yet
		if(getTotalHits() == 0) {
			averageError = error;
		}
		else {
			averageError = (averageError * getTotalHits() + error) / (double) (getTotalHits() + 1);
		}
		
		return addHit(acc);
	}
	
	public AccuracyRating addHit(AccuracyRating acc) {
		hitCounts.put(acc, hitCounts.containsKey(acc) ? hitCounts.get(acc) + 1 : 1);
		
		if(acc != AccuracyRating.MISS) {
			incrementCombo();
		}
		else {
			combo = 0;
		}
		
		bonus = bonus + acc.hitBonus;
		if(bonus > 100) bonus = 100;
		if(bonus < 0) bonus = 0;
		
		double baseScore = ((MAX_SCORE * 0.5f / (float)song.getNoteCount(difficulty)) * (acc.hitValue / (float) 320));
		double bonusScore = ((MAX_SCORE * 0.5f / (float)song.getNoteCount(difficulty)) * (acc.hitBonusValue * Math.sqrt(bonus) / (float) 320));
		
		if(baseScore + bonusScore > 0) {
			score += baseScore + bonusScore;
			
			if(controller.getSocket() != null) controller.getSocket().sendScore((int) Math.round(score));
		}

		controller.getView().setScore((int) Math.round(score));
		controller.getView().setAccuracy(getAccuracy());
		
		lastRating = acc;
		timeLastRating = System.currentTimeMillis();
		
		return acc;
	}
	
	/**
	 *  Returns the AccuracyRating for a specific error value
	 */
	public AccuracyRating getAccuracyForError(long error) {
		error = Math.abs(error);
		
		if(error <= AccuracyRating.MARVELOUS.getError(overallDifficulty)) return AccuracyRating.MARVELOUS; 
		if(error <= AccuracyRating.PERFECT.getError(overallDifficulty)) return AccuracyRating.PERFECT; 
		if(error <= AccuracyRating.GREAT.getError(overallDifficulty)) return AccuracyRating.GREAT; 
		if(error <= AccuracyRating.GOOD.getError(overallDifficulty)) return AccuracyRating.GOOD; 
		if(error <= AccuracyRating.BAD.getError(overallDifficulty)) return AccuracyRating.BAD;
		if(error <= AccuracyRating.MISS.getError(overallDifficulty)) return AccuracyRating.MISS;
		
		return null;
	}
	
	/**
	 * Returns accuracy for the current time (dependant on how much notes were played)
	 */
	public float getAccuracy() {
		int totalPoints = hitCounts.get(AccuracyRating.BAD) * AccuracyRating.BAD.hitValue +
						  hitCounts.get(AccuracyRating.GOOD) * AccuracyRating.GOOD.hitValue +
						  hitCounts.get(AccuracyRating.GREAT) * AccuracyRating.GREAT.hitValue +
						  hitCounts.get(AccuracyRating.PERFECT) * AccuracyRating.PERFECT.hitValue +
						  hitCounts.get(AccuracyRating.MARVELOUS) * AccuracyRating.PERFECT.hitValue; // This is intended!!!
		
		int totalHits = getTotalHits();
		
		return totalHits == 0 ? 100.0F : (int) ((totalPoints / (float) (totalHits * AccuracyRating.PERFECT.hitValue)) * 10000) / 100.0f;
	}
	
	/**
	 * Returns all note hits recorded that far (including missed notes)
	 */
	public int getTotalHits() {
		return hitCounts.get(AccuracyRating.BAD) +
		       hitCounts.get(AccuracyRating.GOOD) +
		       hitCounts.get(AccuracyRating.GREAT) +
		       hitCounts.get(AccuracyRating.PERFECT) +
		       hitCounts.get(AccuracyRating.MARVELOUS) +
		       hitCounts.get(AccuracyRating.MISS);
	}
	
	public long getSongTime() {
		return System.currentTimeMillis() - songStartTime;
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

	public void sliderBreak() {
		combo = 0;
		bonus = 0;
	}

	public void incrementCombo() {
		combo++;
	}

	public int getApproachRate() {
		return approachRate;
	}

	public void setApproachRate(int approachRate) {
		this.approachRate = approachRate;
	}

	public long getTimeLastRating() {
		return timeLastRating;
	}

	public AccuracyRating getLastRating() {
		return lastRating;
	}

	public List<Note> GetHitNotes() {
		return hitNotes;
	}
}
