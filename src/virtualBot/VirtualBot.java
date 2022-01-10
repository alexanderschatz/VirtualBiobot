package virtualBot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

import Mapping.MapWindow;
import main.Keyboard;
import math.Tuple;
import math.Vector;
import reinforcementLearning.qLearning;

public class VirtualBot {
	private Sensors sens; // Sensor class
	private qLearning qLearn;

	private int c; // case numbering variable

	// distance variables
	private int dist = 0;
	private int distR = 0;
	private int distL = 0;

	private boolean isMoving;

	// starting position
	private int xPos = 2;
	private int yPos = 2;
	private Vector v_pos;
	private static Tuple<Integer, Integer> t_position;

	// orientation
	private static int orientation = 0;
	private static int oldOrientation = 0;
	private Vector v_orientation;

	// last position
	private int lastX;
	private int lastY;
	private static Tuple<Integer, Integer> t_lastPosition;

	private int cellSize;

	// remaining charge
	private int charge = 9000;

	private static double reward = 0;
	private int actionCount = 0;

	// actions the bot can do
	private static char[] possibleActions = new char[] { 'f', 'b', 'l', 'r' };
	private static int lastActionIndex = 0;

	private int[][] map;

	public static int maxResets = 10000;

	private PrintWriter writer;
	private PrintWriter writeOld;
	private PrintWriter writeNew;
	private PrintWriter writeGreyMap;
	
	private static MapWindow greyMap;

	public VirtualBot() {
		// initialize sensors
		sens = new Sensors();
		// initialize QLearning
		qLearn = new qLearning();
		greyMap = new MapWindow();

		this.cellSize = World.getCellSize();
		this.isMoving = false;

		// reset the bot
		resetBot();

//		System.out.println(t_position);
		// get orientation vector from sensor class
		v_orientation = sens.getOrientation();

//		ArrayList<Tuple<Integer, Integer>> positionTuples = World.getPositionTuples();

//		System.out.println(positionTuples.indexOf(t_position));

		// get a copy of the worldMatrix
		map = World.getMap();

		try {
			writer = new PrintWriter("output/data.txt", "UTF-8");
			writeOld = new PrintWriter("output/oldQTable.txt", "UTF-8");
			writeNew = new PrintWriter("output/newQTable.txt", "UTF-8");
			writeGreyMap = new PrintWriter("output/greyMap.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// starting case
		this.c = 2;

		System.out.println("Bot instantiated!");
	}

	private void resetBot() {
		if(maxResets < 10000)	generateStartingPosition();
		
		// reset the position vector and tuple to starting position
		v_pos = new Vector(new double[] { xPos, yPos });
		t_position = new Tuple<Integer, Integer>(xPos, yPos);
		t_lastPosition = new Tuple<Integer, Integer>(xPos, yPos);
		
		// reset count of action and remaining charge
		actionCount = 0;
		charge = 9000;

		// reset sensors
		sens.reset();
	}
	
	private void generateStartingPosition() {
		Random rand = new Random();
		xPos = rand.nextInt(20);
		yPos = rand.nextInt(20);
		
		if(map[xPos][yPos] > 0) generateStartingPosition();
	}

	public void update() {
		
		// if a field containing food is not reached
		if (map[t_position.x][t_position.y] != 99) {
			// decrease charge

			// if charge falls below critical value reset the bot
			if (charge < 1) {
				System.out.println("Bot died at " + t_position);
				resetBot();
			}

			// behaviour cases
			switch (c) {

			case 0: // Stop - case // unused
				stop();
				break;

			case 1: // Idle - case
				// listen for keyboard input to change the case
				if (Keyboard.isKeyPressed(KeyEvent.VK_RIGHT))
					this.c = 2;
				break;

			case 2: // Movement - case // QLearning
				doAction(qLearn.newAction());
				
				oldOrientation = orientation;
				orientation = (int) sens.getAngle();
				actionCount++;
				
				greyMap.update(t_position);

				break;

			case 3: // Movement - case // Mapping
				sens.update(v_pos, v_orientation);

//				dist = sens.getDist();
//				distR = sens.getDistR();
//				distL = sens.getDistL();
//				int angle = sens.getAngle();

				break;

			default:
				break;
			}
			// else bot has found food
		} else {
			writer.println(actionCount);
			System.out.printf("%d: found nourishment after %d steps; leftover charge: %d\n", maxResets, actionCount,
					charge);
			greyMap.updateMap();
			// if resetCount has reached value stop the program
			if (maxResets < 1) {
				qLearn.printQTable(writeNew);

				writer.close();
				writeOld.close();
				writeNew.close();
				writeGreyMap.close();
				System.out.println("enough!");
				System.exit(0);
			}

			// reset the bot and increase resetCount
			resetBot();
			World.setFirstRun();
			maxResets--;
		}
//		charge--;
	}

	// draw bot
	public void draw(Graphics g) {
		
		int x = (int) v_pos.cartesian(0);
		int y = (int) v_pos.cartesian(1);

		// draw the field
		g.setColor(Color.MAGENTA);
		g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);

		// draw the orientation as triangle
		int[][] polygonLines = updatePolygonLines(x, y);
		g.setColor(Color.BLACK);
		g.fillPolygon(polygonLines[0], polygonLines[1], 3);
	}

	// updates orientation by current angle
	private int[][] updatePolygonLines(int xPos, int yPos) {
		int[] x = null;
		int[] y = null;

		orientation = sens.getAngle();

		switch (orientation) {

		case 360:
		case 0:
			x = new int[] { xPos * cellSize, xPos * cellSize + cellSize / 2, xPos * cellSize + cellSize, };
			y = new int[] { yPos * cellSize, yPos * cellSize + cellSize, yPos * cellSize, };
			break;

		case 90:
		case -270:
			x = new int[] { xPos * cellSize, xPos * cellSize, xPos * cellSize + cellSize, };
			y = new int[] { yPos * cellSize, yPos * cellSize + cellSize, yPos * cellSize + cellSize / 2, };
			break;

		case 180:
		case -180:
			x = new int[] { xPos * cellSize, xPos * cellSize + cellSize / 2, xPos * cellSize + cellSize, };
			y = new int[] { yPos * cellSize + cellSize, yPos * cellSize, yPos * cellSize + cellSize, };
			break;

		case 270:
		case -90:
			x = new int[] { xPos * cellSize + cellSize, xPos * cellSize + cellSize, xPos * cellSize, };
			y = new int[] { yPos * cellSize, yPos * cellSize + cellSize, yPos * cellSize + cellSize / 2, };
			break;

		default:
			break;
		}

		return new int[][] { x, y };
	}

	// do action with index
	private void doAction(int actionIndex) {
		char action = possibleActions[actionIndex];
		switch (action) {
		case 'f':
			move(1);
			break;

		case 'b':
			move(-1);
			break;

		case 'l':
			turnLeft();
			break;

		case 'r':
			turnRight();
			break;

		default:
			break;
		}

		lastActionIndex = actionIndex;
	}

	/**
	 * moves the robot forward if delay is positive and backward if delay is
	 * negative
	 * 
	 * @param direction is the minimal amount of time the motor has to move
	 * @throws RemoteException
	 */
	private void move(int direction) {
//		System.out.println(direction);
		if (c == 0)
			return;

		// get current orientation from sensors

		v_orientation = sens.getOrientation();

		// newPosition = oldPosition + orientation * movementDirection
		Vector v_newPos = v_pos.plus(v_orientation.scale(direction));

		int newX = (int) v_newPos.cartesian(0);
		int newY = (int) v_newPos.cartesian(1);

//		System.out.println("currently on: " + map[newX][newY]);
		// if movement leads to an empty or food containing field
		if (map[newX][newY] == 0 || map[newX][newY] == 99) {
			lastX = (int) v_pos.cartesian(0);
			lastY = (int) v_pos.cartesian(1);

			// update position
			v_pos = v_newPos;
			t_position = new Tuple<Integer, Integer>(newX, newY);
			t_lastPosition = new Tuple<Integer, Integer>(lastX, lastY);

			// reward is positive
			reward = qLearn.getRewards().get(map[newX][newY]);
		} else {
			// moving into a wall is prohibited
//			System.out.println("Wall!");
			reward = qLearn.getRewards().get(1); // reward is negative
		}

		isMoving = true; // actually unused
	}

	// turns the bot 90° (left)
	private void turnLeft() {
		sens.setAngle(90);
		t_lastPosition = new Tuple<Integer, Integer>(t_position.x, t_position.y);

		turnReward();
	}

	// turns the bot -90° (right)
	private void turnRight() {
		sens.setAngle(-90);
		t_lastPosition = new Tuple<Integer, Integer>(t_position.x, t_position.y);

		turnReward();
	}

	private void turnReward() {
		Vector v_nextPos = v_pos.plus(sens.getOrientation());
		int nextX = (int) v_nextPos.cartesian(0);
		int nextY = (int) v_nextPos.cartesian(0);

		reward = qLearn.getRewards().get(map[nextX][nextY]);
//		System.out.println(v_nextPos + "\t" + reward);
	}

	// unused
	public void stop() {
		isMoving = false;
	}

	// getter

	public static char[] getActions() {
		return possibleActions;
	}

	public static int getLastActionIndex() {
		return lastActionIndex;
	}

	public static double getReward() {
		return reward;
	}

	public int getC() {
		return c;
	}

	public void setC(int i) {
		this.c = i;
	}

	public boolean isMoving() {
		return isMoving;
	}

	public Vector getPos() {
		return v_pos;
	}

	public static int getOrientation() {
		return orientation;
	}
	
	public static int getOldOrientation() {
		return oldOrientation;
	}
	
	public static Tuple<Integer, Integer> getT_position() {
		return t_position;
	}

	public static Tuple<Integer, Integer> getT_lastPosition() {
		return t_lastPosition;
	}

	public Vector getV_Orientation() {
		return v_orientation;
	}

	public int getLastX() {
		return lastX;
	}

	public int getLastY() {
		return lastY;
	}

	public static int getResetCount() {
		return maxResets;
	}
}
