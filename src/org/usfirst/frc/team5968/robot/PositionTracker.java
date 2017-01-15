package org.usfirst.frc.team5968.robot;

/*
 * Keeps track of the current position of the robot.
 */
public class PositionTracker {
	
	//(0,0) is the lower-left corner from the red-alliance point of view
	//(i.e. blue alliance retrieval bins)
	private static double x;
	
	private static double y;
	
	private static double previousAngle; //radians
	
	private static final double MOVEMENT_THRESHOLD = .05;
	
	private static final double STRAIGHT_LINE_THRESHOLD = .5;
	
	private static final double ROBOT_WIDTH = 14.031; //inches
	
	public static void init(double xInitial, double yInitial){
		x = xInitial;
		y = yInitial;
	}
	
	public static double getCurrentX(){
		updateCoordinates();
		return x;
	}
	
	public double getCurrentY(){
		updateCoordinates();
		return y;
	}
	
	
	private static void updateCoordinates(){
		double xDisplacementFromAccel = NavXMXP.getDisplacementX() * 39.3701; //convert meters to inches
		double yDisplacementFromAccel = NavXMXP.getDisplacementY() * 39.3701; //convert meters to inches
		double leftEncoderDistance = DriveBase.getLeftDistance(); //inches
		double rightEncoderDistance = DriveBase.getRightDistance(); //inches
		double angle = NavXMXP.getYaw() * Math.PI / 180; //angle from -pi / 2 to pi / 2
		double avgDistance;
		
		//if both the accelerometer and encoders registered movement
		if((Math.abs(leftEncoderDistance) >= MOVEMENT_THRESHOLD || Math.abs(rightEncoderDistance) >= MOVEMENT_THRESHOLD) && 
				(Math.abs(xDisplacementFromAccel) >= MOVEMENT_THRESHOLD || Math.abs(yDisplacementFromAccel) >= MOVEMENT_THRESHOLD) &&
				((leftEncoderDistance >= 0 && rightEncoderDistance >= 0) ||
				(leftEncoderDistance <= 0 && rightEncoderDistance <= 0))){
			//approximate path as a straight line
			if(Math.abs(leftEncoderDistance - rightEncoderDistance) < STRAIGHT_LINE_THRESHOLD){
				avgDistance = (leftEncoderDistance + rightEncoderDistance) / 2;
				x += avgDistance * Math.sin(angle);
				y += avgDistance * Math.cos(angle);
			}
			
			//approximate path as a circular arc
			else{
				if(leftEncoderDistance == -1 * rightEncoderDistance){
					return;
				}
				double rightRadius = (rightEncoderDistance * ROBOT_WIDTH) / (leftEncoderDistance - rightEncoderDistance);
				double leftRadius;
				
				//convert angle to 0 to 2 pi radians
				if(angle >= 0 && angle <= Math.PI / 2){
					angle = Math.PI / 2 - angle;
				}
				else if(angle > Math.PI / 2){
					angle = Math.PI + angle;
				}
				else if(angle >= -1 * Math.PI / 2 && angle < 0){
					angle = Math.PI / 2 - angle; // angle is negative
				}
				else{ //if angle > -180
					angle = Math.PI + (-1 * angle - Math.PI / 2);
				}
				//the angle to the center of the circle
				double theta;
				double previousTheta;
				
				if(rightEncoderDistance > leftEncoderDistance){
					rightRadius *= -1;
					leftRadius = rightRadius - ROBOT_WIDTH;
					avgDistance = (leftEncoderDistance + rightEncoderDistance) / 2;
					theta = angle - Math.PI / 2;
					previousTheta = previousAngle - Math.PI / 2;
				}
				else{
					leftRadius = rightRadius + ROBOT_WIDTH;
					avgDistance = (leftEncoderDistance + rightEncoderDistance) / 2 * -1;
					theta = angle + Math.PI / 2;
					previousTheta = previousAngle + Math.PI / 2;
				}
				//the radius of the robot's turning path
				double radius = (leftRadius + rightRadius) / 2;
				double initialX = radius * Math.cos(previousTheta);
				double initialY = radius * Math.sin(previousTheta);
				double x = radius * Math.cos(theta);
				double y = radius * Math.sin(theta);
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
		
		previousAngle = angle;
		System.out.println("x: " + x + " y: " + y);
		DriveBase.resetEncoders();
		NavXMXP.resetAccelerometer();
	}
}
