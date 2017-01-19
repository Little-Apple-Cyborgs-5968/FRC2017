package org.usfirst.frc.team5968.robot;

/**
 * Represents a point on the field
 * 
 * @author BeijingStrongbow
 */
public class Point {
	
	/**
	 * The X coordinate of the point
	 */
	private double x;
	
	/**
	 * The Y coordinate of the point
	 */
	private double y;
	
	/**
	 * The hoppers. The robot will first drive to a point 30 inches in front of the hopper,
	 * turn toward the hopper, and drive straight into it.
	 */
	public static final Point[] hoppers = {
		new Point(30, 202),
		new Point(30, 450),
		new Point(294, 115),
		new Point(294, 537),
		new Point(294, 326)
	};
	
	/**
	 * The boilers. The robot will drive to the edge of the key, realign with the camera, then
	 * drive up to the low goal.
	 */
	public static final Point[] boilers = { //these assume the boilers are at a 45 degree angle - check the CAD model
		new Point(272.4, 51.6), //red boiler
		new Point(272.4, 600.4) //blue boiler
	};
	
	/**
	 * Intialize the point
	 * 
	 * @param x The X coordinate of this point
	 * @param y The Y coordiante of this point
	 */
	public Point(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Gets the X coordinate of this point
	 * 
	 * @return This point's X coordinate
	 */
	public double getX(){
		return x;
	}
	
	/**
	 * Gets the Y coordinate of this point
	 * 
	 * @return This point's Y coordinate
	 */
	public double getY(){
		return y;
	}
}
