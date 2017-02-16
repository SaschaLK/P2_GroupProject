package game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class Song {

	private ArrayList<Notes> list = new ArrayList<>();
	private String fileName;
	private File file;

	public Song() {
	}
	
	private int countNotes(File file){
		int count = 0;
		try (FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);){
			String temp = br.readLine();
			while(temp != null){
				count++;
				temp = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	public void readSongNotes(){
		file = new File(fileName);
		int notesCount = countNotes(file)/2;
		try(FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);) {
			for(int i = 0; i < notesCount; i++){
				list.add(new Notes(Integer.parseInt(br.readLine())));
				list.get(i).setTime(Integer.parseInt(br.readLine()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Notes> getCurrentSong() {
		return list;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		readSongNotes();
	}
	
}
