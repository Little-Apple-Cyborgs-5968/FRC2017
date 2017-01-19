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
	 * Whether this point is a hopper
	 */
	private boolean isHopper;
	
	/**
	 * Whether this point is a boiler
	 */
	private boolean isBoiler;
	
	/**
	 * Whether this point is a retrieval or overflow chute
	 */
	private boolean isChute;
	
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
