package  MusicNotes;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class MusicNotes2 {
	 public static int x=40;
	
	 public static void SimpleBlackNote() {
		 
	 }
	 
	public static void main(String[] args) {
	
		
	JFrame f = new JFrame() {
	  public void paint(Graphics g) {
		  
		  
		  //
	    Graphics2D g2 = (Graphics2D) g;
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	        RenderingHints.VALUE_ANTIALIAS_ON);

	    Font font = new Font("Bravura", Font.PLAIN, 60);
	   
	    g2.setFont(font);
	    g2.drawString("\uD834\uDD1E", 40, 268);// Gclef
	    g2.drawString("\uD834\uDD22", 40, 370);// Fclef
	    g2.drawString("\u266A", 40, 470);
	    
	    
	    
	    // Vertical lines
		g2.drawLine(0,268,960,268);
		g2.drawLine(0,368,960,368);
		g2.drawLine(0,468,960,468);
		g2.drawLine(0,568,960,568);
	  }
	};
	SwingUtilities.updateComponentTreeUI(f);
	f.setSize(960,960);
	f.setVisible(true);
	
	
	}
	}
