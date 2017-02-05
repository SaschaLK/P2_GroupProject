package game;

import java.util.ArrayList;

public class Song {
	
	private ArrayList<Notes> list = new ArrayList<>();
	public Song(){
		
		fml();
		
	}
	public void fml(){
		for(int i=0;i<20;i++){
			list.add(new Notes(0 + (int)(Math.random() * ((4 - 0) + 1))));
			list.get(i).setTime(i*100);
		}		
	}
	public ArrayList<Notes> getCurrentSong(){
		return list;
	}
}
