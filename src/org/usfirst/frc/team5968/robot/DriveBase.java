package org.usfirst.frc.team5968.robot;

import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.text.Position;

import org.usfirst.frc.team5968.robot.Point.Setpoint;
import org.usfirst.frc.team5968.robot.PortMap.CAN;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Timer;

/**
 * Manages the drive train
 * 
 * @author BeijingStrongbow
 * @author allen458
 */
public class DriveBase {
	
	/**
	 * The Lead left motor
	 */
	private static CANTalon leftMotorLead = new CANTalon(PortMap.portOf(CAN.LEFT_MOTOR_LEAD));
	
	/**
	 * The Follow left motor
	 */
	private static CANTalon leftMotorFollow = new CANTalon(PortMap.portOf(CAN.LEFT_MOTOR_FOLLOW));
	
	/**
	 * The Lead right motor
	 */
	private static CANTalon rightMotorLead = new CANTalon(PortMap.portOf(CAN.RIGHT_MOTOR_LEAD));
	
	/**
	 * The Follow right motor
	 */
	private static CANTalon rightMotorFollow = new CANTalon(PortMap.portOf(CAN.RIGHT_MOTOR_FOLLOW));
	
	/**
	 * Used in 4 cases. Could be multiple constants, but having a ton of constants looks kind of bad. 
	 *  
	 * 1) How close to 0 degrees is considered driving straight in driveStraight()
	 * 2) How close to the target distance is considered at the target in driveDistance(double inches)
	 * 3) How fast the encoders need to be moving to be considered moving in driveToPoint(Point p)
	 * 
	 * This is measured in inches
	 */
	private static final double DISTANCE_TOLERANCE = 0.5; //.5 inches
	
	/**
	 * How close the robot needs to be when turning to be considered at the target angle
	 */
	private static final double ANGLE_TOLERANCE = 2; //degrees
	
	/**
	 * Proportion used to drive straight, using P (traditionally PID) control.
	 */
	private static final double autoP = -.23;
	
	/**
	 * Target speed for driving straight
	 */
	private static final double DRIVE_SPEED = .2;
	
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
	private static final double MAX_SPEED_RPM = 425;
	
	/**
     * The robot will keep trying to reach the target until it is this far away
     * from the target point (in inches).
     */
    private static final double DISTANCE_THRESHOLD = 2;
	
	/**
	 * All the motors we have in the drive train.
	 */
	public enum Motor{
		LEFT_Lead,
		LEFT_Follow,
		RIGHT_Lead,
		RIGHT_Follow;
	}
	
	/**
	 * Initializes the drive base.
	 */
	public static void init(){
		initialized = true;
		
		leftMotorFollow.changeControlMode(CANTalon.TalonControlMode.Follower);
		rightMotorFollow.changeControlMode(CANTalon.TalonControlMode.Follower);
		leftMotorFollow.set(PortMap.portOf(CAN.LEFT_MOTOR_LEAD));
		rightMotorFollow.set(PortMap.portOf(CAN.RIGHT_MOTOR_LEAD));
		
		leftMotorLead.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		leftMotorLead.changeControlMode(TalonControlMode.Speed);
		
		leftMotorLead.configEncoderCodesPerRev(ENCODER_RESOLUTION);
		
		leftMotorLead.configNominalOutputVoltage(0.0f, -0.0f);
		leftMotorFollow.configNominalOutputVoltage(0.0f, -0.0f);
		
		leftMotorLead.setProfile(0);
		leftMotorLead.setF(0); //.19
		leftMotorLead.setP(0); //.09
		leftMotorLead.setI(0);
		leftMotorLead.setD(0);
		
		rightMotorLead.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		rightMotorLead.changeControlMode(TalonControlMode.Speed);
		
		rightMotorLead.configEncoderCodesPerRev(ENCODER_RESOLUTION);
		
		rightMotorLead.configPeakOutputVoltage(12.0f, -12.0f);
		rightMotorFollow.configPeakOutputVoltage(12.0f, -12.0f);
				
		rightMotorLead.setProfile(0);
		rightMotorLead.setF(0); //.19
		rightMotorLead.setP(0); //.09
		rightMotorLead.setI(0);
		rightMotorLead.setD(0);
		
	}
	
	/**
	 * Drives straight, starting at a certain initial speed
	 * 
	 * @param initialSpeed The speed to start driving at. This speed will change somewhat as the motor speed is adjusted.
	 */
    public static void driveStraight(double initialSpeed){
    	NavXMXP.resetYaw();
    	
    	double angle = NavXMXP.getYaw();
    	
    	if(angle >= 180){
    		angle -= 360;
    	}
    	
    	if (Math.abs(angle) < DISTANCE_TOLERANCE){
    		setRawFraction(initialSpeed, initialSpeed);
    	}
    	else{
    		if (angle < DISTANCE_TOLERANCE){
    			setRawFraction(initialSpeed + angle * autoP, initialSpeed);
    		} 
    		else if (angle > DISTANCE_TOLERANCE){
    			setRawFraction(initialSpeed, initialSpeed + angle * autoP);
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
    		case LEFT_Lead:
    			return leftMotorLead.getTemperature() > MOTOR_MAX_TEMP;
    		case LEFT_Follow:
    			return leftMotorFollow.getTemperature() > MOTOR_MAX_TEMP;
    		case RIGHT_Lead:
    			return rightMotorLead.getTemperature() > MOTOR_MAX_TEMP;
    		case RIGHT_Follow:
    			return rightMotorFollow.getTemperature() > MOTOR_MAX_TEMP;
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
    	leftMotorLead.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
    	rightMotorLead.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
    	leftMotorLead.set(leftSpeed);
    	rightMotorLead.set(-1 * rightSpeed);    	
    }
    
    /**
     * Set the left and right motors to a certain absolute speed
     * 
     * @param leftSpeed The absolute speed (in RPM) the left motors should be set to.
     * @param rightSpeed The absolute speed (in RPM) the right motors should be set to.
     */
    @SuppressWarnings("unused")
	private static void setRawSpeed(double leftSpeed, double rightSpeed){
    	leftMotorLead.set(leftSpeed);
    	rightMotorLead.set(-rightSpeed);
    }
    
    /*
     * Reset the encoders to 0 displacement
     */
    public static void resetEncoders(){
    	leftMotorLead.setPosition(0);
    	rightMotorLead.setPosition(0);
    }
    
    /**
     * Get the average distance traveled, as measured by the left and right encoders
     * 
     * @return The average distance measured by the encoders. NOTE: this won't mean anything if the robot has turned.
     */
    private static double getDistance(){
    	return leftMotorLead.getPosition() - rightMotorLead.getPosition() / 2.0;
	}
    
    /**
     * Gets the speed currently measured by the left encoder
     * 
     * @return The speed currently measured by the left encoder
     */
    public static double getLeftSpeed(){
    	double rpm = leftMotorLead.getSpeed();
    	double inchesPerSecond = rpm * 1 / 60 * Math.PI * WHEEL_DIAMETER;
    	
    	return inchesPerSecond; //returns inches/second
    }
    
    /**
     * Gets the speed currently measured by the right encoder
     * 
     * @return The speed currently measured by the right encoder
     */
    public static double getRightSpeed(){
    	double rpm = rightMotorLead.getSpeed();
    	double inchesPerSecond = rpm * 1 / 60 * Math.PI * WHEEL_DIAMETER;
    	
    	return inchesPerSecond; //returns inches/second
    }
    
    /**
     * Gets the distance traveled by the left side of the robot
     * 
     * @return The distance traveled by the left side of the robot
     */
	public static double getLeftDistance(){
    	return leftMotorLead.getPosition() * Math.PI * WHEEL_DIAMETER; //returns inches
    }
    
	/**
     * Gets the distance traveled by the right side of the robot
     * 
     * @return The distance traveled by the right side of the robot
     */
    public static double getRightDistance(){
    	return rightMotorLead.getPosition() * Math.PI * WHEEL_DIAMETER; //returns inches
    }
	
    /**
     * The last time this method was called. It will reinitialize if the last call was more than .5 seconds ago
     */
    private static long driveLastCallMillis = System.currentTimeMillis();
    
    /**
     * The point the robot is at before starting to drive
     */
    private static Point startingPosition;
    
    /**
     * The point to drive to
     */
    private static Point endingPosition;
    
    /**
     * Whether to drive backwards
     */
    private static boolean backwards;
    
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

		if(System.currentTimeMillis() - driveLastCallMillis >= 100){
			startingPosition = PositionTracker.getCurrentPoint();
			double endingX;
			double endingY;
			if(inches < 0){
				endingX = startingPosition.getX() + inches * Math.sin((NavXMXP.getYaw() + 180) * Math.PI / 180);
				endingY = startingPosition.getY() + inches * Math.cos((NavXMXP.getYaw() + 180) * Math.PI / 180);
				backwards = true;
				accelerate(-DRIVE_SPEED, 0);
			}
			else{
				endingX = startingPosition.getX() + inches * Math.sin((NavXMXP.getYaw()) * Math.PI / 180);
				endingY = startingPosition.getY() + inches * Math.cos((NavXMXP.getYaw()) * Math.PI / 180);
				backwards = false;
				accelerate(DRIVE_SPEED, 0);
			}
			endingPosition = new Point(endingX, endingY);
		}
		
		driveLastCallMillis = System.currentTimeMillis();
		
		if (Math.abs(Math.pow(PositionTracker.getCurrentPoint().getX() - endingPosition.getX(), 2) + Math.pow(PositionTracker.getCurrentPoint().getY() - endingPosition.getY(), 2)) >= DISTANCE_TOLERANCE) {
    		if (!backwards) {
    			driveStraight(DRIVE_SPEED);
    		}
    		else {
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
	 * The direction to turn. 0 = clockwise, 1 = counter clockwise
	 */
	private static int direction = -1;
	
	/**
	 * Drive to a certain angle (0 to 360 degrees)
	 * 
	 * @param degrees The angle to turn to
	 * @return Whether the turning is finished
	 */
	public static boolean driveRotation(double degrees) {
		
		if(!initialized){
			init();
		}
		
		if(System.currentTimeMillis() - rotateLastCallMillis >= 100){			
			if(Math.abs(NavXMXP.getYaw() - degrees) <= ANGLE_TOLERANCE){
				return true;
			}
			else if(Math.abs(NavXMXP.getYaw() - degrees) > 180){
				if(degrees < NavXMXP.getYaw()){
					direction = 0;
				}
				else {
					direction = 1;
				}
			}
			else{
				if(degrees > NavXMXP.getYaw()){
					direction = 1;
				}
				else{
					direction = 0;
				}
			}
		}
		
		rotateLastCallMillis = System.currentTimeMillis();
		
    	if (Math.abs(NavXMXP.getYaw() - degrees) >= ANGLE_TOLERANCE) {
    		if(direction == 1){
    			if(Math.abs(NavXMXP.getYaw() - degrees) <= ANGLE_TOLERANCE * 10){ //yes, 10 is a very magic number
    				setRawFraction(-.1, .1);
    			}
    			else{
        			setRawFraction(-DRIVE_SPEED, DRIVE_SPEED);
    			}
    		}
    		else if(direction == 0){
    			if(Math.abs(NavXMXP.getYaw() - degrees) <= ANGLE_TOLERANCE * 10){
    				setRawFraction(.1, -.1);
    			}
    			else{
        			setRawFraction(DRIVE_SPEED, -DRIVE_SPEED);
    			}
    		}
    		else{
    			return true;
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
	public static void accelerate(double finalSpeed, double currentSpeed){
		double increment = .01;
		if(finalSpeed < currentSpeed){
			increment *= -1;
		}
		
		while(Math.abs(currentSpeed - finalSpeed) > .011){
			currentSpeed += increment;
			setRawFraction(currentSpeed, currentSpeed);
			Timer.delay(.025);
		}
		
		setRawFraction(finalSpeed, finalSpeed);
	}
	
	/**
	 * Drive to a point on the field. At the moment, this is somewhat imprecise (not to mention untested),
	 * and uses what might be called P control.
	 * 
	 * @param p The point to drive to
	 * @param stop Whether top stop before driving to the point
	 */
	public static void driveToPoint(Point p, boolean stop){
		double currentX = PositionTracker.getCurrentPoint().getX();
		double currentY = PositionTracker.getCurrentPoint().getY();
		double targetX = p.getX();
		double targetY = p.getY();
		
		final double PROPORTION = .2;
		final double MAX_SPEED = .6;
		
		double leftSpeed = leftMotorLead.getSpeed();
		double rightSpeed = rightMotorLead.getSpeed();
		
		if(stop){
			setRawSpeed(0, 0);
		}
		
		if(leftMotorLead.getSpeed() >= DISTANCE_TOLERANCE && !stop){
			
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
			double turnAngle = Math.atan((targetX - currentX) / (targetY - currentY)) * 180 / Math.PI;
			double distance = Math.sqrt(Math.pow(targetX - currentX, 2) + Math.pow(targetY - currentY, 2));
		
			driveRotation(turnAngle);
			driveDistance(distance);
		}
	}
	
	/**
	 * Drive along a path. It's nonblocking, so call it from the main control loop
	 * 
	 * @param points The list of points defining the path to drive.
	 * @param stopBetweenPoints Whether to stop between driving to each point
	 */
	public static void drivePath(LinkedBlockingQueue<Point> points, boolean stopBetweenPoints){
		if(points.isEmpty()){
			Setpoint destination = PositionTracker.findNearestSetpoint(PositionTracker.getCurrentPoint());
			driveToPoint(Point.getCoordinates(destination), stopBetweenPoints);
			
			if(destination == Setpoint.RED_BOILER || destination == Setpoint.BLUE_BOILER){
				driveRotation(Point.getCorrectAngle(destination));
				//correct for error with vision processing
				driveStraight(52 - .5 * Robot.getRobotLength());
			}
			else{
				driveRotation(Point.getCorrectAngle(destination));
				driveStraight(Point.getStopDistance() - .5 * Robot.getRobotLength());
			}
		}
		else{			
			Point current = PositionTracker.getCurrentPoint();
			Point target = points.peek();
			
			driveToPoint(target, stopBetweenPoints);
			
			if(Math.sqrt(Math.pow(current.getX() - target.getX(), 2) + Math.pow(current.getY() - target.getY(), 2)) <= DISTANCE_THRESHOLD){
				points.poll();
			}
		}
	}
}
