package game;

import java.util.ArrayList;

public class Song {

	private ArrayList<Notes> list = new ArrayList<>();

	public Song() {
		
		playSongMap();
//		fml();
//		dragon();

	}

	public void playSongMap(){
		list.add(new Notes(3));
		list.get(0).setTime(100);
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
