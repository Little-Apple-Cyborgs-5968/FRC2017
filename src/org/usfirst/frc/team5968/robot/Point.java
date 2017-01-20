package org.usfirst.frc.team5968.robot;

import org.usfirst.frc.team5968.robot.Point.Setpoint;

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
	 * The list of all important points on the field
	 */
	public enum Setpoint{
		HOPPER1, //Hoppers are numbered clockwise starting at the blue retrieval bins
		HOPPER2,
		HOPPER3,
		HOPPER4,
		HOPPER5,
		RED_BOILER,
		BLUE_BOILER,
		RED_RETRIEVAL1,
		RED_RETRIEVAL2,
		BLUE_RETRIEVAL1,
		BLUE_RETRIEVAL2,
		RED_OVERFLOW,
		BLUE_OVERFLOW,
		RED_GEAR1,
		RED_GEAR2,
		RED_GEAR3,
		BLUE_GEAR1,
		BLUE_GEAR2,
		BLUE_GEAR3
	}
	
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
	
	public static Point getCoordinates(Setpoint p){
		
		switch(p){
			case HOPPER1:
				return new Point(30, 202);
			case HOPPER2:
				return new Point(30, 450);
			case HOPPER3:
				return new Point(294, 537);
			case HOPPER4:
				return new Point(294, 326);
			case HOPPER5:
				return new Point(294, 115);
			case RED_BOILER:
				return new Point(272.4, 51.6);
			case BLUE_BOILER:
				return new Point(272.4, 600.4);
			case RED_RETRIEVAL1:
				return null;
			case RED_RETRIEVAL2:
				return null;
			case BLUE_RETRIEVAL1:
				return null;
			case BLUE_RETRIEVAL2:
				return null;
			case RED_OVERFLOW:
				return null;
			case BLUE_OVERFLOW:
				return null;
			case RED_GEAR1:
				return null;
			case RED_GEAR2:
				return null;
			case RED_GEAR3:
				return null;
			case BLUE_GEAR1:
				return null;
			case BLUE_GEAR2:
				return null;
			case BLUE_GEAR3:
				return null;
			default:
				return null;
		}
	}
	
	/**
	 * Get the angle the robot needs to be at to drive up to a setpoint. (For example,
	 * on the left side of the field from the red alliance point of view, the angle is
	 * pi radians.
	 * 
	 * @param p The setpoint to use for the calculation
	 * @return The angle for the point in radians
	 */
	public static double getCorrectAngle(Setpoint p){
		switch(p){
			case HOPPER1:
			case HOPPER2:
				return Math.PI;
			case HOPPER3:
			case HOPPER4:
			case HOPPER5:
				return 0;
			case RED_BOILER:
				return 7 * Math.PI / 4;
			case BLUE_BOILER:
				return Math.PI / 4;
			case RED_RETRIEVAL1:
			case RED_RETRIEVAL2:
				return 5 * Math.PI / 6; //not quite sure about this - check it
			case BLUE_RETRIEVAL1:
			case BLUE_RETRIEVAL2:
				return 7 * Math.PI / 6; //not quite sure about this - check it
			case RED_OVERFLOW:
				return 3 * Math.PI / 2;
			case BLUE_OVERFLOW:
				return Math.PI / 2;
			case RED_GEAR1:
				return 2 * Math.PI / 3;
			case RED_GEAR2:
				return Math.PI / 2;
			case RED_GEAR3:
				return Math.PI / 6;
			case BLUE_GEAR1:
				return 11 * Math.PI / 6;
			case BLUE_GEAR2:
				return 3 * Math.PI / 2;
			case BLUE_GEAR3:
				return 7 * Math.PI / 6;
			default:
				return -1;
		}
	}
}
