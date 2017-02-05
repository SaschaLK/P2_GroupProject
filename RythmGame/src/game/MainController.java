
package game;

import java.awt.Color;
import java.awt.event.KeyAdapter;
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

	private int color=0;
	private Thread t1=new Thread();
	private Thread t2=new Thread();
	private Thread t3=new Thread();
	private Thread t4=new Thread();
	
	public MainController(MyJFrame view){
		//
		
		
		this.view = view;
		timer = new Timer(5,listener -> update());
		view.getStart().addActionListener(listener->{ready();
				playSound();});
		//Sollte in der Lage sein 2 noten zuerfassen Thread
		view.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				t1 = new Thread(new Runnable() {
					
					@Override
					public void run() {
						if(e.getKeyCode() == KeyEvent.VK_D){
						checkh0();				
			    	}
						
					}
				});
				t2 = new Thread(new Runnable() {

					@Override
					public void run() {
						if (e.getKeyCode() == KeyEvent.VK_F) {
							checkh1();
						}

					}
				});
				t3 = new Thread(new Runnable() {

					@Override
					public void run() {
						if (e.getKeyCode() == KeyEvent.VK_J) {
							checkh2();
						}

					}
				});
				t4 = new Thread(new Runnable() {

					@Override
					public void run() {
						if (e.getKeyCode() == KeyEvent.VK_K) {
							checkh3();
						}

					}
				});			
				t1.start();
				t2.start();
				t3.start();
				t4.start();
			}
					
		});
		
//		view.addKeyListener(new KeyListener() {
//			
//			@Override
//			public void keyTyped(KeyEvent e) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void keyReleased(KeyEvent e) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void keyPressed(KeyEvent e) {
//				
//				if(e.getKeyCode() == KeyEvent.VK_D){
//					checkh0();				
//		    	}
//				if(e.getKeyCode() == KeyEvent.VK_F){
//					checkh1();				
//		    	}	
//				if(e.getKeyCode() == KeyEvent.VK_J){
//					checkh2();				
//		    	}	
//				if(e.getKeyCode() == KeyEvent.VK_K){
//					checkh3();				
//		    	}	
//			}
//		});
		
	
	}
	private void ready(){
		timer.start();	
		view.requestFocus();
	}
	private void update(){
		time++;
		color++;
		list.checkTime(time);
		list.moveNotes();
		if(color==30){
			view.getPanel().setColor(Color.BLUE, 1);
			view.getPanel().setColor(Color.BLUE, 2);
			view.getPanel().setColor(Color.BLUE, 3);
			view.getPanel().setColor(Color.BLUE, 4);
			
		}
		view.updateView(list.getNotesList());
		
	}
	private void checkh0(){
		for(int i=0;i<list.getNotesList().size();i++){
			if(hitbox.hit0(list.getNotesList().get(i).getNote())){
    			score += 10;
    			view.getPanel().setColor(Color.green, 1);
    			color=0;
    			list.remove(list.getNotesList().get(i));
    			view.setScore(score);
    		}
		}
	}
	private void checkh1(){
		for(int i=0;i<list.getNotesList().size();i++){
			if(hitbox.hit1(list.getNotesList().get(i).getNote())){
    			score += 10;
    			view.getPanel().setColor(Color.green, 2);
    			color=0;
    			list.remove(list.getNotesList().get(i));
    			view.setScore(score);
    		}
		}
	}
	private void checkh2(){
		for(int i=0;i<list.getNotesList().size();i++){
			if(hitbox.hit2(list.getNotesList().get(i).getNote())){
    			score += 10;
    			view.getPanel().setColor(Color.green, 3);
    			color=0;
    			list.remove(list.getNotesList().get(i));
    			view.setScore(score);
    		}
		}
	}
	private void checkh3(){
		for(int i=0;i<list.getNotesList().size();i++){
			if(hitbox.hit3(list.getNotesList().get(i).getNote())){
    			score += 10;
    			view.getPanel().setColor(Color.green, 4);
    			color=0;
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

