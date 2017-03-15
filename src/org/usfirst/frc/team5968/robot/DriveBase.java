package org.usfirst.frc.team5968.robot;

import org.usfirst.frc.team5968.robot.PortMap.CAN;
import org.usfirst.frc.team5968.robot.PortMap.PWM;

import com.ctre.CANTalon;
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
	 * The left side drive encder
	 */
	private static Encoder leftEncoder = new Encoder(PortMap.portOf(PWM.LEFT_DRIVE_ENCODER_A), PortMap.portOf(PWM.LEFT_DRIVE_ENCODER_B));
	
	/**
	 * The right side drive encoder
	 */
	private static Encoder rightEncoder = new Encoder(PortMap.portOf(PWM.RIGHT_DRIVE_ENCODER_A), PortMap.portOf(PWM.RIGHT_DRIVE_ENCODER_B));
	
	/**
	 * Used in 2 cases. Could be multiple constants, but having a ton of constants looks kind of bad. 
	 *  
	 * 1) How close to the target distance is considered at the target in driveDistance(double inches)
	 * 2) How fast the encoders need to be moving to be considered moving in driveToPoint(Point p)
	 * 
	 * This is measured in inches
	 */
	private static final double DISTANCE_TOLERANCE = .25; //inches
	
	/**
	 * How close to the target angle the gyro needs to read to be considered driving straight
	 */
	private static final double STRAIGHT_LINE_TOLERANCE = .008;
	
	/**
	 * How close the robot needs to be when turning to be considered at the target angle
	 */
	private static final double ANGLE_TOLERANCE = 1; //degrees
	
	/**
	 * Proportion used to drive straight, using P (traditionally PID) control.
	 */
	private static final double autoP = .01;
	
	/**
	 * Target speed for driving straight
	 */
	private static final double DRIVE_SPEED = .3;
	
	/**
	 * Target speed for turning
	 */
	private static final double TURN_SPEED = .25;
	
	/**
	 * The maximum allowable speed while the robot is driving.
	 */
	private static final double MAX_SPEED = DRIVE_SPEED + .1;
	
	/**
	 * The maximum temperature of the motor controllers that's considered "safe." Not exactly sure what this should be.
	 * This is in degrees Celsius.
	 */
	private static final double MOTOR_MAX_TEMP = 70;
	
	/**
	 * The resolution of the drive encoders, in counts/revolution
	 */
	private static final int ENCODER_RESOLUTION = 2048;
	
	/**
	 * Wheel diameter in inches
	 */
	private static final double WHEEL_DIAMETER = 6.6;
	
	/**
	 * The maximum speed the robot can drive in inches per second
	 */
	public static final double MAX_SPEED_INCHES = 146.87; //inches per second, based on 425 RPM max
    
    /**
     * The distance to accelerate over when driving a distance, in inches.
     */
    private static final double ACCELERATE_DISTANCE = 6;
    
    /**
     * The distance to decelerate over when driving a distance, in inches
     */
    private static final double DECELERATE_DISTANCE = 10;
    
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
		leftMotorFollow.changeControlMode(CANTalon.TalonControlMode.Follower);
		rightMotorFollow.changeControlMode(CANTalon.TalonControlMode.Follower);
		leftMotorFollow.set(PortMap.portOf(CAN.LEFT_MOTOR_LEAD));
		rightMotorFollow.set(PortMap.portOf(CAN.RIGHT_MOTOR_LEAD));
						
		leftMotorLead.configNominalOutputVoltage(0.0f, -0.0f);
		leftMotorFollow.configNominalOutputVoltage(0.0f, -0.0f);
		
		leftMotorLead.setStatusFrameRateMs(CANTalon.StatusFrameRate.Feedback, 4); //250 Hz
		
				
		rightMotorLead.configPeakOutputVoltage(12.0f, -12.0f);
		rightMotorFollow.configPeakOutputVoltage(12.0f, -12.0f);

		rightMotorLead.setStatusFrameRateMs(CANTalon.StatusFrameRate.Feedback, 4); //250 Hz
		
		configurePercentControl();
		
		leftEncoder.setDistancePerPulse(Math.PI * WHEEL_DIAMETER / ENCODER_RESOLUTION);
		rightEncoder.setDistancePerPulse(Math.PI * WHEEL_DIAMETER / ENCODER_RESOLUTION);
		
		if(NavXMXP.getYaw() >= 180){
			angle -= 360;
		}
		
	}
	
	/**
	 * Set the motors for speed control mode (with PID), with the proper parameters. For use while driving with
	 * joysticks
	 * @deprecated This won't work right now. I'm going to try to rewrite it though
	 */
	private static void configureSpeedControl(){
		leftMotorLead.changeControlMode(TalonControlMode.Speed);
		rightMotorLead.changeControlMode(TalonControlMode.Speed);
		
		leftMotorLead.setProfile(0);
		leftMotorLead.setF(0.19);
		leftMotorLead.setP(0.09);
		leftMotorLead.setI(0);
		leftMotorLead.setD(0);
		
		rightMotorLead.setProfile(0);
		rightMotorLead.setF(0.19);
		rightMotorLead.setP(0.09);
		rightMotorLead.setI(0);
		rightMotorLead.setD(0);
	}
	
	/**
	 * Set the motors for PercentVbus control (no PID). For use during auto or while using the touchscreen.
	 */
	private static void configurePercentControl(){
		leftMotorLead.changeControlMode(TalonControlMode.PercentVbus);
		rightMotorLead.changeControlMode(TalonControlMode.PercentVbus);
	}
	
	/**
	 * The speed the left motors are driving at in driveStraight
	 */
	private static double leftSpeed = DRIVE_SPEED;
	
	/**
	 * The speed the right motors are driving at in driveStraight
	 */
	private static double rightSpeed = DRIVE_SPEED;
	
	/**
	 * The angle to maintain while driving straight
	 */
	private static double angle = NavXMXP.getYaw();
	
	/**
	 * Set the angle to maintain while driving straight to the current angle read by the NavX
	 */
	public static void resetTargetAngle(){
		angle = NavXMXP.getYaw();
		if(angle >= 180){
			angle -= 360;
		}
	}
	
	/**
	 * Set the angle to maintain while driving straight to a certain angle
	 * 
	 * @param angle The angle to maintain, in degrees
	 */
	public static void setTargetAngle(double angle){
		DriveBase.angle = angle;
		if(angle >= 180){
			angle -= 360;
		}
	}
	/**
	 * Drives straight, starting at a certain initial speed
	 * 
	 * @param initialSpeed The speed to start driving at. This speed will change somewhat as the motor speed is adjusted.
	 * @param reinitialize Whether to reset to a new angle
	 */
    public static void driveStraight(double initialSpeed, boolean reinitialize){
    	if(reinitialize){
    		angle = NavXMXP.getYaw();
    		if(angle >= 180){
    			angle -= 360;
    		}
    	}
    	leftSpeed = initialSpeed;
    	rightSpeed = initialSpeed;
    	
    	double currentAngle = NavXMXP.getYaw();
    	if(currentAngle >= 180){
			currentAngle -= 360;
		}
    	if (currentAngle >= angle - STRAIGHT_LINE_TOLERANCE && currentAngle <= angle + STRAIGHT_LINE_TOLERANCE){
    		leftSpeed = initialSpeed;
    		rightSpeed = initialSpeed;
    	}
    	else{
    		if (currentAngle <= angle - STRAIGHT_LINE_TOLERANCE){
        		if(leftSpeed < MAX_SPEED){
        			leftSpeed += (currentAngle - angle) * -1 * autoP;
        		}
        		else if(rightSpeed > -1 * MAX_SPEED){
        			rightSpeed -= (currentAngle - angle) * -1 * autoP;
        		}
    		} 
    		else if (currentAngle >= angle + STRAIGHT_LINE_TOLERANCE){
    			if(rightSpeed < MAX_SPEED){
    				rightSpeed += (currentAngle - angle) * autoP;
    			}
    			else if(leftSpeed > -1 * MAX_SPEED){
    				leftSpeed -= (currentAngle - angle) * autoP;
    			}
    		}
    	}
    	
    	if(leftSpeed > MAX_SPEED){
    		leftSpeed = MAX_SPEED;
    	}
    	if(rightSpeed > MAX_SPEED){
    		rightSpeed = MAX_SPEED;
    	}
    	setRawFraction(leftSpeed, rightSpeed);
    }
    
    /**
     * Get whether a motor is too hot, as determined by comparing its temperature to MOTOR_MAX_TEMP.
     * 
     * @param m The motor whose temperature should be checked
     * @return Whether the specified motor is too hot.
     */
    public static boolean isAMotorTooHot(){
    	return (leftMotorLead.getTemperature() > MOTOR_MAX_TEMP) ||
    			(rightMotorLead.getTemperature() > MOTOR_MAX_TEMP) ||
    			(leftMotorFollow.getTemperature() > MOTOR_MAX_TEMP) ||
    			(rightMotorFollow.getTemperature() > MOTOR_MAX_TEMP);
    }
    
    /**
     * Set the left and right motors to a certain fraction of full speed
     * 
     * @param leftSpeed The fraction of full speed the left motors should be set to.
     * @param rightSpeed The fraction of full speed the right motors should be set to.
     */
    public static void setRawFraction(double leftSpeed, double rightSpeed){
    	if(leftMotorLead.getControlMode() != TalonControlMode.PercentVbus){
			configurePercentControl();
		}
    	
    	leftMotorLead.set(leftSpeed);
    	rightMotorLead.set(-1 * rightSpeed);    	
    }
    
    /*
     * Reset the encoders to 0 displacement
     */
    public static void resetEncoders(){
    	leftEncoder.reset();
    	rightEncoder.reset();
    }
    
    /**
     * Gets the speed currently measured by the left encoder
     * 
     * @return The speed in inches per second currently measured by the left encoder
     */
    public static double getLeftSpeed(){
    	double speed = leftEncoder.getRate();
    	
    	return speed; //returns inches/second
    }
    
    /**
     * Gets the speed currently measured by the right encoder
     * 
     * @return The speed currently measured by the right encoder
     */
    public static double getRightSpeed(){
    	double speed = rightEncoder.getRate();
    	
    	return speed; //returns inches/second
    }
    
    /**
     * Gets the distance traveled by the left side of the robot. Negative when driving forward.
     * 
     * @return The distance traveled by the left side of the robot in inches
     */
	public static double getLeftDistance(){
    	return leftEncoder.getDistance(); //returns inches
    }
    
	/**
     * Gets the distance traveled by the right side of the robot. Positive when driving forward.
     * 
     * @return The distance traveled by the right side of the robot in inches
     */
    public static double getRightDistance(){
    	return rightEncoder.getDistance(); //returns inches
    }
    
    /**
     * The last time this method was called. It will reinitialize if the last call was more than .5 seconds ago
     */
    private static long driveLastCallMillis = System.currentTimeMillis();
    
    /**
     * Left reading at the beginning of the driving
     */
    private static double leftEncoderInitial = 0;
    
    /**
     * Right reading at the beginning of the driving
     */
    private static double rightEncoderInitial = 0;
    
    /**
     * Drive straight for a certain number of inches
     * 
     * @param inches The number of inches to drive
     * @return whether the driving is complete
     */
	public static boolean driveDistance(double inches, double speed) {
		
		if(System.currentTimeMillis() - driveLastCallMillis >= 100){
			leftEncoderInitial = getLeftDistance();
			rightEncoderInitial = getRightDistance();
			resetTargetAngle();
			Timer.delay(.05);
		}
		
		double distance = (-1 * (getLeftDistance() - leftEncoderInitial) + (getRightDistance() - rightEncoderInitial)) / 2;
		double driveSpeed;
		if(Math.abs(inches) < 12){
			driveSpeed = speed * .5;
		}
		else{
			driveSpeed = speed;
		}
		if(distance >= 0 && distance <= ACCELERATE_DISTANCE && inches >= ACCELERATE_DISTANCE + DECELERATE_DISTANCE){
			driveStraight(.1 + (driveSpeed - .1) / ACCELERATE_DISTANCE * distance, false);
		}
		
		else if(distance <= 0 && distance >= -1 * ACCELERATE_DISTANCE && inches <= -1 * (ACCELERATE_DISTANCE + DECELERATE_DISTANCE)){
			driveStraight(-.1 + (driveSpeed - .1) / ACCELERATE_DISTANCE * distance, false);
		}
		
		else if(Math.abs(distance) > ACCELERATE_DISTANCE && Math.abs(distance) < Math.abs(inches) - DECELERATE_DISTANCE){
			if(inches < 0){
				driveStraight(-driveSpeed, false);
			}
			else{
				driveStraight(driveSpeed, false);
			}
		}
		
		else{
			if(Math.abs(distance) > Math.abs(inches) - DISTANCE_TOLERANCE){
				setRawFraction(0, 0);
				return true;
			}
			else if(inches > 0){
				driveStraight((driveSpeed - 0.1) / DECELERATE_DISTANCE * (inches - distance) + 0.1, false); //replace 0 with some other number if you want to finish at a different speed
			}
			else{
				driveStraight((driveSpeed + 0.1) / DECELERATE_DISTANCE * (inches - distance) - 0.1, false);
			}
		}
		
		driveLastCallMillis = System.currentTimeMillis();
		return false;
	}
	
	/**
	 * The direction to turn. 0 = clockwise, 1 = counter clockwise
	 */
	private static int direction;
	
	/**
	 * Drive to a certain angle (0 to 360 degrees)
	 * 
	 * @param degrees The angle to turn to
	 * @return Whether the turning is finished
	 */
	public static boolean driveRotation(double degrees) {
		if(Math.abs(NavXMXP.getYaw() - degrees) <= ANGLE_TOLERANCE){
			setRawFraction(0, 0);
			return true;
		}
		if(degrees < 0){
			degrees += 360;
		}
		if(Math.abs(NavXMXP.getYaw() - degrees) > 180){
			if(degrees < NavXMXP.getYaw()){
				direction = 0;
			}
			else {
				direction = 1;
			}
		}
		else{
			if(degrees > NavXMXP.getYaw()){
				direction = 0;
			}
			else{
				direction = 1;
			}
		}
		if(direction == 1){
			if(Math.abs(NavXMXP.getYaw() - degrees) <= ANGLE_TOLERANCE * 10){ //yes, 10 is a very magic number
				double speed = (TURN_SPEED - 0.1) / (ANGLE_TOLERANCE * 10) * Math.abs(NavXMXP.getYaw() - degrees) + 0.1; //replace speed with a different number if it's too low to reach the target
				setRawFraction(-1 * speed, speed);
			}
			else{
    			setRawFraction(-1 * TURN_SPEED, TURN_SPEED); 
			}
		}
		else if(direction == 0){
			if(Math.abs(NavXMXP.getYaw() - degrees) <= ANGLE_TOLERANCE * 10){
				double speed = (TURN_SPEED - 0.1) / (ANGLE_TOLERANCE * 10) * Math.abs(NavXMXP.getYaw() - degrees) + 0.1;
				setRawFraction(speed, -1 * speed);
			}
			else{
    			setRawFraction(TURN_SPEED, -1 * TURN_SPEED);
			}
		}
		return false; 
	}
	
	/**
	 * The distance driven so far in the driveDistanceWithVision
	 */
	private static volatile double distanceDriven = 0;
		
	/**
	 * Whether distanceFromCamera and angleFromCamera are new values (alternative is that they have
	 * already been processed)
	 */
	private static volatile boolean newAngle = false;
	
	/**
	 * The distance to the goal read by the camera
	 */
	private static volatile double distanceFromCamera = 0;
	
	/**
	 * The angle to the goal read by the camera
	 */
	private static volatile double angleFromCamera = 0;
	
	/**
	 * The distance that had been driven when the last image was taken
	 */
	private static volatile double lastDistance = 0;
	
	/**
	 * The distance remaining in the driveDistanceWithVision
	 */
	private static volatile double distanceToGo;
	
	/**
	 * Whether it is the first call to driveDistanceWithVision
	 */
	private static boolean firstCall = true;
	
	/**
	 * Drive a certain distance using a vision closed loop. Will start targeting when you're 72 inches away, and stop
	 * when you're x inches away.
	 *
	 * @param distance The distance to drive to the goal
	*/
	public static boolean driveDistanceWithVision(double distance, double speed){
		if(firstCall){
			NavXMXP.resetYaw();
			Timer.delay(.05);
			distanceDriven = 0;
			resetTargetAngle();
			resetEncoders();
			System.out.println("here");
			firstCall = false;
		}
		distanceDriven = (Math.abs(-1 * getLeftDistance()) + Math.abs(getRightDistance())) / 2;
		if(distanceDriven < distance - 72){
			System.out.println("firstDrive");
			driveStraight(DRIVE_SPEED, false);
			System.out.println(distanceDriven);
		}
		else if(distanceDriven >= distance - 72 && distanceDriven < distance - 30){
			Robot.startProcessingImage();
			if(newAngle){
				if(Math.abs(distance - distanceDriven - distanceFromCamera) < 5){
					double displacement = Math.abs(distance - distanceDriven - lastDistance);
					distanceDriven = lastDistance + displacement;
					setTargetAngle(angleFromCamera);
				}
				newAngle = false;
			}
			driveStraight(speed, false);
			distanceDriven = (Math.abs(getLeftDistance()) + Math.abs(getRightDistance())) / 2;
		}
		else{
			Robot.stopProcessingImage();
			if(driveDistance(30, speed)){
				return true;
			}	
		}
		distanceToGo = distance - distanceDriven;
		return false;
	}
	
	/**
	 * Get the distance remaining in the driveDistanceWithVision
	 * 
	 * @return The distance remaining
	 */
	public static double getDistanceToGo(){
		return distanceToGo;
	}
	
	/**
	 * Save new measurements read from the camera
	 * 
	 * @param distanceToGoal The distance to the goal
	 * @param angle The angle to the goal
	 * @param lastDistance The distance to the goal when the picture was taken, from the encoders
	 */
	public static void putNewMeasurements(double distanceToGoal, double angle, double lastDistance){
		DriveBase.distanceFromCamera = distanceToGoal;
		DriveBase.angleFromCamera = angle;
		DriveBase.lastDistance = lastDistance;
		newAngle = true;
	}
	
	/**
	 * The last call to a one-side drive method
	 */
	private static double oneSideDistanceLastCallMillis = System.currentTimeMillis();
	
	/**
	 * The distance driven in the last one-side drive
	 */
	private static double oneSideDistanceDriven = 0;
	
	/**
	 * Drive a distance with only the right side
	 * 
	 * @param distance The distance to drive
	 * @return Whether the drive is finished
	 */
	public static boolean driveLeftDistance(double distance){
		if(System.currentTimeMillis() - oneSideDistanceLastCallMillis > 100){
			oneSideDistanceDriven = 0;
			resetEncoders();
		}
		oneSideDistanceDriven += (Math.abs(getLeftDistance()) + Math.abs(getRightDistance())) / 2;
		
		if(oneSideDistanceDriven >= distance){
			return true;
		}	
		
		setRawFraction(DRIVE_SPEED, 0);
		oneSideDistanceLastCallMillis = System.currentTimeMillis();
		return false;
	}
	
	/**
	 * Drive a distance with only the right side
	 * 
	 * @param distance The distance to drive
	 * @return Whether the drive is done
	 */
	public static boolean driveRightDistance(double distance){
		if(System.currentTimeMillis() - oneSideDistanceLastCallMillis > 100){
			oneSideDistanceDriven = 0;
			resetEncoders();
		}
		
		oneSideDistanceDriven += (Math.abs(getLeftDistance()) + Math.abs(getRightDistance())) / 2;

		if(oneSideDistanceDriven >= distance){
			return true;
		}
		
		setRawFraction(0, DRIVE_SPEED);
		oneSideDistanceLastCallMillis = System.currentTimeMillis();
		return false;
	}
	
	private static boolean controlsReversed = false;

	
	/**
	 * Called when the human is in control of the robot, using the joysticks
	 * 
	 * @param leftSpeed The fraction of full speed to set the left motors to
	 * @param rightSpeed The fraction of full speed to set the right motors to
	 */
	public static void teleopDrive(double leftSpeed, double rightSpeed){
		if(leftMotorLead.getControlMode() != TalonControlMode.PercentVbus){
			configurePercentControl();
		}
		if(!controlsReversed){
			setRawFraction(-1 * Math.pow(leftSpeed, 3), -1 * Math.pow(rightSpeed, 3));
		}
		else{
			setRawFraction(Math.pow(rightSpeed, 3), Math.pow(leftSpeed, 3));

		}
	}
	
	/**
	 * "Reverse" the front side of the robot. I.e. if forward is toward the dump side, this will
	 * change it so forward is toward the gear side, or vice versa.
	 */
	public static void reverseControls(){
		if(controlsReversed){
			controlsReversed = false;
		}
		else{
			controlsReversed = true;
		}
		Dashboard.sendControlsReversed(controlsReversed);
	}
}
