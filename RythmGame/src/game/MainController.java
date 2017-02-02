package game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Timer;

public class MainController {
	private MyJFrame view;
	private Timer timer;
//	private Notes note=new Notes();
	private NotesList list = new NotesList();
	private int time=0;
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
					checkh0();				
		    	}
				else if(e.getKeyCode() == KeyEvent.VK_F){
					checkh1();				
		    	}	
				else if(e.getKeyCode() == KeyEvent.VK_J){
					checkh2();				
		    	}	
				else if(e.getKeyCode() == KeyEvent.VK_K){
					checkh3();				
		    	}	
			}
		});
		
		timer.start();
	}
	private void update(){
		time++;
		list.checkTime(time);
		list.moveNotes();
		view.updateView(list.getNotesList());
		
	}
	private void checkh0(){
		for(Notes note : list.getNotesList()){
			if(hitbox.hit0(note.getNote())){
    			score =+ 10;
    			view.setScore(score);
    		}
		}
	}
	private void checkh1(){
		for(Notes note : list.getNotesList()){
			if(hitbox.hit1(note.getNote())){
    			score =+ 10;
    			view.setScore(score);
    		}
		}
	}
	private void checkh2(){
		for(Notes note : list.getNotesList()){
			if(hitbox.hit2(note.getNote())){
    			score =+ 10;
    			view.setScore(score);
    		}
		}
	}
	private void checkh3(){
		for(Notes note : list.getNotesList()){
			if(hitbox.hit3(note.getNote())){
    			score =+ 10;
    			view.setScore(score);
    		}
		}
	}
}
