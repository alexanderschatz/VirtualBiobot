package main;

import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import virtualBot.World;

@SuppressWarnings("serial")
public class Fenster extends JFrame {
	private BufferStrategy strat;

	private World world;

	public Fenster() {
		super("VirtualBot");

		setSize(main.WIDTH, main.HEIGHT);
		setUndecorated(true); // no taskbar with name
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
//		setLocation(this.getSize().width / 2, 0); // middle of monitor
		setLocation(0, 0); // top left corner
		setVisible(true);

		// initialize World 
		world = new World();

		// initialize KeyListener
		Keyboard keyboard = new Keyboard();
		addKeyListener(keyboard);
	}

	// creates Buffers for a smoother frame transition
	public void makestrat() {
		createBufferStrategy(2);
		strat = getBufferStrategy();
	}

	// repaint the graphical content
	public void repaint() {
		Graphics g = strat.getDrawGraphics();

		draw(g);
		g.dispose(); // get rid of last frame
		strat.show(); // show the next frame
		Toolkit.getDefaultToolkit().sync(); // synchronize buffers
	}

	// update content
	public void update(float tslf) {
		world.update(tslf);
	}

	// draw content
	public void draw(Graphics g) {
		world.draw(g);
	}
}
