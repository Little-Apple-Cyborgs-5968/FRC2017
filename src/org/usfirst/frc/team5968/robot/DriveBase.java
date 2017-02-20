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
	 * The resolution of the encoder readings in counts/revolution
	 */
	private static final int ENCODER_RESOLUTION = 2048;
	
	/**
	 * Wheel diameter in inches
	 */
	private static final double WHEEL_DIAMETER = 6.6;
	
	/**
	 * The maximum speed the robot can drive. Used to scale the joystick inputs to an absolute speed.
	 */
	private static final double MAX_SPEED_RPM = 425;
    
    /**
     * The distance to accelerate over when driving a distance, in inches.
     */
    private static final double ACCELERATE_DISTANCE = 6;
    
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
		
		leftMotorLead.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		
		leftMotorLead.configEncoderCodesPerRev(ENCODER_RESOLUTION);
		
		leftMotorLead.configNominalOutputVoltage(0.0f, -0.0f);
		leftMotorFollow.configNominalOutputVoltage(0.0f, -0.0f);
		
		leftMotorLead.setStatusFrameRateMs(CANTalon.StatusFrameRate.Feedback, 4); //250 Hz
		
		
		rightMotorLead.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		
		rightMotorLead.configEncoderCodesPerRev(ENCODER_RESOLUTION);
		
		rightMotorLead.configPeakOutputVoltage(12.0f, -12.0f);
		rightMotorFollow.configPeakOutputVoltage(12.0f, -12.0f);

		rightMotorLead.setStatusFrameRateMs(CANTalon.StatusFrameRate.Feedback, 4); //250 Hz
		
		configureSpeedControl();
		
		if(NavXMXP.getYaw() >= 180){
			angle -= 360;
		}
		
	}
	
	/**
	 * Set the motors for speed control mode (with PID), with the proper parameters. For use while driving with
	 * joysticks
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
	
	public static void resetTargetAngle(){
		angle = NavXMXP.getYaw();
		if(angle >= 180){
			angle -= 360;
		}
	}
	
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
    
    /**
     * Set the left and right motors to a certain absolute speed
     * 
     * @param leftSpeed The absolute speed (in RPM) the left motors should be set to.
     * @param rightSpeed The absolute speed (in RPM) the right motors should be set to.
     */
    @SuppressWarnings("unused")
	public static void setRawSpeed(double leftSpeed, double rightSpeed){
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
     * Gets the distance traveled by the left side of the robot. Negative when driving forward.
     * 
     * @return The distance traveled by the left side of the robot
     */
	public static double getLeftDistance(){
    	return leftMotorLead.getPosition() * Math.PI * WHEEL_DIAMETER; //returns inches
    }
    
	/**
     * Gets the distance traveled by the right side of the robot. Positive when driving forward.
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
		if(distance >= 0 && distance <= ACCELERATE_DISTANCE && inches >= ACCELERATE_DISTANCE * 2){
			driveStraight(.1 + (driveSpeed - .1) / ACCELERATE_DISTANCE * distance, false);
		}
		
		else if(distance <= 0 && distance >= -1 * ACCELERATE_DISTANCE && inches <= -1 * ACCELERATE_DISTANCE * 2){
			driveStraight(-.1 + (driveSpeed - .1) / ACCELERATE_DISTANCE * distance, false);
		}
		
		else if(Math.abs(distance) < Math.abs(inches) - ACCELERATE_DISTANCE){
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
				driveStraight((driveSpeed - 0) / DECELERATE_DISTANCE * (inches - distance) + 0, false); //replace 0 with some other number if you want to finish at a different speed
			}
			else{
				driveStraight((driveSpeed + 0) / DECELERATE_DISTANCE * (inches - distance) - 0, false);
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
		
    	if(Math.abs(NavXMXP.getYaw() - degrees) >= ANGLE_TOLERANCE){
    		if(direction == 1){
    			if(Math.abs(NavXMXP.getYaw() - degrees) <= ANGLE_TOLERANCE * 10){ //yes, 10 is a very magic number
    				double speed = (TURN_SPEED - 0) / (ANGLE_TOLERANCE * 10) * Math.abs(NavXMXP.getYaw() - degrees) + 0; //replace speed with a different number if it's too low to reach the target
    				setRawFraction(-1 * speed, speed);
    			}
    			else{
        			setRawFraction(Math.max(-TURN_SPEED, -1), Math.min(.1, 1)); //only set to 2 * DRIVE_SPEED if that's within -1 to 1
    			}
    		}
    		else if(direction == 0){
    			if(Math.abs(NavXMXP.getYaw() - degrees) <= ANGLE_TOLERANCE * 10){
    				double speed = (TURN_SPEED - 0) / (ANGLE_TOLERANCE * 10) * Math.abs(NavXMXP.getYaw() - degrees) + 0;
    				setRawFraction(speed, -1 * speed);
    			}
    			else{
        			setRawFraction(Math.min(TURN_SPEED, 1), Math.max(-TURN_SPEED, -1));
    			}
    		}
    		return false;
    	}    
    	else{
    		DriveBase.setRawFraction(0, 0);
    		return true;
    	}
	}
	
	private static volatile double distanceDriven = 0;
		
	private static volatile boolean newAngle = false;
	
	private static volatile double distanceFromCamera = 0;
	
	private static volatile double angleFromCamera = 0;
	
	private static volatile double lastDistance = 0;
	
	private static volatile double distanceToGo;
	
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
	
	public static double getDistanceToGo(){
		return distanceToGo;
	}
	
	public static void putNewMeasurements(double distanceToGoal, double angle, double lastDistance){
		DriveBase.distanceFromCamera = distanceToGoal;
		DriveBase.angleFromCamera = angle;
		DriveBase.lastDistance = lastDistance;
		newAngle = true;
	}
	
	private static double oneSideDistanceLastCallMillis = System.currentTimeMillis();
	
	private static double oneSideDistanceDriven = 0;
	
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
		if(leftMotorLead.getControlMode() != TalonControlMode.Speed){
			configureSpeedControl();
		}
		if(!controlsReversed){
			setRawSpeed(-1 * (leftSpeed * .3) * MAX_SPEED_RPM, -1 * (rightSpeed * .3) * MAX_SPEED_RPM);
		}
		else{
			setRawSpeed((rightSpeed * .3) * MAX_SPEED_RPM, (leftSpeed * .3) * MAX_SPEED_RPM);

		}
	}
	
	public static void reverseControls(){
		if(controlsReversed){
			controlsReversed = false;
		}
		else{
			controlsReversed = true;
		}
		Dashboard.sendControlsReversed(controlsReversed);
	}
	
	private static boolean angleDriven = false;
	
	private static boolean distanceDriven1 = false;
	
	private static long pointDriveLastCallMillis = System.currentTimeMillis();
	
	private static double targetAngle;
	
	private static double targetDistance;
	
	/**
	 * Drive to a point on the field. At the moment, this is somewhat imprecise (not to mention untested),
	 * and uses what might be called P control.
	 * 
	 * @param p The point to drive to
	 * @param stop Whether top stop before driving to the point
	 */
	public static boolean driveToPoint(Point p, boolean stop){
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
		
		if(Math.sqrt(Math.pow(currentX - targetX, 2) + Math.pow(currentY - targetY, 2)) < DISTANCE_TOLERANCE){
			return true;
		}
		
		if(leftMotorLead.getSpeed() >= DISTANCE_TOLERANCE && !stop){
			System.out.println("uhhh lol");
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
			if(System.currentTimeMillis() - pointDriveLastCallMillis >= 50){
				targetAngle = Math.atan((targetX - currentX) / (targetY - currentY)) * 180 / Math.PI;
				targetDistance = Math.sqrt(Math.pow(targetX - currentX, 2) + Math.pow(targetY - currentY, 2));
			}
			
			if(!angleDriven){
				while(!driveRotation(targetAngle)){
				}
				angleDriven = true;
			}
			else if(!distanceDriven1){
				while(!driveDistance(targetDistance, 0.2)){
				}
				distanceDriven1 = true;
				pointDriveLastCallMillis = System.currentTimeMillis();
				return true;
			}
		}
		
		pointDriveLastCallMillis = System.currentTimeMillis();
		return false;
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
				driveDistance(52 - .5 * Robot.getRobotLength(), 0.2);
			}
			else{
				driveRotation(Point.getCorrectAngle(destination));
				driveDistance(Point.getStopDistance() - .5 * Robot.getRobotLength(), 0.2);
			}
		}
		else{			
			Point current = PositionTracker.getCurrentPoint();
			Point target = points.peek();
			
			driveToPoint(target, stopBetweenPoints);
			
			if(Math.sqrt(Math.pow(current.getX() - target.getX(), 2) + Math.pow(current.getY() - target.getY(), 2)) <= DISTANCE_TOLERANCE){
				points.poll();
			}
		}
	}
}
