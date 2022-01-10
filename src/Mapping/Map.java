package Mapping;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Map {
	private static float[][] map;
	private static float[][] count;
	private static float[][] dot;

	final static float SCALE = 0.1f;

	final static int WIDTH = (int) (2000 * SCALE); // mm
	final static int HEIGHT = (int) (2000 * SCALE); // mm

	final static int ROBOTWIDTH = (int) (183 * SCALE); // mm
	final static float ROBOTSPEED = 14.62618f * SCALE; // mm/s

	private static int[] distF;
	private static int[] distL;
	private static int[] distR;
	private static int[] angle;

	private static MapWindow fenster;

	static BufferedWriter w;

	public Map() throws NumberFormatException, IOException {
		map = new float[(int) (WIDTH)][(int) (HEIGHT)];
		count = new float[(int) (WIDTH)][(int) (HEIGHT)];
		dot = new float[WIDTH][HEIGHT];

		distF = new int[1000000];
		distL = new int[1000000];
		distR = new int[1000000];
		angle = new int[1000000];

		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH; x++) {
				map[x][y] = 0;
				count[x][y] = 0;
			}
		}

		FileReader input = new FileReader(Logger.getDir() + "/distance.dat");
		BufferedReader bufRead = new BufferedReader(input);
		String myLine = null;
		int o = 0;
		float help = 0;
		while ((myLine = bufRead.readLine()) != null) {
			String[] array = myLine.split("\t");

			// frontal US
			help = Float.parseFloat(array[1]);
			if (help >= 2829)
				distF[o] = 0;
			else
				distF[o] = (int) (help * 10);

			// left US
			help = Float.parseFloat(array[0]);
			if (help >= 2829)
				distL[o] = 0;
			else
				distL[o] = (int) (help * 10);

			// right US
			help = Float.parseFloat(array[2]);
			if (help >= 2829)
				distR[o] = 0;
			else
				distR[o] = (int) (help * 10);

			// gyro sensor
			int angela = (int) Float.parseFloat(array[3]);
			angle[o] = cutoff(angela);
			o++;
		}
		bufRead.close();
		input.close();

		int phi = 0;

		for (int i = 0; i < angle.length; i++) {
			if (i != 0 && angle[i] != angle[i - 1]) {
				phi += angle[i] - angle[i - 1];
			}

			if (distL[i] != 0 && distF[i] != 0 && distR[i] != 0)
				bel(i, phi);
		}

		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH; x++) {
				map[x][y] = map[x][y] / count[x][y];

				if (x == 0 || y == 0 || x == WIDTH - 1 || y == HEIGHT - 1)
					map[x][y] = 1;

				if (map[x][y] >= 0.1) {
					dot[x][y] = 1;
				} else {
					dot[x][y] = 0;
				}
			}
		}

		w = new BufferedWriter(new FileWriter(Logger.getDir() + "/rawMap.dat"));

		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH; x++) {
				if (x == 0 || y == 0 || x == WIDTH - 1 || y == HEIGHT - 1)
					dot[x][y] = 1;

				if (dot[x][y] == 1)
					w.write(1 + " ");
				else
					w.write(0 + " ");
			}
			w.write("\n");
		}

		w.close();

		fenster = new MapWindow(dot);
		fenster.makestrat();

	}

	public void update() {
		fenster.repaint(dot);
	}

	public static int cutoff(int angle) {
		for (int i = angle / 360; i >= 1; i--) {
			angle -= 360;
		}
		return angle;
	}

	public static void bel(int p, int phi) {
		int rho = phi + 90;
		int tau = phi - 90;

		int mX = 0;
		int mY = 0;

		int rX = 0;
		int rY = 0;

		int lX = 0;
		int lY = 0;

		if (phi >= 0 && phi <= 10) {
			rX = (int) (distR[p] * SCALE) - 1;
			lX = (int) (rX + ROBOTWIDTH);
			mX = (int) (rX + (ROBOTWIDTH / 2));

			rY = (int) ((HEIGHT - distF[p] * SCALE) - 1);
			lY = rY;
			mY = rY;
		} else if (phi >= 80 && phi <= 100) {
			rX = (int) ((WIDTH - distF[p] * SCALE) - 1);
			lX = rX;
			mX = rX;

			rY = (int) (HEIGHT - distR[p] * SCALE) - 1;
			lY = (int) (rY - ROBOTWIDTH);
			mY = (int) (rY - (ROBOTWIDTH / 2));
		} else if (phi >= 170 && phi <= 190) {
			rX = (int) (WIDTH - distR[p] * SCALE) - 1;
			lX = (int) (rX - ROBOTWIDTH);
			mX = (int) (rY - (ROBOTWIDTH / 2));

			rY = (int) ((distF[p] * SCALE) - 1);
			lY = rY;
			mY = rY;
		} else if (phi >= 260 && phi <= 280) {
			rX = (int) (distF[p] * SCALE) - 1;
			lX = rX;
			mX = rX;

			rY = (int) (distR[p] * SCALE) - 1;
			lY = (int) (rY + ROBOTWIDTH);
			mY = (int) (rY + (ROBOTWIDTH / 2));
		}

		// information from frontal US
		int tilesF = (int) (distF[p] * SCALE) - 1;
		bresenham(mX, mY, phi, tilesF);

		int tilesL = (int) (distL[p] * SCALE) - 1;
		bresenham(lX, lY, rho, tilesL);

		int tilesR = (int) (distR[p] * SCALE) - 1;
		bresenham(rX, rY, tau, tilesR);

	}

	public static void setCell(int x, int y, float p) {
		if (x < 200 && y < 200 && x >= 0 && y >= 0) {
			map[x][y] += p;
			count[x][y]++;
		}
	}

	public static void bresenham(int x0, int y0, int phi, int dist) {
		int dX = (int) (dist * Math.sin(Math.toRadians(phi)));
		int dY = (int) (dist * Math.cos(Math.toRadians(phi)));

		int x1 = x0 + dX;
		int y1 = y0 + dY;

		if (Math.abs(dY) < Math.abs(dX)) {
			if (x0 > x1) {
				plotLineLow(x1, y1, x0, y0);
			} else {
				plotLineLow(x0, y0, x1, y1);
			}
		} else {
			if (y0 > y1) {
				plotLineHigh(x1, y1, x0, y0);
			} else {
				plotLineHigh(x0, y0, x1, y1);
			}
		}

		setCell(x1, y1, 1);
	}

	public static void plotLineLow(int x0, int y0, int x1, int y1) {
		int dX = x1 - x0;
		int dY = y1 - y0;
		int yi = 1;

		if (dY < 0) {
			yi = -1;
			dY = -dY;
		}

		int D = 2 * dY - dX;
		int y = y0;

		for (int x = x0; x < x1; x++) {
			setCell(x, y, 0);
			if (D > 0) {
				y += yi;
				D -= 2 * dX;
			}
			D += 2 * dY;
		}

	}

	public static void plotLineHigh(int x0, int y0, int x1, int y1) {
		int dX = x1 - x0;
		int dY = y1 - y0;
		int xi = 1;

		if (dX < 0) {
			xi = -1;
			dX = -dX;
		}

		int D = 2 * dX - dY;
		int x = x0;

		for (int y = y0; y < y1; y++) {
			setCell(x, y, 0);
			if (D > 0) {
				x += xi;
				D -= 2 * dY;
			}
			D += 2 * dX;
		}

	}

}
