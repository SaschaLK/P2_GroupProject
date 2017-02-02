package game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Timer;

public class MainController {
	private MyJFrame view;
	private Timer timer;
//	private Notes note=new Notes();
	private NotesList list = new NotesList();
	
	private Hitbox hitbox = new Hitbox();
	private int score = 0;
	public MainController(MyJFrame view){
		this.view = view;
		timer = new Timer(5,listener -> update());
		
		view.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_D){
//		    		if(hitbox.hit0(note.getNote())){
//		    			score =+ 10;
//		    			view.setScore(score);
//		    		}
					for(Notes note : list.getNotesList()){
						if(hitbox.hit0(note.getNote())){
			    			score =+ 10;
			    			view.setScore(score);
			    		}
					}
		    	}				
			}
		});
		
		timer.start();
	}
	private void update(){
		list.moveNotes();
		view.updateView(list.getNotesList());
		
	}
	private void checkh1(){
		for(Notes note : list.getNotesList()){
			if(hitbox.hit0(note.getNote())){
    			score =+ 10;
    			view.setScore(score);
    		}
		}
	}
}
