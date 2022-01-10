package virtualBot;

import math.Vector;

public class Sensors {

	private int dist;
	private int distL;
	private int distR;
	private int angle;
	
	private int[][] map;
	

	public Sensors() {
		map = World.getMap();
	}
	
	// reset angle to 0
	public void reset() {
		angle = 0;
	}
	
	// update distance measurements
	public void update(Vector v_botPos, Vector v_botDir) {

		updateFront(v_botPos, v_botDir);
		updateLeft(v_botPos);
		updateRight(v_botPos);
		
//		System.out.println(angle);
	}
	
	// count empty fields from front until a wall is reached
	public void updateFront(Vector v_botPos, Vector v_botDir) {
		dist = 0;
		Vector tempPos = v_botPos;
		
		while(map[(int) tempPos.cartesian(0)][(int) tempPos.cartesian(1)] == 0) {
			tempPos = tempPos.plus(v_botDir);
			dist++;
//			System.out.println(v_botPos + " + " + v_botDir + " = " + tempPos + " --> " + map[(int) tempPos.cartesian(0)][(int) tempPos.cartesian(1)] + " // " + dist);		
		}
	}
	
	// count empty fields from left until a wall is reached
	public void updateLeft(Vector v_botPos) {
		int oldAngle = angle;
		setAngle(-90);
		
		Vector v_botDirLeft = getOrientation();
		
		distL = 0;
		Vector tempPos = v_botPos;
		
		while(map[(int) tempPos.cartesian(0)][(int) tempPos.cartesian(1)] == 0) {
			tempPos = tempPos.plus(v_botDirLeft);
			distL++;
//			System.out.println(v_botPos + " + " + v_botDir + " = " + tempPos + " --> " + map[(int) tempPos.cartesian(0)][(int) tempPos.cartesian(1)] + " // " + distL);		
		}
		
		this.angle = oldAngle;
	}
	
	// count empty fields from right until a wall is reached
	public void updateRight(Vector v_botPos) {
		int oldAngle = angle;
		setAngle(90);
		
		Vector v_botDirRight = getOrientation();
		
		distR = 0;
		Vector tempPos = v_botPos;
		
		while(map[(int) tempPos.cartesian(0)][(int) tempPos.cartesian(1)] == 0) {
			tempPos = tempPos.plus(v_botDirRight);
			distR++;
//			System.out.println(v_botPos + " + " + v_botDir + " = " + tempPos + " --> " + map[(int) tempPos.cartesian(0)][(int) tempPos.cartesian(1)] + " // " + distR);		
		}
		
		this.angle = oldAngle;
	}

	
	// getter / setter
	
	public int getDist() {
		return dist;
	}

	public int getDistL() {
		return distL;
	}

	public int getDistR() {
		return distR;
	}

	public int getAngle() {
		if(angle >= 0) for(int i = angle/360; i >= 1; i--){angle -= 360;}
		else if(angle < 0) for(int i = (angle/360)*-1; i >= 1; i--){angle += 360;}
		return angle;
	}

	public void setAngle(int angle) {
		if(this.angle == 0 && angle < 0) this.angle = 360;
		this.angle += angle;
	}
	
	public Vector getOrientation() {
		switch (getAngle()) {

		case 360:
		case 0:
			return new Vector(new double[] {0.0, 1.0});
			
		case 90:
		case -270:
			return new Vector(new double[] {1.0, 0.0});
			
		case 180:
		case -180:
			return new Vector(new double[] {0.0, -1.0});
			
		case 270:
		case -90:
			return new Vector(new double[] {-1.0, 0.0});

		default:
			break;
		}
		
		throw new IllegalArgumentException("angle disagree");
	}
}
