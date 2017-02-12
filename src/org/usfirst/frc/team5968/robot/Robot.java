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
    	
    	while (true) {
    		// Call the appropriate function depending upon the current robot mode
    		if (isDisabled()) {
    			// call DisabledInit() if we are now just entering disabled mode from
    			// either a different mode or from power-on
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
    private static final double ROBOT_LENGTH = 38;
    
    /**
     * The width of the robot in inches, measured between the centers of the wheels
     */
    private static final double ROBOT_WIDTH = 25.1875;
    
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
    
    /**
     * The climber will run in this thread to allow it to stop within 5 ms of hitting the target, instead of 20 ms.
     */
    private static Thread climberThread = null;
    
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
    	//Pneumatics.setSolenoidDown();
    	
		alliance = DriverStation.getInstance().getAlliance();
    	
    	startPoint = Dashboard.getStartingPoint();
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
    	if(alliance != Alliance.Red && alliance != Alliance.Blue){
    		DriverStation.reportError("I don't know what alliance I'm on!", false);
    		autoFinished = true;
    		return;
    	}
    	if(startPoint != StartPoint.KEY && startPoint != StartPoint.MIDLINE && startPoint != StartPoint.RETRIEVAL_ZONE){
    		DriverStation.reportError("I don't know where I am!!! D: D:", false);
    		return;
    	}
    	
    	auto = Dashboard.getAutoMode();
    	
    	hopper = Dashboard.getHopper();
    	
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
    
    
    /**
     * Called periodically during autonomous
     */
	public void autoPeriodic(){
    	//hehe this is all in a separate thread. It won't have to wait for Driver Station updates!! :D
    }
    
    /**
     * Called at the beginning of teleop
     */
    public void teleopInit(){
    	if(autoThread != null && autoThread.isAlive()){
    		autoThread.interrupt(); //stops the auto code if it's for some reason still running
    	}
    	

    	Runnable climb = new RopeClimber();
    	climberThread = new Thread(climb);
    	climberThread.start();
    	
    	NavXMXP.resetYaw();
    	DriveBase.resetTargetAngle();
    	
    }
    
    boolean driven = false;
    /**
     * Called periodically during teleop
     */
    public void teleopPeriodic(){
    	//if(!drivePoints.isEmpty() && HumanInterface.getLeftStick() != 0 && HumanInterface.getRightStick() != 0){
    		//DriveBase.drivePath(drivePoints, false);
    	//}
    	//else{
    	//DriveBase.teleopDrive(HumanInterface.getLeftStick(), HumanInterface.getRightStick());
    	//}
    	
    	//HumanInterface.liftControl();
    	//HumanInterface.emergencyStopClimberControl();
		/*lights.pneumatics();
		lights.climbing();*/
    }
    
    /**
     * Called periodically during the entire match. This should be used for any code that needs to run
     * at all times, to avoid pasting it twice.
     */
    public void robotPeriodic(){
    	Dashboard.updateDashboardValues();
    	
    	/*if(!isDisabled()){
        	PositionTracker.updateCoordinates();
    		Dashboard.updateDrivePoints(drivePoints);
    	}*/
    }
    
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
    
    private LinkedBlockingQueue<Point> processAutoPath(AutoMode mode, int hopper) throws InterruptedException{
    	LinkedBlockingQueue<Point> path = new LinkedBlockingQueue<Point>();
    	
    	if(alliance == Alliance.Red && hopper == 3){
    		DriverStation.reportError("We aren't allowed to go to that hopper", false);
    		hopper = -1;
    		return path;
    	}
    	else if(alliance == Alliance.Blue && hopper == 5){
    		DriverStation.reportError("We aren't allowed to go to that hopper", false);
    		hopper = -1;
    		return path;
    	}
    	
    	if(mode == AutoMode.GEAR){
    		if(startPoint == StartPoint.KEY){
    			if(alliance == Alliance.Red){
    				path.put(Point.getCoordinates(Setpoint.RED_GEAR3));
    			}
    			else if(alliance == Alliance.Blue){
    				path.put(Point.getCoordinates(Setpoint.BLUE_GEAR3));
    			}
    		}
    		else if(startPoint == StartPoint.MIDLINE){
    			if(alliance == Alliance.Red){
    				path.put(Point.getCoordinates(Setpoint.RED_GEAR2));
    			}
    			else if(alliance == Alliance.Blue){
    				path.put(Point.getCoordinates(Setpoint.BLUE_GEAR2));
    			}
    		}
    		else if(startPoint == StartPoint.RETRIEVAL_ZONE){
    			if(alliance == Alliance.Red){
    				path.put(Point.getCoordinates(Setpoint.RED_GEAR1));
    			}
    			else if(alliance == Alliance.Blue){
    				path.put(Point.getCoordinates(Setpoint.BLUE_GEAR1));
    			}
    		}
    	}
    	
    	if(mode == AutoMode.HOPPER_BOILER){
    		if(alliance == Alliance.Red){
    			path.put(new Point(295 + .5 * ROBOT_WIDTH, 115));
    			path.put(new Point(324 - .5 * ROBOT_WIDTH, 115));
    			path.put(new Point(29, 28.9));
    		}
    		else if(alliance == Alliance.Blue){
    			path.put(new Point(295 + .5 * ROBOT_WIDTH, 537));
    			path.put(new Point(324 - .5 * ROBOT_WIDTH, 537));
    			path.put(new Point(29, 622.2));
    		}
    	}
    	
    	if(mode == AutoMode.HOPPER){
    		if(startPoint == StartPoint.KEY && (hopper == 3 || hopper == 4 || hopper == 5)){
    			switch(hopper){
    				case 3:
    					path.put(new Point(295 + .5 * ROBOT_WIDTH, 537));
    					break;
    				case 4:
    					path.put(new Point(295 + .5 * ROBOT_WIDTH, 326));
    					break;
    				case 5:
    					path.put(new Point(295 + .5 * ROBOT_WIDTH, 115));
    					break;
    			}
    		}
    		else if(startPoint == StartPoint.RETRIEVAL_ZONE && (hopper == 1 || hopper == 2)){
    			switch(hopper){
    				case 1:
    					path.put(new Point(33.9 + .5 * ROBOT_WIDTH, 202));
    					break;
    				case 2:
    					path.put(new Point(33.9 + .5 * ROBOT_WIDTH, 450));
    					break;
    			}
    		}
    	}
    	
    	return path;
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
     * Get the climber thread
     * 
     * @return The thread the climber code is running in.
     */
    public static Thread getClimberThread(){
    	return climberThread;
    }
}
