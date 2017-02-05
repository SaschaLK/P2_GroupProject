
package game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.Timer;

public class MainController {
	private MyJFrame view;
	private Timer timer;

	private NotesList list = new NotesList();
	private int time=0;
	private Hitbox hitbox = new Hitbox();
	private int score = 0;
	public MainController(MyJFrame view){
		//
		
		
		this.view = view;
		timer = new Timer(5,listener -> update());
		view.getStart().addActionListener(listener->{ready();
				playSound();});
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
		
		
	}
	private void ready(){
		timer.start();	
		view.requestFocus();
	}
	private void update(){
		time++;
		list.checkTime(time);
		list.moveNotes();
		view.updateView(list.getNotesList());
		
	}
	private void checkh0(){
		for(int i=0;i<list.getNotesList().size();i++){
			if(hitbox.hit0(list.getNotesList().get(i).getNote())){
    			score += 10;
    			list.remove(list.getNotesList().get(i));
    			view.setScore(score);
    		}
		}
	}
	private void checkh1(){
		for(int i=0;i<list.getNotesList().size();i++){
			if(hitbox.hit1(list.getNotesList().get(i).getNote())){
    			score += 10;
    			list.remove(list.getNotesList().get(i));
    			view.setScore(score);
    		}
		}
	}
	private void checkh2(){
		for(int i=0;i<list.getNotesList().size();i++){
			if(hitbox.hit2(list.getNotesList().get(i).getNote())){
    			score += 10;
    			list.remove(list.getNotesList().get(i));
    			view.setScore(score);
    		}
		}
	}
	private void checkh3(){
		for(int i=0;i<list.getNotesList().size();i++){
			if(hitbox.hit3(list.getNotesList().get(i).getNote())){
    			score += 10;
    			list.remove(list.getNotesList().get(i));
    			view.setScore(score);
    		}
		}
	}
	public void playSound() {
	    try {
	        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("fml.wav").getAbsoluteFile());
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start();
	    } catch(Exception ex) {
	        System.out.println("Error with playing sound.");
	        ex.printStackTrace();
	    }
	}
//	private void checkh0(){
//		for(Notes note : list.getNotesList()){
//			if(hitbox.hit0(note.getNote())){
//    			score += 10;
//    			list.remove(note);
//    			view.setScore(score);
//    		}
//		}
//	}
}

