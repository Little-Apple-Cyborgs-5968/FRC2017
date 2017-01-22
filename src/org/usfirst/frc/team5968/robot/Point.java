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
	 * The distance away from a target in inches to stop. Gives room for the robot to
	 * turn to the correct angle. 
	 */
	private static double STOP_DISTANCE = 30;
	
	/**
	 * The list of all important points on the field
	 */
	public enum Setpoint{
		HOPPER1, //Hoppers are numbered clockwise starting at the blue retrieval chutes
		HOPPER2,
		HOPPER3,
		HOPPER4,
		HOPPER5,
		RED_BOILER,
		BLUE_BOILER,
		RED_RETRIEVAL1, //farther chute from blue alliance wall
		RED_RETRIEVAL2, //closer chute to blue alliance wall
		BLUE_RETRIEVAL1, //farther chute from red alliance wall
		BLUE_RETRIEVAL2, //closer chute to red alliance wall
		RED_OVERFLOW,
		BLUE_OVERFLOW,
		RED_GEAR1,  //labeled counter clockwise from the left side, from the red alliance point of view
		RED_GEAR2,
		RED_GEAR3,
		BLUE_GEAR1, //labeled clockwise from left, from the red alliance point of view
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
	
	/**
	 * Get the coordinates of a point on the field. Gives a point 30 inches away from the point,
	 * so the robot can turn to the correct angle and drive forwards.
	 * 
	 * @param p The point to get the coordinates for (Hopper, Boiler, etc.)
	 * @return The point representing the X and Y coordinates of the point on the field.
	 */
	public static Point getCoordinates(Setpoint p){
		
		switch(p){
			case HOPPER1: //Hoppers are numbered clockwise starting at the blue retrieval chutes
				return new Point(STOP_DISTANCE, 202);
			case HOPPER2:
				return new Point(STOP_DISTANCE, 450);
			case HOPPER3:
				return new Point(324 - STOP_DISTANCE, 537); //324 = field width in inches
			case HOPPER4:
				return new Point(324 - STOP_DISTANCE, 326);
			case HOPPER5:
				return new Point(324 - STOP_DISTANCE, 115);
			case RED_BOILER:
				return new Point(271.9, 51.11); //edge of the key, even with the middle of the low boiler
			case BLUE_BOILER:
				return new Point(271.9, 600.89); //edge of the key, even with the middle of the low boiler
			case RED_RETRIEVAL1:
				return new Point(6.01 + STOP_DISTANCE * Math.cos(-.46565), 596.49 + STOP_DISTANCE * Math.sin(-.46565)); //farther chute from blue alliance wall
			case RED_RETRIEVAL2:
				return new Point(27.89 + STOP_DISTANCE * Math.cos(-.46565), 640.05 + STOP_DISTANCE * Math.sin(-.46565)); //closer chute to blue alliance wall
			case BLUE_RETRIEVAL1:
				return new Point(6.01 + STOP_DISTANCE * Math.cos(.46565), 55.51 + STOP_DISTANCE * Math.sin(.46565)); //farther chute from red alliance wall
			case BLUE_RETRIEVAL2:
				return new Point(27.89 + STOP_DISTANCE * Math.cos(.46565), 11.95 + STOP_DISTANCE * Math.sin(.46565)); //closer chute to red alliance wall
			case RED_OVERFLOW: //labeled counterclockwise from left, from the red alliance point of view
				return new Point(131.14, STOP_DISTANCE);
			case BLUE_OVERFLOW:
				return new Point(131.14, 652 - STOP_DISTANCE);
			case RED_GEAR1:
				return new Point(131.69 - STOP_DISTANCE * Math.cos(Math.PI / 6), 132.8 - STOP_DISTANCE * Math.sin(Math.PI / 6));
			case RED_GEAR2:
				return new Point(162, 115.3 - STOP_DISTANCE);
			case RED_GEAR3:
				return new Point(192.31 + STOP_DISTANCE * Math.cos(Math.PI / 6), 132.8 - STOP_DISTANCE * Math.sin(Math.PI / 6));
			case BLUE_GEAR1: //labeled clockwise from left, from the red alliance point of view
				return new Point(131.7 - STOP_DISTANCE * Math.cos(Math.PI / 6), 519.2 + STOP_DISTANCE * Math.sin(Math.PI / 6));
			case BLUE_GEAR2:
				return new Point(162, 536.7 + STOP_DISTANCE);
			case BLUE_GEAR3:
				return new Point(192.3 + STOP_DISTANCE * Math.cos(Math.PI / 6), 519.2 + STOP_DISTANCE * Math.sin(Math.PI / 6));
			default:
				return null;
		}
	}
	
	/**
	 * Get the angle the robot needs to be at from 0 to 2 pi radians to drive up to a setpoint. (For example,
	 * on the left side of the field from the red alliance point of view, the angle is
	 * pi radians.
	 * 
	 * @param p The setpoint to use for the calculation
	 * @return The angle for the point in radians
	 */
	public static double getCorrectAngle(Setpoint p){
		switch(p){
			case HOPPER1: //labeled clockwise from the blue alliance retrieval chutes
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
			case RED_RETRIEVAL1: //farther chute from blue alliance wall
			case RED_RETRIEVAL2: //closer chute to blue alliance wall
				return 2.618; 
			case BLUE_RETRIEVAL1: //farther chute from red alliance wall
			case BLUE_RETRIEVAL2: //closer chute to blue alliance wall
				return 3.607;
			case RED_OVERFLOW:
				return 3 * Math.PI / 2;
			case BLUE_OVERFLOW:
				return Math.PI / 2;
			case RED_GEAR1:  //labeled counter clockwise from the left side, from the red alliance point of view
				return Math.PI / 6;
			case RED_GEAR2:
				return Math.PI / 2;
			case RED_GEAR3:
				return 5 * Math.PI / 6;
			case BLUE_GEAR1: //labeled clockwise from left, from the red alliance point of view
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
