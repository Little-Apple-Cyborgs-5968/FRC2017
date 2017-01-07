package org.usfirst.frc.team5968.robot;

import org.usfirst.frc.team5968.robot.PortMap.CAN;
import org.usfirst.frc.team5968.robot.PortMap.DIO;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;

public class DriveBase {
	
	private static NavXMXP navX = new NavXMXP();
	private static CANTalon leftMotorFront = new CANTalon(PortMap.portOf(CAN.LEFT_MOTOR_FRONT));
	private static CANTalon leftMotorBack = new CANTalon(PortMap.portOf(CAN.LEFT_MOTOR_BACK));
	private static CANTalon rightMotorFront = new CANTalon(PortMap.portOf(CAN.RIGHT_MOTOR_FRONT));
	private static CANTalon rightMotorBack = new CANTalon(PortMap.portOf(CAN.RIGHT_MOTOR_BACK));
	
	private static Encoder leftEncoder = new Encoder(PortMap.portOf(DIO.LEFT_ENCODER1), PortMap.portOf(DIO.LEFT_ENCODER2));
	private static Encoder rightEncoder = new Encoder(PortMap.portOf(DIO.RIGHT_ENCODER1), PortMap.portOf(DIO.RIGHT_ENCODER2));
	
	private static final double TOLERANCE = 0.5; //.5 degrees for angles, .5 inches for distance
	private static final double autoP = -.23;
	private static double teleopPLeft = 1;
	private static double teleopPRight = 1;
	private static final double DRIVE_SPEED = .3;
	private static final double MOTOR_MAX_TEMP = 70;
	private static final double MAX_SPEED = 36;
	
	private static boolean initialized = false;
	
	public enum Motor{
		LEFT_FRONT,
		LEFT_BACK,
		RIGHT_FRONT,
		RIGHT_BACK;
	}
	
	public static void init(){
		initialized = true;
		leftEncoder.setDistancePerPulse(.042455); //this is in inches
		rightEncoder.setDistancePerPulse(.042455); //this is in inches
	}
	
    private static void driveStraight(double initialSpeed){
    	navX.resetYaw();
    	if (Math.abs(navX.getYaw()) < TOLERANCE){
    		setRaw(initialSpeed, initialSpeed);
    	}
    	else{
    		if (navX.getYaw() < TOLERANCE){
    			setRaw(initialSpeed + navX.getYaw() * autoP, initialSpeed);
    			navX.resetYaw();
    		} 
    		else if (navX.getYaw() > TOLERANCE){
    			setRaw(initialSpeed, initialSpeed + navX.getYaw() * autoP);
    			navX.resetYaw();
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
    
    private static void setRaw(double leftSpeed, double rightSpeed){
    	leftMotorFront.set(leftSpeed);
    	leftMotorBack.set(leftSpeed);
    	rightMotorFront.set(-1 * rightSpeed);
    	rightMotorBack.set(-1 * rightSpeed);
    	
    	
    }
    
    private static void resetEncoders(){
    	leftEncoder.reset();
    	rightEncoder.reset();
    }
    
    private static double getDistance(){
    	return Math.abs((leftEncoder.getDistance() - rightEncoder.getDistance()) / 2.0);
	}
    
    public static double getLeftSpeed(){
    	return leftEncoder.getRate(); //returns inches/second
    }
    
    public static double getRightSpeed(){
    	return rightEncoder.getRate(); //returns inches/second
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
    		DriveBase.setRaw(0, 0);
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
			navX.resetYaw();
		}
		
		rotateLastCallMillis = System.currentTimeMillis();
		
    	if (Math.abs(navX.getYaw() - degrees) >= TOLERANCE) {
    		if (navX.getYaw() > degrees) {
    			setRaw(-DRIVE_SPEED, DRIVE_SPEED);
    		} 
    		else if (navX.getYaw() < degrees){
    			setRaw(DRIVE_SPEED, -DRIVE_SPEED);
    		}
    		return false;
    	}    
    	else {
    		DriveBase.setRaw(0, 0);
    		return true;
    	}
	}
	
	public static void teleopDrive(double leftSpeed, double rightSpeed){
		if(!initialized){
			init();
		}
		setRaw(leftSpeed * teleopPLeft, rightSpeed * teleopPRight);
		
		Robot.waitMillis(10);
		teleopPLeft = leftSpeed / (leftEncoder.getRate() / MAX_SPEED);
		teleopPRight = rightSpeed / (rightEncoder.getRate() / MAX_SPEED);
		
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
}