package game;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Song {

	private ArrayList<Notes> list = new ArrayList<>();
	private File file = new File("test.txt");

	public Song() {
		
		playSongMap();
//		fml();
//		dragon();

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
	
	public void playSongMap(){
		int notesCount = countNotes(file)/2;
		try(FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);) {
			for(int i = 0; i < notesCount; i++){
				list.add(new Notes(Integer.parseInt(br.readLine())));
				list.get(i).setTime(Integer.parseInt(br.readLine()));
			}
			System.out.println(notesCount);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
//		list.add(new Notes(3));
//		list.get(0).setTime(100);
	}
	
//To-Do jeder song hat eigene void...?
	public void fml() {
//		for (int i = 0; i < 20; i++) {
//			list.add(new Notes(0 + (int) (Math.random() * ((4 - 0) + 1))));
//			list.get(i).setTime(i * 100);
//		}
	}

	public void dragon() {
//		int a=0;
//		int t=100;
//		for(int i=0;i<20;i++){
//			
//			a=0 +(int)(Math.random() * ((4 - 0) + 1));
//			if(i==2){
//				t=t;
//			}
//			else {
//				t+=100;
//			}
//			list.add(new Notes(0 + (int)(Math.random() * ((4 - 0) + 1))));
//			list.get(i).setTime(t);
//		}
	}

	public ArrayList<Notes> getCurrentSong() {
		return list;
	}
}
