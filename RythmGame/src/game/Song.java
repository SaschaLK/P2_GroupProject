package game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Song {
	// Songliste
	private static final Song DECONSTRUCTION_STAR = new Song("Deconstruction Star");

	public static HashMap<String, Song> songList;
	
	static {
		songList = new HashMap<String, Song>();
		
		songList.put("Deconstruction Star", DECONSTRUCTION_STAR);
	}
	
	// Audio
	private AudioInputStream audioInputStream;
	private Clip clip;
	
	// Beatmap info
	private List<List<Note>> list;
	
	// Datei Info
	private String fileName;
	private File file;

	public Song(String fileName) {
		this.fileName = fileName;
		file = new File(fileName+".map");
		
		list = new ArrayList<List<Note>>();
		
		// add lists for lanes
		list.add(new ArrayList<Note>());
		list.add(new ArrayList<Note>());
		list.add(new ArrayList<Note>());
		list.add(new ArrayList<Note>());
		
		readSongNotes();
	}
	
	public void play() {
		try {
			audioInputStream = AudioSystem.getAudioInputStream(new File(fileName+".wav").getAbsoluteFile());
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);			
			clip.start();
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		clip.stop();
		clip = null;
	}
	
	public void readSongNotes() {
		boolean readingNotes = false;
		
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line = "";
			
			while((line = br.readLine()) != null) {
				if(readingNotes) {
					if(line.isEmpty()) {
						readingNotes = false;
					}
					else {
						String[] data = line.split(",");
						
						Note note = new Note(Integer.valueOf(data[2]));
						
						if(data[0].equals("64")) list.get(0).add(note);
						if(data[0].equals("192")) list.get(1).add(note);
						if(data[0].equals("320")) list.get(2).add(note);
						if(data[0].equals("448")) list.get(3).add(note);
					}
				}
				else {
					if(br.readLine().equals("[HitObjects]")) {
						readingNotes = true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<List<Note>> getCurrentSong() {
		return list;
	}

	public List<List<Note>> getNotes(long time, int approachRate) {
		long startShow = time - 50;
		long endShow = time + ((10 - approachRate) * 150 + 450);
		
		List<List<Note>> laneList = new ArrayList<List<Note>>();
		
		for(int i = 0; i < 4; i++) {
			List<Note> noteList = new ArrayList<Note>();
			
			for(Note note : list.get(i)) {
				if(note.getTime() >= startShow && note.getTime() <= endShow) {
					noteList.add(note);
				}
			}
			
			laneList.add(noteList);
		}
		
		return laneList;
	}
}
