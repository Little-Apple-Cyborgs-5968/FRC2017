package org.usfirst.frc.team5968.robot;

import java.util.concurrent.LinkedBlockingQueue;

import org.usfirst.frc.team5968.robot.Point.Setpoint;
import org.usfirst.frc.team5968.robot.PortMap.CAN;
import org.usfirst.frc.team5968.robot.PortMap.DIO;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;

/**
 * Manages the drive train
 * 
 * @author BeijingStrongbow
 * @author allen458
 */
public class DriveBase {
	
	/**
	 * The front left motor
	 */
	private static CANTalon leftMotorFront = new CANTalon(PortMap.portOf(CAN.LEFT_MOTOR_FRONT));
	
	/**
	 * The back left motor
	 */
	private static CANTalon leftMotorBack = new CANTalon(PortMap.portOf(CAN.LEFT_MOTOR_BACK));
	
	/**
	 * The front right motor
	 */
	private static CANTalon rightMotorFront = new CANTalon(PortMap.portOf(CAN.RIGHT_MOTOR_FRONT));
	
	/**
	 * The back right motor
	 */
	private static CANTalon rightMotorBack = new CANTalon(PortMap.portOf(CAN.RIGHT_MOTOR_BACK));
	
	/**
	 * Used in 4 cases. Could be multiple constants, but having a ton of constants looks kind of bad. 
	 *  
	 * 1) How close to 0 degrees is considered driving straight in driveStraight()
	 * 2) How close to the target distance is considered at the target in driveDistance(double inches)
	 * 3) How close to the target angle is considered at the target angle in driveRotation(double degrees)
	 * 4) How fast the encoders need to be moving to be considered moving in driveToPoint(Point p)
	 * 
	 * This is measured in degrees for angles and inches for distance.
	 */
	private static final double TOLERANCE = 0.5; //.5 degrees for angles, .5 inches for distance
	
	/**
	 * Proportion used to drive straight, using P (traditionally PID) control.
	 */
	private static final double autoP = -.23;
	
	/**
	 * Target speed for driving straight
	 */
	private static final double DRIVE_SPEED = .3;
	
	/**
	 * The maximum temperature of the motor controllers that's considered "safe." Not exactly sure what this should be.
	 * This is in degrees Celsius.
	 */
	private static final double MOTOR_MAX_TEMP = 70;
	
	/**
	 * The resolution of the encoder readings in counts/revolution
	 */
	private static final int ENCODER_RESOLUTION = 2048;
	
	/**
	 * Wheel diameter in inches
	 */
	private static final double WHEEL_DIAMETER = 6; //TODO: check this
	
	/**
	 * Whether the drive base has been initialized
	 */
	private static boolean initialized = false;
	
	/**
	 * The maximum speed the robot can drive. Used to scale the joystick inputs to an absolute speed.
	 */
	private static final double MAX_SPEED_RPM = 1500;
	
	/**
     * The robot will keep trying to reach the target until it is this far away
     * from the target point (in inches).
     */
    private static final double DISTANCE_THRESHOLD = 2;
	
	/**
	 * All the motors we have in the drive train.
	 */
	public enum Motor{
		LEFT_FRONT,
		LEFT_BACK,
		RIGHT_FRONT,
		RIGHT_BACK;
	}
	
	/**
	 * Initializes the drive base.
	 */
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
	
	/**
	 * Drives straight, starting at a certain initial speed
	 * 
	 * @param initialSpeed The speed to start driving at. This speed will change somewhat as the motor speed is adjusted.
	 */
    private static void driveStraight(double initialSpeed){
    	NavXMXP.resetYaw();
    	if (Math.abs(NavXMXP.getYaw()) < TOLERANCE){
    		setRawFraction(initialSpeed, initialSpeed);
    	}
    	else{
    		if (NavXMXP.getYaw() < TOLERANCE){
    			setRawFraction(initialSpeed + NavXMXP.getYaw() * autoP, initialSpeed);
    			NavXMXP.resetYaw();
    		} 
    		else if (NavXMXP.getYaw() > TOLERANCE){
    			setRawFraction(initialSpeed, initialSpeed + NavXMXP.getYaw() * autoP);
    			NavXMXP.resetYaw();
    		}
    	}
    }
    
    /**
     * Get whether a motor is too hot, as determined by comparing its temperature to MOTOR_MAX_TEMP.
     * 
     * @param m The motor whose temperature should be checked
     * @return Whether the specified motor is too hot.
     */
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
    
    /**
     * Set the left and right motors to a certain fraction of full speed
     * 
     * @param leftSpeed The fraction of full speed the left motors should be set to.
     * @param rightSpeed The fraction of full speed the right motors should be set to.
     */
    public static void setRawFraction(double leftSpeed, double rightSpeed){
    	leftMotorFront.set(leftSpeed * MAX_SPEED_RPM);
    	rightMotorFront.set(-1 * rightSpeed * MAX_SPEED_RPM);    	
    }
    
    /**
     * Set the left and right motors to a certain absolute speed
     * 
     * @param leftSpeed The absolute speed (in RPM) the left motors should be set to.
     * @param rightSpeed The absolute speed (in RPM) the right motors should be set to.
     */
    @SuppressWarnings("unused")
	private static void setRawSpeed(double leftSpeed, double rightSpeed){
    	leftMotorFront.set(leftSpeed);
    	rightMotorFront.set(rightSpeed);
    }
    
    /*
     * Reset the encoders to 0 displacement
     */
    public static void resetEncoders(){
    	leftMotorFront.setPosition(0);
    	rightMotorFront.setPosition(0);
    }
    
    /**
     * Get the average distance traveled, as measured by the left and right encoders
     * 
     * @return The average distance measured by the encoders. NOTE: this won't mean anything if the robot has turned.
     */
    private static double getDistance(){
    	return Math.abs((leftMotorFront.getPosition() - rightMotorFront.getPosition()) / 2.0);
	}
    
    /**
     * Gets the speed currently measured by the left encoder
     * 
     * @return The speed currently measured by the left encoder
     */
    public static double getLeftSpeed(){
    	double rpm = leftMotorFront.getSpeed();
    	double inchesPerSecond = rpm * 1 / 60 * Math.PI * WHEEL_DIAMETER;
    	
    	return inchesPerSecond; //returns inches/second
    }
    
    /**
     * Gets the speed currently measured by the right encoder
     * 
     * @return The speed currently measured by the right encoder
     */
    public static double getRightSpeed(){
    	double rpm = rightMotorFront.getSpeed();
    	double inchesPerSecond = rpm * 1 / 60 * Math.PI * WHEEL_DIAMETER;
    	
    	return inchesPerSecond; //returns inches/second
    }
    
    /**
     * Gets the distance traveled by the left side of the robot
     * 
     * @return The distance traveled by the left side of the robot
     */
	public static double getLeftDistance(){
    	return leftMotorFront.getPosition() * Math.PI * WHEEL_DIAMETER; //returns inches
    }
    
	/**
     * Gets the distance traveled by the right side of the robot
     * 
     * @return The distance traveled by the right side of the robot
     */
    public static double getRightDistance(){
    	return rightMotorFront.getPosition() * Math.PI * WHEEL_DIAMETER; //returns inches
    }
	
    /**
     * The last time this method was called. It will reinitialize if the last call was more than .5 seconds ago
     */
    private static long driveLastCallMillis = System.currentTimeMillis();
    
    /**
     * Drive straight for a certain number of inches
     * 
     * @param inches The number of inches to drive
     * @return whether the driving is complete
     */
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
    		DriveBase.setRawFraction(0, 0);
    		return true;
    	}
	}
	

    /**
     * The last time this method was called. It will reinitialize if the last call was more than .5 seconds ago
     */
	private static long rotateLastCallMillis = System.currentTimeMillis();
	
	/**
	 * Drive through a certain degree angle. NOTE: the robot will turn through this angle,
	 * not to this angle. So if the robot is at -30 degrees and this method is called with
	 * degrees = 60, then the robot will turn to 30 degrees.
	 * 
	 * @param degrees The number of degrees to turn through.
	 * @return Whether the turning is finished
	 */
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
    			setRawFraction(-DRIVE_SPEED, DRIVE_SPEED);
    		} 
    		else if (NavXMXP.getYaw() < degrees){
    			setRawFraction(DRIVE_SPEED, -DRIVE_SPEED);
    		}
    		return false;
    	}    
    	else {
    		DriveBase.setRawFraction(0, 0);
    		return true;
    	}
	}
	
	/**
	 * Called when the human is in control of the robot, using the joysticks
	 * 
	 * @param leftSpeed The fraction of full speed to set the left motors to
	 * @param rightSpeed The fraction of full speed to set the right motors to
	 */
	public static void teleopDrive(double leftSpeed, double rightSpeed){
	
		setRawFraction(leftSpeed, rightSpeed);
	}
	
	/**
	 * Accelerate to DRIVE_SPEED. Without this, the robot jerks itself out of alignment,
	 * hurting its straight driving accuracy.
	 * 
	 * @param forward Whether to accelerate forward (the alternate is obviously accelerating backward)
	 */
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
	
	/**
	 * Drive to a point on the field. At the moment, this is somewhat imprecise (not to mention untested),
	 * and uses what might be called P control.
	 * 
	 * @param p The point to drive to
	 */
	private static void driveToPoint(Point p){
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
			
			setRawFraction(leftSpeed, rightSpeed);
		}
		else{
			double turnAngle = Math.atan((targetX - currentX) / (targetY - currentY)) * 180 / Math.PI - NavXMXP.getYaw();
			double distance = Math.sqrt(Math.pow(targetX - currentX, 2) + Math.pow(targetY - currentY, 2));
		
			driveRotation(turnAngle);
			driveDistance(distance);
		}
	}
	
	/**
	 * Drive along a path. It's nonblocking, so call it from the main control loop
	 * 
	 * @param points The list of points defining the path to drive.
	 */
	public static void drivePath(LinkedBlockingQueue<Point> points){
		if(points.isEmpty()){
			Setpoint destination = PositionTracker.findNearestSetpoint(PositionTracker.getCurrentPoint());
			driveToPoint(Point.getCoordinates(destination));
			
			driveRotation(NavXMXP.getYaw() - Point.getCorrectAngle(destination));
			driveStraight(30);
		}
		
		driveToPoint(points.peek());
		
		Point current = PositionTracker.getCurrentPoint();
		Point target = points.peek();
		if(Math.sqrt(Math.pow(current.getX() - target.getX(), 2) + Math.pow(current.getY() - target.getY(), 2)) <= DISTANCE_THRESHOLD){
			points.poll();
		}
	}
}
