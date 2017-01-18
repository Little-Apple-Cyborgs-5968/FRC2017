package org.usfirst.frc.team5968.robot;

import org.usfirst.frc.team5968.robot.PortMap.CAN;
import org.usfirst.frc.team5968.robot.PortMap.DIO;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;

public class DriveBase {
	
	private static CANTalon leftMotorFront = new CANTalon(PortMap.portOf(CAN.LEFT_MOTOR_FRONT));
	private static CANTalon leftMotorBack = new CANTalon(PortMap.portOf(CAN.LEFT_MOTOR_BACK));
	private static CANTalon rightMotorFront = new CANTalon(PortMap.portOf(CAN.RIGHT_MOTOR_FRONT));
	private static CANTalon rightMotorBack = new CANTalon(PortMap.portOf(CAN.RIGHT_MOTOR_BACK));
	
	private static final double TOLERANCE = 0.5; //.5 degrees for angles, .5 inches for distance
	private static final double autoP = -.23;
	private static final double DRIVE_SPEED = .3;
	private static final double MOTOR_MAX_TEMP = 70;
	private static final int ENCODER_RESOLUTION = 512; //TODO: check this //counts/revolution
	private static final double WHEEL_DIAMETER = 6; //TODO: check this //inches
	
	private static boolean initialized = false;
	
	private static final double MAX_SPEED_RPM = 1500;
		
	public enum Motor{
		LEFT_FRONT,
		LEFT_BACK,
		RIGHT_FRONT,
		RIGHT_BACK;
	}
	
	public static void init(){
		initialized = true;
		
		leftMotorBack.changeControlMode(CANTalon.TalonControlMode.Follower);
		rightMotorBack.changeControlMode(CANTalon.TalonControlMode.Follower);
		leftMotorBack.set(PortMap.portOf(CAN.LEFT_MOTOR_FRONT));
		rightMotorBack.set(PortMap.portOf(CAN.RIGHT_MOTOR_FRONT));
		
		leftMotorFront.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		leftMotorFront.changeControlMode(TalonControlMode.Speed);
		
		leftMotorFront.configEncoderCodesPerRev(ENCODER_RESOLUTION);
		rightMotorFront.configEncoderCodesPerRev(ENCODER_RESOLUTION);
		
		leftMotorFront.setProfile(0);
		leftMotorFront.setF();
		leftMotorFront.setP();
		leftMotorFront.setI(0);
		leftMotorFront.setD();
		
		rightMotorFront.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		rightMotorFront.changeControlMode(TalonControlMode.Speed);
		rightMotorFront.setProfile(0);
		rightMotorFront.setF();
		rightMotorFront.setP();
		rightMotorFront.setI(0);
		rightMotorFront.setD();
		
	}
	
    private static void driveStraight(double initialSpeed){
    	NavXMXP.resetYaw();
    	if (Math.abs(NavXMXP.getYaw()) < TOLERANCE){
    		setRawProportion(initialSpeed, initialSpeed);
    	}
    	else{
    		if (NavXMXP.getYaw() < TOLERANCE){
    			setRawProportion(initialSpeed + NavXMXP.getYaw() * autoP, initialSpeed);
    			NavXMXP.resetYaw();
    		} 
    		else if (NavXMXP.getYaw() > TOLERANCE){
    			setRawProportion(initialSpeed, initialSpeed + NavXMXP.getYaw() * autoP);
    			NavXMXP.resetYaw();
    		}
    	}
    }
    
    public static boolean isMotorTooHot(Motor m){
    	switch(m){
    		case LEFT_FRONT:
    			return leftMotorFront.getTemperature() > MOTOR_MAX_TEMP;
    		case LEFT_BACK:
    			return leftMotorBack.getTemperature() > MOTOR_MAX_TEMP;
    		case RIGHT_FRONT:
    			return rightMotorFront.getTemperature() > MOTOR_MAX_TEMP;
    		case RIGHT_BACK:
    			return rightMotorBack.getTemperature() > MOTOR_MAX_TEMP;
    		default:
    			return true;
    	}
    }
    
    private static void setRawProportion(double leftSpeed, double rightSpeed){
    	leftMotorFront.set(leftSpeed * MAX_SPEED_RPM);
    	rightMotorFront.set(-1 * rightSpeed * MAX_SPEED_RPM);    	
    }
    
    @SuppressWarnings("unused")
	private static void setRawSpeed(double leftSpeed, double rightSpeed){
    	leftMotorFront.set(leftSpeed);
    	rightMotorFront.set(rightSpeed);
    }
    
    public static void resetEncoders(){
    	leftMotorFront.setPosition(0);
    	rightMotorFront.setPosition(0);
    }
    
    private static double getDistance(){
    	return Math.abs((leftMotorFront.getPosition() - rightMotorFront.getPosition()) / 2.0);
	}
    
    public static double getLeftSpeed(){
    	double rpm = leftMotorFront.getSpeed();
    	double inchesPerSecond = rpm * 1 / 60 * Math.PI * WHEEL_DIAMETER;
    	
    	return inchesPerSecond; //returns inches/second
    }
    
    public static double getRightSpeed(){
    	double rpm = rightMotorFront.getSpeed();
    	double inchesPerSecond = rpm * 1 / 60 * Math.PI * WHEEL_DIAMETER;
    	
    	return inchesPerSecond; //returns inches/second
    }
    
	public static double getLeftDistance(){
    	return leftMotorFront.getPosition() * Math.PI * WHEEL_DIAMETER; //returns inches
    }
    
    public static double getRightDistance(){
    	return rightMotorFront.getPosition() * Math.PI * WHEEL_DIAMETER; //returns inches
    }
	
    //The last time this method was called. It will reinitialize if the last call was more than
    //.5 seconds ago
    private static long driveLastCallMillis = System.currentTimeMillis();
    
	public static boolean driveDistance(double inches) {
		if(!initialized){
			init();
		}
		
		if(System.currentTimeMillis() - driveLastCallMillis >= 500){
			resetEncoders();
			accelerate(true);
		}
		
		driveLastCallMillis = System.currentTimeMillis();
		
		if (Math.abs(getDistance() - inches) >= TOLERANCE) {
    		if (getDistance() < inches) {
    			driveStraight(DRIVE_SPEED);
    		}
    		else if (getDistance() > inches) {
    			driveStraight(-DRIVE_SPEED); 
    		}
    		return false;
    	}
    	else {
    		DriveBase.setRawProportion(0, 0);
    		return true;
    	}
	}
	

    //The last time this method was called. It will reinitialize if the last call was more than
    //.5 seconds ago
	private static long rotateLastCallMillis = System.currentTimeMillis();
	
	public static boolean driveRotation(double degrees) {
		
		if(!initialized){
			init();
		}
		
		if(System.currentTimeMillis() - rotateLastCallMillis >= 500){
			NavXMXP.resetYaw();
		}
		
		rotateLastCallMillis = System.currentTimeMillis();
		
    	if (Math.abs(NavXMXP.getYaw() - degrees) >= TOLERANCE) {
    		if (NavXMXP.getYaw() > degrees) {
    			setRawProportion(-DRIVE_SPEED, DRIVE_SPEED);
    		} 
    		else if (NavXMXP.getYaw() < degrees){
    			setRawProportion(DRIVE_SPEED, -DRIVE_SPEED);
    		}
    		return false;
    	}    
    	else {
    		DriveBase.setRawProportion(0, 0);
    		return true;
    	}
	}
	
	public static void teleopDrive(double leftSpeed, double rightSpeed){
	
		setRawProportion(leftSpeed, rightSpeed);
	}
	
	private static void accelerate(boolean forward){
		double stop = DRIVE_SPEED;
		double increment = .01;
		
		if(!forward){
			stop *= -1;
			increment *= -1;
		}
		
		for(double i = 0; Math.abs(i) <= Math.abs(stop); i += increment){
			driveStraight(i);
			Timer.delay(.05);
		}
	}
	
	public static void driveToPoint(Point p){
		double currentX = PositionTracker.getCurrentPoint().getX();
		double currentY = PositionTracker.getCurrentPoint().getY();
		double targetX = p.getX();
		double targetY = p.getY();
		
		final double PROPORTION = .2;
		final double MAX_SPEED = .6;
		
		double leftSpeed = leftMotorFront.getSpeed();
		double rightSpeed = rightMotorFront.getSpeed();
		
		if(leftMotorFront.getSpeed() >= TOLERANCE){
			
			double robotPathSlope;
			if(NavXMXP.getYaw() == 0 || NavXMXP.getYaw() == 180 || NavXMXP.getYaw() == -180){
				robotPathSlope = 0.0001;
			}
			else{
				robotPathSlope = Math.tan(NavXMXP.convertAngleToRadians(NavXMXP.getYaw()));
			}
			
			//This assumes the robot is traveling in a straight line, which may be somewhat inaccurate.
			//I'm hoping the refresh rate is high enough that it won't matter, but we'll have to see.
			double projectedY = robotPathSlope * (targetX - currentX) + currentY;
			
			if((NavXMXP.getYaw() < 0 && targetY > projectedY) || (NavXMXP.getYaw() >= 0 && targetY < projectedY)){
				if(leftSpeed < MAX_SPEED){
					leftSpeed += PROPORTION * (targetY - projectedY);
				}
				else{
					rightSpeed -= PROPORTION * (targetY - projectedY);
				}
			}
			else{
				if(rightSpeed < MAX_SPEED){
					rightSpeed += PROPORTION * (targetY - projectedY);
				}
				else{
					leftSpeed -= PROPORTION * (targetY - projectedY);
				}
			}
			
			setRawProportion(leftSpeed, rightSpeed);
		}
		else{
			double turnAngle = Math.atan((targetX - currentX) / (targetY - currentY)) * 180 / Math.PI - NavXMXP.getYaw();
			double distance = Math.sqrt(Math.pow(targetX - currentX, 2) + Math.pow(targetY - currentY, 2));
		
			driveRotation(turnAngle);
			driveDistance(distance);
		}
	}
}
