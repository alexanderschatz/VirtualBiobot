package virtualBot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import main.Fenster;
import main.main;
import math.Tuple;

@SuppressWarnings("serial")
public class World extends JPanel {

	// scaling can be change to increase or decrease the size of the world
	final static float SCALE = 0.1f;

	// matrix width and height is defined here
	public final static int WIDTH = (int) (2000 * SCALE); // mm
	public final static int HEIGHT = (int) (2000 * SCALE); // mm

	private static int cellSize;

	private static int matrixSizeX;
	private static int matrixSizeY;

	private static int[][] map;
	
	// positionTuples holds a List of all possible positions inside the matrix as tuples
	private static ArrayList<Tuple<Integer, Integer>> positionTuples = new ArrayList<Tuple<Integer, Integer>>();

	private VirtualBot bot;
	
	private PrintWriter mapWriter;

	private static boolean firstRun = true;

	public World() {

		// cellSize is defined as frameWidth / matrixWidth
		cellSize = main.WIDTH / (int) (World.WIDTH);
		matrixSizeX = WIDTH;
		matrixSizeY = HEIGHT;

		// initialize map size
		map = new int[matrixSizeX][matrixSizeY];


		
		// initialize map matrix
		initializeMap();

		// initialize new VirtualBot
		bot = new VirtualBot();
		
		
	}

	private void initializeMap() {
		// random value defines how possible a "box" or wall is 
		double randValue = 0.01;

//		try {
//			mapWriter = new PrintWriter("map2.txt", "UTF-8");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		for (int x = 0; x < matrixSizeX; x++) {
//			for (int y = 0; y < matrixSizeY; y++) {
//				int value = 0;
//
//				// edge fields are always walls
//				if ((x == 0 || y == 0 || x == matrixSizeX - 1 || y == matrixSizeY - 1)
//						|| (Math.random() <= randValue)) {
//					value = 1;
//				}
//				// set the value of the position
//				map[x][y] = value;
//				// add new position tuple
//				positionTuples.add(new Tuple<Integer, Integer>(x, y));
//				
//				mapWriter.printf("%d ", value);
//			}
//			mapWriter.println();
//		}
//		mapWriter.close();
		
		try {
			BufferedReader readMap = new BufferedReader(new FileReader("map2.txt"));
			
			String line;
			int y = 0;
			
			while((line = readMap.readLine()) != null) {
				int x = 0;
				for(String l: line.split(" ")) {
					map[x][y] = Integer.parseInt(l);
					// add new position tuple
					positionTuples.add(new Tuple<Integer, Integer>(x, y));
					x++;
					
				}
				y++;
			}
			
			readMap.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Unable to open file");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Unable to read file");
			e.printStackTrace();
		}
		
		
		
		map[2][2] = 0; // bot starting position is always 0
		
		// field containing food is defined as 99 and drawn in green
		map[12][0] = 99;
		map[7 ][matrixSizeY-1] = 99;
	}

	// update the bot
	public void update(float tslf) {
		bot.update();
	}

	/* draw content
	 * 
	 * if firstRun draw the matrix grid
	 */
	public void draw(Graphics g) {
//		if (this.firstRun) {
			this.drawGrid(g);
//			this.firstRun = false;
//			System.out.println("World drawn!");
//		}

		// draw bot
		bot.draw(g);
		// redraw the last bot position
		drawField(g, bot.getLastX(), bot.getLastY());
	}

	// draw matrixGrid
	private void drawGrid(Graphics g) {
		for (int y = 0; y < matrixSizeX; y++) {
			for (int x = 0; x < matrixSizeY; x++) {
				drawField(g, x, y);
			}
		}
	}

	// draws the current field 
	private void drawField(Graphics g, int x, int y) {
		if (map[x][y] >= 1) {
			if (map[x][y] == 99) 	// field contains food
				g.setColor(Color.GREEN);
			else				 	// field contains wall
				g.setColor(Color.BLACK);
			g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
		} else {					// field is empty
			g.setColor(Color.WHITE);
			g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
			g.setColor(Color.BLACK); // draws border
			g.drawRect(x * cellSize, y * cellSize, cellSize, cellSize);
		}
	}

	
	// getter
	
	public static int getCellSize() {
		return cellSize;
	}

	public static int getMatrixSizeX() {
		return matrixSizeX;
	}

	public static int getMatrixSizeY() {
		return matrixSizeY;
	}

	public static int[][] getMap() {
		return map;
	}

	public static ArrayList<Tuple<Integer, Integer>> getPositionTuples() {
		return positionTuples;
	}
	
	public static void setFirstRun() {
		firstRun = true;
	}

}
