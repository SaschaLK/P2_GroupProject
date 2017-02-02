package game;


import java.util.ArrayList;

public class NotesList {
	private ArrayList<Notes> list = new ArrayList<>();
	
	public NotesList(){
		list.add(new Notes(0));
//		list.add(new Notes(1));
//		list.add(new Notes(2));
//		list.add(new Notes(3));
	}
	public void moveNotes(){
		for(Notes notes :list){
			notes.moveNote();
		}
		
	}
	public ArrayList<Notes> getNotesList(){
		return list;
	}
	public void setList(ArrayList<Notes> nlist){
		list=nlist;
	}
	public void checkTime(int i){
		if(i==100){
			list.add(new Notes(1));
		}
		else if(i==200){
			list.add(new Notes(3));
		}
		else if(i==300){
			list.add(new Notes(2));
		}
		
	}
}
