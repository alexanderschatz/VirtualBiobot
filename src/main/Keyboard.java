package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Keyboard implements KeyListener, MouseMotionListener, MouseListener
{ //hört auf Keyboard/Mauseingaben
	private static boolean [] keys = new boolean[1024];
	
	private static int mouseX;
	private static int mouseY;
	
	private static int button;
	
	public static int getMouseX()
	{
		return mouseX;
	}
	public static int getMouseY()
	{
		return mouseY;
	}
	public static int getButton()
	{
		return button;
	}
	public static boolean isKeyPressed(int keycode)
	{
		return keys[keycode];
	}
	
	@Override
	public void keyTyped(KeyEvent e) 
	{

	}

	@Override
	public void keyPressed(KeyEvent e) 
	{
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) 
	{
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) 
	{
		mouseX = e.getX();
		mouseY = e.getY();
		//System.out.println("X = " + mouseX + " Y = " + mouseY);
	}
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		
	}
	@Override
	public void mousePressed(MouseEvent e) 
	{
		button = e.getButton();
	}
	@Override
	public void mouseReleased(MouseEvent e) 
	{
		button = -1;
	}
	@Override
	public void mouseEntered(MouseEvent e) 
	{
		
	}
	@Override
	public void mouseExited(MouseEvent e) 
	{
		
	}
	
}
