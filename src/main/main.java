package main;

import java.awt.event.KeyEvent;

import Mapping.MapWindow;

public class main {
	// width and height of the monitor
	public final static int WIDTH = 1000;
	public final static int HEIGHT = 1000;

	// FPS = frames per second => refresh rate of the JFrame
	final static int FPS = 120;
	
	private static boolean runningUpdates = false;
	private static boolean runningVisualUpdates = true;


	
	// start the program from here
	public static void main(String[] args) {

		// initialize new JFrame
		Fenster frame = new Fenster();

		
		frame.makestrat(); // enable Bufferstrategy

		long lastFrame = System.currentTimeMillis(); // unused

		// program loop
		while (true) {
			// unused
			long thisFrame = System.currentTimeMillis(); // nimmt die Zeit des Frames
			float timeSinceLastFrame = (float) ((thisFrame - lastFrame) / 1000.0);
			lastFrame = thisFrame;

			// exit condition
			if (Keyboard.isKeyPressed(KeyEvent.VK_ESCAPE))	System.exit(0);
			if (Keyboard.isKeyPressed(KeyEvent.VK_SPACE))	runningUpdates = !runningUpdates;
			if (Keyboard.isKeyPressed(KeyEvent.VK_ENTER))	runningVisualUpdates = !runningVisualUpdates;
						
			
			
			// update the content
			if (runningUpdates) frame.update(timeSinceLastFrame);

			// redraw the content
			if (runningVisualUpdates) frame.repaint();

			//	sleep 
			//	currently disabled to unlock framerate
//			try {
//				Thread.sleep(1000 / FPS);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
	}
}
