package game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Song {
	// Audio
	private AudioInputStream audioInputStream;
	private Clip clip;
	
	// Beatmap info
	private HashMap<String, List<List<Note>>> noteCollection;
	private List<TimingPoint> timingPoints;
	
	// Datei Info
	private String fileName;
	private Map<String, File> difficulties;

	public Song(File songFile) {
		this.fileName = songFile.getName().substring(0, songFile.getName().length() - 4).split(" \\[")[0];
		
		difficulties = new HashMap<String, File>();
		
		noteCollection = new HashMap<String, List<List<Note>>>();
		
		File[] fileList = new File(songFile.getParent()).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(fileName) && name.endsWith(".map");
			}
		});
		
		for(File file : fileList) {
			difficulties.put(file.getName().split("\\[")[1].split("\\]")[0], file);
			
			List<List<Note>> laneList = new ArrayList<List<Note>>();
			
			// add lists for lanes
			laneList.add(new ArrayList<Note>());
			laneList.add(new ArrayList<Note>());
			laneList.add(new ArrayList<Note>());
			laneList.add(new ArrayList<Note>());
			
			noteCollection.put(file.getName().split("\\[")[1].split("\\]")[0], laneList);
		}
		
		timingPoints = new ArrayList<TimingPoint>();
		
		readSongNotes();
	}
	
	public void play(LineListener listener) {
		try {
			audioInputStream = AudioSystem.getAudioInputStream(new File("./maps/"+fileName+"/"+fileName+".wav").getAbsoluteFile());
			clip = AudioSystem.getClip();
			clip.addLineListener(listener);
			clip.open(audioInputStream);
			clip.start();
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		if(clip == null) return;
		
		clip.stop();
		clip = null;
	}
	
	public void readSongNotes() {
		for(String difficulty : noteCollection.keySet()) {
			boolean readingTiming = false;
			boolean readingNotes = false;
			
			try(BufferedReader br = new BufferedReader(new FileReader(difficulties.get(difficulty)))) {
				String line = "";
				
				while((line = br.readLine()) != null) {
					if(readingTiming) {
						if(line.isEmpty()) {
							readingTiming = false;
						}
						else {
							String[] data = line.split(",");
							
							timingPoints.add(new TimingPoint(Integer.valueOf(data[0]), Float.valueOf(data[1]), data[6].equals("0")));
						}
					}
					else {
						if(line.equals("[TimingPoints]")) {
							readingTiming = true;
						}
					}
					
					if(readingNotes) {
						if(line.trim().isEmpty()) {
							readingNotes = false;
						}
						else {
							String[] data = line.split(",");
							
							Note note = new Note(Integer.valueOf(data[2]));
							
							if(data[0].equals("64")) noteCollection.get(difficulty).get(0).add(note);
							if(data[0].equals("192")) noteCollection.get(difficulty).get(1).add(note);
							if(data[0].equals("320")) noteCollection.get(difficulty).get(2).add(note);
							if(data[0].equals("448")) noteCollection.get(difficulty).get(3).add(note);
						}
					}
					else {
						if(line.equals("[HitObjects]")) {
							readingNotes = true;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public TimingPoint getCurrentTiming(long time, boolean nonInherited) {
		for(int i = timingPoints.size() - 1; i >= 0; i--) {
			if(i == 0) return timingPoints.get(0);
			
			if(nonInherited && timingPoints.get(i - 1).isInherited()) continue;
			
			if(timingPoints.get(i).getOffset() > time && timingPoints.get(i-1).getOffset() <= time) return timingPoints.get(i-1);
		}
		
		return null;
	}
	
	public long getEndTime(String difficulty) {
		List<List<Note>> laneNotes = noteCollection.get(difficulty);
		
		return 5000 + Math.max(Math.max(laneNotes.get(0).get(laneNotes.get(0).size() - 1).getTime(), laneNotes.get(1).get(laneNotes.get(1).size() - 1).getTime()), Math.max(laneNotes.get(2).get(laneNotes.get(2).size() - 1).getTime(), laneNotes.get(3).get(laneNotes.get(3).size() - 1).getTime()));
	}

	public List<List<Note>> getCurrentSong(String difficultyName) {
		return noteCollection.get(difficultyName);
	}

	public List<List<Note>> getNotes(String difficultyName, long time, int approachRate) {
		long startShow = time - 50;
		long endShow = time + ((10 - approachRate) * 150 + 450) + 50;
		
		List<List<Note>> laneList = new ArrayList<List<Note>>();
		
		for(int i = 0; i < 4; i++) {
			List<Note> noteList = new ArrayList<Note>();
			
			for(Note note : noteCollection.get(difficultyName).get(i)) {
				if(note.getTime() >= startShow && note.getTime() <= endShow) {
					noteList.add(note);
				}
			}
			
			laneList.add(noteList);
		}
		
		return laneList;
	}

	public List<Note> getNotes(String difficultyName, int i) {
		return noteCollection.get(difficultyName).get(i);
	}

	public int getNoteCount(String difficultyName) {
		return noteCollection.get(difficultyName).get(0).size() + noteCollection.get(difficultyName).get(1).size() + noteCollection.get(difficultyName).get(2).size() + noteCollection.get(difficultyName).get(3).size();
	}

	public boolean isPlaying() {
		return clip.isRunning();
	}

	public String getName() {
		return fileName;
	}

	public Set<String> getDifficulties() {
		return difficulties.keySet();
	}
}
