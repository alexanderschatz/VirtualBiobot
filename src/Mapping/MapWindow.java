package Mapping;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JFrame;

import main.Keyboard;
import math.Tuple;
import reinforcementLearning.qLearning;
import virtualBot.VirtualBot;
import virtualBot.World;

@SuppressWarnings("serial")
public class MapWindow extends JFrame {
	private BufferStrategy strat;

	private static int matrixSizeX;
	private static int matrixSizeY;

	private final int WIDTH = main.main.WIDTH;
	private final int HEIGHT = main.main.HEIGHT;

	private Tuple<Integer, Integer> current;

	private boolean firstRun = true;

	private int cellSize;
	
//	private double[][] qTable;
	private static double[][] map;
	private static long[][] countMap;
	private static long highestValue = 0;
	private static long lowestValue = 100000;
	
	private int maxResets;

//	private int counter = 0;

	public MapWindow() {
		super("Umgebungskarte");

		setSize(WIDTH, HEIGHT);
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLocation(0 , 0);
		setVisible(true);
		
		Keyboard keyboard = new Keyboard();
		addKeyListener(keyboard);

		cellSize = WIDTH / (int) (virtualBot.World.WIDTH);

		matrixSizeX = World.getMatrixSizeX();
		matrixSizeY = World.getMatrixSizeY();

		maxResets = VirtualBot.maxResets;
		
		map = new double[matrixSizeX][matrixSizeY];
		countMap = new long[matrixSizeX][matrixSizeY];
		
		for (int x = 0; x < matrixSizeX; x++) {
			for (int y = 0; y < matrixSizeY; y++) {
				countMap[x][y] = 0;
			}
		}
		
		makestrat();

//		reset();
	}

	public void makestrat() {
		createBufferStrategy(2);
		strat = getBufferStrategy();
	}

	public void repaint() {
		Graphics g = strat.getDrawGraphics();
		draw(g);
		g.dispose();
		strat.show();
	}
	
	private static void reset() {
		highestValue = 0;
		lowestValue = 100000;
	}

	public void updateMap() {
		
		//reset
//		for(int x = 0; x < matrixSizeX; x++) {
//			for(int y = 0; y < matrixSizeY; y++) {
//				if(countMap[x][y] > highestValue) highestValue = countMap[x][y];
//				if(countMap[x][y] < lowestValue) lowestValue = countMap[x][y];
//			}
//		}
		
		repaint();
//		reset();
	}

	public void printMap(PrintWriter write) {
		for (int x = 0; x < matrixSizeX; x++) {
			for (int y = 0; y < matrixSizeY; y++) {
				write.print(map[x][y] + "\t");
			}
			write.println();
		}
	}

	public void update(Tuple<Integer, Integer> pos) {
		countMap[pos.x][pos.y]++;
	}

	public void draw(Graphics g) {
		for(int x = 0; x < matrixSizeX; x++) {
			for(int y = 0; y < matrixSizeY; y++) {
				map[x][y] = (countMap[x][y] - (double) lowestValue) / ((double) highestValue - (double) lowestValue);
				drawField(g, x, y);
			}
		}
	}

	// draws the current field
	private void drawField(Graphics g, int x, int y) {
		if (countMap[x][y] >= (int) (maxResets * 0.2))
			g.setColor(Color.WHITE);
		else if (countMap[x][y] >= (int) (maxResets * 0.1))
			g.setColor(Color.LIGHT_GRAY);
		else if (countMap[x][y] >= (int) (maxResets * 0.01))
			g.setColor(Color.DARK_GRAY);
		else
			g.setColor(Color.BLACK);

		g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
		g.setColor(Color.BLACK); // draws border
		g.drawRect(x * cellSize, y * cellSize, cellSize, cellSize);
	}
}
