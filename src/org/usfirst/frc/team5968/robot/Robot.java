package org.usfirst.frc.team5968.robot;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.DoubleAccumulator;

import javax.swing.text.Position;

import org.usfirst.frc.team5968.robot.Point.Setpoint;
import org.usfirst.frc.team5968.robot.AutoManager.AutoMode;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tInstances;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 * 
 * @author BejingStrongbow
 * @author dash102
 * @author Elden123
 */
public class Robot extends RobotBase {
	
	/**
	 * Method called automatically by the VM when a round starts. We decided to implement this directly from RobotBase
	 * instead of using the IterativeRobot interface in order to maximize the update rate. As far as I'm aware, this
	 * method runs considerably faster than FIRST's default.
	 */
    public void startCompetition(){
    	HAL.report(tResourceType.kResourceType_Framework, tInstances.kFramework_Iterative);
    	
    	HAL.observeUserProgramStarting();
    	LiveWindow.setEnabled(false);
    	robotInit();
    	
    	boolean autoInitialized = false;
    	boolean teleopInitialized = false;
    	boolean disabledInitialized = false;
    	
    	while (true) {
    		// Call the appropriate function depending upon the current robot mode
    		if (isDisabled()) {
    			if(!disabledInitialized){
    				disabledInit();
    				disabledInitialized = true;
    			}
    			disabledPeriodic();
    			HAL.observeUserProgramDisabled();
    		}
    		else if (isTest()) {
    			// call TestInit() if we are now just entering test mode from either
    			// a different mode or from power-on
    			HAL.observeUserProgramTest();
    		} 
    		else if (isAutonomous()) {
    			// call Autonomous_Init() if this is the first time
    			// we've entered autonomous_mode
    			if(!autoInitialized){
    				autoInit();
    				autoInitialized = true;
    			}
    			HAL.observeUserProgramAutonomous();
    			autoPeriodic();
    		}
    		else {
    			
    			if(!teleopInitialized){
    				teleopInit();
    				teleopInitialized = true;
    			}
    			HAL.observeUserProgramTeleop();
    			teleopPeriodic();
    		}
    		robotPeriodic();
    		m_ds.waitForData(); 
    	}
    }
    
    /**
     * Stores the points to drive to in first-in-first-out order. Stores nothing if the user
     * is using the joysticks
     */
    private LinkedBlockingQueue<Point> drivePoints = new LinkedBlockingQueue<Point>();
    
    /**
     * Where the robot started
     */
    private StartPoint startPoint;
    
    /**
     * The length of the robot in inches
     */
    private static final double ROBOT_LENGTH = 38.5;
    
    /**
     * The width of the robot in inches, measured between the centers of the wheels
     */
    private static final double ROBOT_WIDTH = 36;
    
    /**
     * The alliance we're on
     */
    private static Alliance alliance;
    
    /**
     * The swag (LED strip lights)
     */
	private static Lights lights = new Lights();
	
	/**
	 * The automode selected on the driver station
	 */
	private static AutoMode auto;
	
	/**
	 * The hopper to drive to if it's needed for the selected automode
	 */
	private static int hopper;
	
	/**
	 * Whether the selected autonomous routine is finished.
	 */
	private static boolean autoFinished = false;
    
    /**
     * The starting positions
     *
     */
    public enum StartPoint {
    	MIDLINE,
    	KEY,
    	RETRIEVAL_ZONE;
    }
    
    /**
     * The autonomous code runs in this thread. Should allow the code to be updated faster,
     * because it won't have to wait for updates from the Driver Station, and we should get better
     * precision.
     */
    private static Thread autoThread = null;
    
    private static Thread teleopThread = null;
    
    private Processing processing = new Processing();
    
    private static boolean shouldProcessImage = false;
    /**
     * Called when the robot turns on
     */
    public void robotInit(){
    	NavXMXP.init();
    	DriveBase.init();
    	DriveBase.resetEncoders();
    	Pneumatics.init();
    	RopeClimber.init();
    	Dashboard.init();
    	Pneumatics.setSolenoidDown();
    	UsbCamera.init();
    	
    	/*if(startPointName == StartPoint.KEY){
    		if(alliance == Alliance.Red){
    			PositionTracker.init(223.1 + .5 * ROBOT_WIDTH, .5 * ROBOT_LENGTH);
    		}
    		else if(alliance == Alliance.Blue){
    			PositionTracker.init(223.1 + .5 * ROBOT_WIDTH, 652 - .5 * ROBOT_LENGTH);
    		}
    	}
    	else if(startPointName == StartPoint.MIDLINE){
    		if(alliance == Alliance.Red){
    			PositionTracker.init(162, .5 * ROBOT_LENGTH);
    		}
    		else if(alliance == Alliance.Blue){
    			PositionTracker.init(162, 652 - .5 * ROBOT_LENGTH);
    		}
    	}
    	else if(startPointName == StartPoint.RETRIEVAL_ZONE){
    		if(alliance == Alliance.Red){
    			PositionTracker.init(80.9 + .5 * ROBOT_WIDTH, .5 * ROBOT_LENGTH);
    		}
    		else if(alliance == Alliance.Blue){
    			PositionTracker.init(80.9 + .5 * ROBOT_WIDTH, 652 - .5 * ROBOT_LENGTH);
    		}
    	}*/
    	
    	
    }
    
    /**
     * Called at the beginning of autonomous.
     */
    public void autoInit(){
    	NavXMXP.resetYaw();
    	DriveBase.resetTargetAngle();
    	Timer.delay(.05);
    	Lights.toggleLedRing();
    	
    	alliance = DriverStation.getInstance().getAlliance();
    	
    	startPoint = Dashboard.getStartingPoint();
    	
    	auto = Dashboard.getAutoMode();
    	
    	hopper = Dashboard.getHopper();
    	
    	if(alliance != Alliance.Red && alliance != Alliance.Blue){
    		DriverStation.reportError("I don't know what alliance I'm on!", false);
    		autoFinished = true;
    		return;
    	}
    	if(startPoint != StartPoint.KEY && startPoint != StartPoint.MIDLINE && startPoint != StartPoint.RETRIEVAL_ZONE){
    		DriverStation.reportError("I don't know where I am!!! D: D:", false);
    		return;
    	}
    	
    	if(auto == AutoMode.HOPPER_BOILER){
    		DriverStation.reportWarning("Selected auto is hopper and boiler", false);
    	}
    	else if(auto == AutoMode.HOPPER){
    		DriverStation.reportWarning("Selected auto is hopper on hopper number " + hopper, false);
    	}
    	else if(auto == AutoMode.GEAR){
    		DriverStation.reportWarning("Selected auto is gear", false);
    	}
    	else if(auto == AutoMode.CROSS){
    		DriverStation.reportWarning("Selected auto is cross", false);
    	}
    	else{
    		System.out.println("Derp. Tell that to Rishi and he'll say it's \"cringy\"");
    	}
    	
    	Runnable task = new AutoThread(startPoint, auto, alliance, hopper);
    	autoThread = new Thread(task);
    	autoThread.start();
    }
    
    boolean driven = false;
    /**
     * Called periodically during autonomous
     */
	public void autoPeriodic(){
    	//hehe this is all in a separate thread. It won't have to wait for Driver Station updates!! :D
		lights.pneumatics();
		if(shouldProcessImage){
			double initialDistance = DriveBase.getDistanceToGo();

			processing.process(UsbCamera.getImage(), true);
			if(Processing.getGroundDistance() > 0.0) {
				DriveBase.putNewMeasurements(Processing.getGroundDistance(), Processing.getAngle(), initialDistance);
				System.out.println("********* " + Processing.getGroundDistance() + " " + Processing.getAngle());
			}
		}
    }
    
    /**
     * Called at the beginning of teleop
     */
    public void teleopInit(){
    	if(autoThread != null && autoThread.isAlive()){
    		autoThread.interrupt(); //stops the auto code if it's for some reason still running
    	}
    	
    	NavXMXP.resetYaw();
    	DriveBase.resetTargetAngle();
    	Timer.delay(.05);
    	Lights.toggleLedRing();
    	
    	teleopThread = new Thread(new TeleopThread());
    	teleopThread.start();
    }
    
    boolean climbed = false;
    /**
     * Called periodically during teleop
     */
    public void teleopPeriodic(){
    	
    	HumanInterface.buttonControls();
    	
    	if(!climbed){
    		climbed = RopeClimber.motorClimb();
    	}
    	if(!RopeClimber.motorClimb() && !HumanInterface.isLightsFlashing()) {
    		lights.pneumatics();
    	}
    }
    
    /**
     * Called periodically during the entire match. This should be used for any code that needs to run
     * at all times, to avoid pasting it twice.
     */
    public void robotPeriodic(){
    	Dashboard.updateDashboardValues();
    }
    
    /**
     * Called the first time the robot is disabled
     */
    public void disabledInit(){
    	
    }
    
    /**
     * Called periodically while disabled
     */
    public void disabledPeriodic(){
    	if(autoThread != null && autoThread.isAlive()){
    		autoThread.interrupt();
    	}
    	if(teleopThread != null && teleopThread.isAlive()){
    		teleopThread.interrupt();
    	}
    }
    
    /**
     * Runs the autonomous code. This means the code doesn't have to wait for driver station updates.
     */
    private class AutoThread implements Runnable{
    	private StartPoint startPoint;
    	
    	private AutoMode mode;
    	
    	private Alliance alliance;
    	
    	private int hopper;
    	
    	public AutoThread(StartPoint startPoint, AutoMode mode, Alliance alliance, int hopper){
    		this.startPoint = startPoint;
    		this.mode = mode;
    		this.alliance = alliance;
    		this.hopper = hopper;
    	}
    	
    	public void run(){
    		AutoManager.doAuto(startPoint, mode, alliance, hopper);
    	}
    }
    
    /**
     * Runs the PID code in teleop. This probably needs testing if we want to use it.
     *
     */
    private class TeleopThread implements Runnable{
    	
    	public void run(){
    		while(!Thread.interrupted()){
    			double leftFraction = HumanInterface.getLeftStick();
        		double leftSpeed = DriveBase.getLeftSpeed();
        		double leftTargetSpeed = leftFraction * DriveBase.MAX_SPEED_INCHES;
        		leftSpeed += 0.09 * (leftTargetSpeed - leftSpeed);
        		
        		double rightFraction = HumanInterface.getRightStick();
        		double rightSpeed = DriveBase.getRightSpeed();
        		double rightTargetSpeed = rightFraction * DriveBase.MAX_SPEED_INCHES;
        		rightSpeed += 0.09 * (rightTargetSpeed - rightSpeed);
        		
        		DriveBase.teleopDrive(leftSpeed, rightSpeed);
    		}
    	}
    }
    
    /**
     * Wait for a specified duration in milliseconds. This should not be used for any extended period
     * of time, because the watchdog will get very angry at this code if it is.
     * 
     * @param millis The length in milliseconds to wait.
     */
    public static void waitMillis(long millis){
    	long start = System.currentTimeMillis();
    	
    	while(System.currentTimeMillis() - (millis + start) < 0){}
    }
    
    /**
     * The width of the robot between the centers of the wheels, in inches
     * 
     * @return The width of the robot in inches
     */
    public static double getRobotWidth(){
    	return ROBOT_WIDTH;
    }
    
    /**
     * The length of the robot in inches
     * 
     * @return The length of the robot
     */
    public static double getRobotLength(){
    	return ROBOT_LENGTH;
    }
    
    /**
     * Get the distance to the nearest wall from a startpoint
     * 
     * @param p The startpoint to find the x coordinate for
     * @return The distance to the nearest wall
     */
    public static double getDistanceFromWall(StartPoint p){
    	switch(p){
    		case KEY:
    			return 82.5;
    		case MIDLINE:
    			return 162;
    		case RETRIEVAL_ZONE:
    			return 80.9;
    		default:
    			return -1;
    	}
    }
    
    public static void startProcessingImage(){
    	shouldProcessImage = true;
    }
    
    public static void stopProcessingImage(){
    	shouldProcessImage = false;
    }
    
    public static Lights getLights(){
    	return lights;
    }
}
