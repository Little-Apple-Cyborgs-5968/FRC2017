package org.usfirst.frc.team5968.robot;

import org.usfirst.frc.team5968.robot.Point.Setpoint;

/**
 * Keeps track of the current position of the robot.
 * 
 * @author BejingStrongbow
 */
public class PositionTracker {
	
	/*
	 * The robot's current X coordinate. (0,0) is the lower-left corner from the red-alliance point of view
	 * (i.e. blue alliance retrieval bins)
	 */
	private static double x;
	
	/**
	 * The robot's current Y coordinate. //(0,0) is the lower-left corner from the red-alliance point of view
	 * (i.e. blue alliance retrieval bins)
	 */
	private static double y;
	
	/**
	 * The robot's angle at the last iteration, measured from 0 to 2 pi radians.
	 */
	private static double previousAngle;
	
	/**
	 * The threshold for considering an accelerometer or encoder reading as movement.
	 */
	private static final double MOVEMENT_THRESHOLD = .004;
	
	/**
	 * The threshold for considering a driving path as a straight line. Otherwise, it's
	 * considered a curve.
	 */
	private static final double STRAIGHT_LINE_THRESHOLD = 1000000;

	/**
	 * The position of the left encoder on the last iteration
	 */
	private static double leftEncoderPrev = 0;
	
	/**
	 * The position of the right encoder on the last iteration
	 */
	private static double rightEncoderPrev = 0;
	/**
	 * Initializes the position tracker
	 * 
	 * @param xInitial The initial X coordinate
	 * @param yInitial The initial Y coordinate
	 */
	public static void init(double xInitial, double yInitial){
		x = xInitial;
		y = yInitial;
		leftEncoderPrev = DriveBase.getLeftDistance();
		rightEncoderPrev = DriveBase.getRightDistance();
	}
	
	public static void resetCoordinates(){
		x = 0;
		y = 0;
	}
	
	/**
	 * Updates the robot's current position based on the most recent accelerometer and encoder readings
	 * 
	 * @return The robot's current position
	 */
	public static Point getCurrentPoint(){
		updateCoordinates();
		return new Point(x, y);
	}
	
	/**
	 * Update the robot's current position based on the most recent accelerometer and encoder readings
	 */
	public static void updateCoordinates(){
		double xDisplacementFromAccel = NavXMXP.getDisplacementX() * 39.3701; //convert meters to inches
		double yDisplacementFromAccel = NavXMXP.getDisplacementY() * 39.3701; //convert meters to inches
		double leftEncoderDistance = DriveBase.getLeftDistance() - leftEncoderPrev; //inches
		double rightEncoderDistance = DriveBase.getRightDistance() - rightEncoderPrev; //inches
		double angle = NavXMXP.getYaw() * Math.PI / 180; //angle from 0 to 2 pi
				
		//if both the accelerometer and encoders registered movement
		if((Math.abs(leftEncoderDistance) >= MOVEMENT_THRESHOLD || Math.abs(rightEncoderDistance) >= MOVEMENT_THRESHOLD) && 
				/*(Math.abs(xDisplacementFromAccel) >= MOVEMENT_THRESHOLD || Math.abs(yDisplacementFromAccel) >= MOVEMENT_THRESHOLD) &&*/
				((leftEncoderDistance <= 0 && rightEncoderDistance >= 0) ||
				(leftEncoderDistance >= 0 && rightEncoderDistance <= 0))){
			//approximate path as a straight line
			if(Math.abs(leftEncoderDistance - rightEncoderDistance) <= STRAIGHT_LINE_THRESHOLD){
				System.out.println("yoyo line");
				double avgDistance = (rightEncoderDistance - leftEncoderDistance) / 2;
				x += avgDistance * Math.sin(angle);
				y += avgDistance * Math.cos(angle);
			}
			
			//approximate path as a circular arc
			else{
				if(leftEncoderDistance == rightEncoderDistance){
					return;
				}
				double rightRadius = (rightEncoderDistance * Robot.getRobotWidth()) / (-leftEncoderDistance - rightEncoderDistance);
				double leftRadius;
				
				//convert angle to 0 to 2 pi radians
				angle = NavXMXP.convertAngleToRadians(angle);
				
				//the angle to the center of the circle
				double theta;
				double previousTheta;
				double initialX;
				double initialY;
				double x;
				double y;
				
				if(rightEncoderDistance > leftEncoderDistance){
					rightRadius *= -1; //not sure what this is
					leftRadius = rightRadius - Robot.getRobotWidth();
					
					theta = angle + Math.PI / 2;
					previousTheta = previousAngle + Math.PI / 2;
					
					//the radius of the robot's turning path
					double radius = (leftRadius + rightRadius) / 2;
					initialX = radius * Math.sin(previousTheta);
					initialY = -1 * radius * Math.cos(previousTheta);
					x = radius * Math.sin(theta);
					y = -1 * radius * Math.cos(theta);
				}
				else{
					leftRadius = rightRadius + Robot.getRobotWidth();

					theta = angle + Math.PI / 2;
					previousTheta = previousAngle + Math.PI / 2;
					
					//the radius of the robot's turning path
					double radius = (leftRadius + rightRadius) / 2;
					initialX = -1 * radius * Math.sin(previousTheta);
					initialY = radius * Math.cos(previousTheta);
					x = -1 * radius * Math.sin(theta);
					y = radius * Math.cos(theta);
					
				}
				
				double dX = x - initialX;
				double dY = y - initialY;
				
				PositionTracker.x += dX;
				PositionTracker.y += dY;
			}
		}
		
		//if the encoders didn't read movement but the accelerometer does
		else if(Math.abs(xDisplacementFromAccel) >= MOVEMENT_THRESHOLD || Math.abs(yDisplacementFromAccel) >= MOVEMENT_THRESHOLD){
			x += xDisplacementFromAccel;
			y += yDisplacementFromAccel;
		}
		
		//do nothing if the encoders read movement but the accelerometer didn't
		
		//System.out.println("x: " + x + " y: " + y);
		previousAngle = angle;
		leftEncoderPrev += leftEncoderDistance;
		rightEncoderPrev += rightEncoderDistance;
		NavXMXP.resetAccelerometer();
	}
	
	/**
	 * Find the closest hopper/chute/boiler to a point on the field
	 * 
	 * @param current The current point. Likely the end of the user's path selection
	 * on the touchscreen, and not the current position of the robot.
	 */
	public static Setpoint findNearestSetpoint(Point current){
		Setpoint closestPoint = Setpoint.BLUE_BOILER; //just a placeholder value
		double closestDistance = 0;
		
		double distance;
		for(Setpoint s : Setpoint.values()){
			Point setpoint = Point.getCoordinates(s); //knock knock? who's there? *Silence*
			
			distance = Math.sqrt(Math.pow(current.getX() - setpoint.getX(), 2) + (Math.pow(current.getY() - setpoint.getY(), 2)));
			if(distance < closestDistance){
				closestPoint = s;
				closestDistance = distance;
			}
		}
		
		return closestPoint;
	}
}
