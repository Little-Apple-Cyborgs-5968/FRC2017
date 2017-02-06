package org.usfirst.frc.team5968.robot;

import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.text.Position;

import org.usfirst.frc.team5968.robot.Point.Setpoint;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.RobotBase;
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
    private StartPoint startPointName;
    
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
	private Lights lights = new Lights();
    
    /**
     * All the autonomous options
     */
    public enum AutoMode {
    	HOPPER_BOILER,
    	HOPPER,
    	GEAR,
    	GEAR_HOPPER,
    	BOILER,
    	BOILER_CROSS,
    	CROSS,
    	BE_USELESS; //<- do nothing, if you didn't understand
    }
    
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
     * Called when the robot turns on
     */
    public void robotInit(){
    	//Dashboard.init();
    	NavXMXP.init();
    	DriveBase.init();
    	DriveBase.resetEncoders();
    	PositionTracker.init(0, 0);
    	Pneumatics.init();
    	System.out.println(PositionTracker.getCurrentPoint().getX() + " " + PositionTracker.getCurrentPoint().getY());
		/*alliance = DriverStation.getInstance().getAlliance();
    	
    	startPointName = Dashboard.getStartingPoint();
    	if(startPointName == StartPoint.KEY){
    		if(alliance == Alliance.Red){
    			PositionTracker.init(295 + .5 * ROBOT_WIDTH, .5 * ROBOT_LENGTH);
    		}
    		else if(alliance == Alliance.Blue){
    			PositionTracker.init(295 + .5 * ROBOT_WIDTH, 652 - .5 * ROBOT_LENGTH);
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
    			PositionTracker.init(33.9 + .5 * ROBOT_WIDTH, .5 * ROBOT_LENGTH);
    		}
    		else if(alliance == Alliance.Blue){
    			PositionTracker.init(33.9 + .5 * ROBOT_WIDTH, 652 - .5 * ROBOT_LENGTH);
    		}
    	}
    	else{
    		DriverStation.reportError("I don't know where I am!! Position Tracking won't work!!", false);
    	}*/
    	
    }
    
    /**
     * Called at the beginning of autonomous.
     */
    public void autoInit(){
    	NavXMXP.resetYaw();
    	System.out.println("INITIAL: " + PositionTracker.getCurrentPoint().getX() + " " + PositionTracker.getCurrentPoint().getY());
    	/*if(alliance != Alliance.Red && alliance != Alliance.Blue){
    		DriverStation.reportError("I don't know what alliance I'm on!", false);
    		return;
    	}
    	
    	AutoMode mode = Dashboard.getAutoMode();
    	
    	int hopper = Dashboard.getHopper();
    	
    	try{
    		drivePoints = processAutoPath(mode, hopper);
    	}
    	catch(InterruptedException ex){
    		DriverStation.reportError("Chances are I have no idea why this happened", false);
    	}*/
    	
    }
    
    private boolean driven = false;
    
    /**
     * Called periodically during autonomous
     */
    public void autoPeriodic(){
    	/*if(!driven){
    		if(DriveBase.driveToPoint(new Point(-48, 48), true)){
    			System.out.println("done");
    			driven = true;
    		}
    	}*/
    	if(!driven){
    		if(DriveBase.driveToPoint(new Point(-48, 48), true)){
    			driven = true;
    		}
    	}
    }
    
    /**
     * Called at the beginning of teleop
     */
    public void teleopInit(){
    	NavXMXP.resetYaw();
    	DriveBase.resetTargetAngle();
    }
    boolean forward = false;
    /**
     * Called periodically during teleop
     */
    public void teleopPeriodic(){
    	if(!drivePoints.isEmpty() && HumanInterface.getLeftStick() != 0 && HumanInterface.getRightStick() != 0){
    		DriveBase.drivePath(drivePoints, false);
    	}
    	else{
    		DriveBase.teleopDrive(HumanInterface.getLeftStick(), HumanInterface.getRightStick());
    	}
    	
    	HumanInterface.liftControl();
    	
    	if(HumanInterface.getButton()){
    		DriveBase.resetEncoders();
    		NavXMXP.resetYaw();
    		PositionTracker.resetCoordinates();
    	}
		/*lights.pneumatics();
		lights.climbing();*/
    	
    }
    
    /**
     * Called periodically during the entire match. This should be used for any code that needs to run
     * at all times, to avoid pasting it twice.
     */
    public void robotPeriodic(){
    	Dashboard.updateDashboardValues();
    	
    	if(!isDisabled()){
        	PositionTracker.updateCoordinates();
    		Dashboard.updateDrivePoints(drivePoints);
    	}
    }
    
    private LinkedBlockingQueue<Point> processAutoPath(AutoMode mode, int hopper) throws InterruptedException{
    	LinkedBlockingQueue<Point> path = new LinkedBlockingQueue<Point>();
    	
    	if(alliance == Alliance.Red && hopper == 3){
    		DriverStation.reportError("We aren't allowed to go to that hopper", false);
    		hopper = -1;
    	}
    	else if(alliance == Alliance.Blue && hopper == 5){
    		DriverStation.reportError("We aren't allowed to go to that hopper", false);
    		hopper = -1;
    	}
    	
    	if(mode == AutoMode.GEAR || mode == AutoMode.GEAR_HOPPER){
    		if(startPointName == StartPoint.KEY){
    			if(alliance == Alliance.Red){
    				path.put(Point.getCoordinates(Setpoint.RED_GEAR3));
    			}
    			else if(alliance == Alliance.Blue){
    				path.put(Point.getCoordinates(Setpoint.BLUE_GEAR3));
    			}
    		}
    		else if(startPointName == StartPoint.MIDLINE){
    			if(alliance == Alliance.Red){
    				path.put(Point.getCoordinates(Setpoint.RED_GEAR2));
    			}
    			else if(alliance == Alliance.Blue){
    				path.put(Point.getCoordinates(Setpoint.BLUE_GEAR2));
    			}
    		}
    		else if(startPointName == StartPoint.RETRIEVAL_ZONE){
    			if(alliance == Alliance.Red){
    				path.put(Point.getCoordinates(Setpoint.RED_GEAR1));
    			}
    			else if(alliance == Alliance.Blue){
    				path.put(Point.getCoordinates(Setpoint.BLUE_GEAR1));
    			}
    		}
    	}
    	
    	if(mode == AutoMode.HOPPER_BOILER || mode == AutoMode.HOPPER || mode == AutoMode.GEAR_HOPPER){
    		if(startPointName == StartPoint.KEY && (hopper == 3 || hopper == 4 || hopper == 5)){
    			switch(hopper){
    				case 3:
    					path.put(Point.getCoordinates(Setpoint.HOPPER3));
    					break;
    				case 4:
    					path.put(Point.getCoordinates(Setpoint.HOPPER4));
    					break;
    				case 5:
    					path.put(Point.getCoordinates(Setpoint.HOPPER5));
    					break;
    			}
    		}
    		else if(startPointName == StartPoint.RETRIEVAL_ZONE && (hopper == 1 || hopper == 2)){
    			switch(hopper){
    				case 1:
    					path.put(Point.getCoordinates(Setpoint.HOPPER1));
    					break;
    				case 2:
    					path.put(Point.getCoordinates(Setpoint.HOPPER2));
    					break;
    			}
    		}
    	}
    	
    	if(mode == AutoMode.BOILER_CROSS || mode == AutoMode.HOPPER_BOILER){
    		if(alliance == Alliance.Red){
    			path.put(Point.getCoordinates(Setpoint.RED_BOILER));
    		}
    		else if(alliance == Alliance.Blue){
    			path.put(Point.getCoordinates(Setpoint.BLUE_BOILER));
    		}
    	}
    	
    	if(mode == AutoMode.CROSS || mode == AutoMode.BOILER_CROSS){
    		if(alliance == Alliance.Red){
    			path.put(new Point(PositionTracker.getCurrentPoint().getX(), 93.25));
    		}
    		else if(alliance == Alliance.Blue){
    			path.put(new Point(PositionTracker.getCurrentPoint().getX(), 558.75));
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
}
