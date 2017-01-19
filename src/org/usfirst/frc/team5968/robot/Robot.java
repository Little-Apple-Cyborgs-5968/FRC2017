package org.usfirst.frc.team5968.robot;

import edu.wpi.first.wpilibj.RobotBase;
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
    	LiveWindow.setEnabled(false);
    	robotInit();
    	
    	boolean autoInitialized = false;
    	boolean teleopInitialized = false;
    	
    	while (true) {
    		// Call the appropriate function depending upon the current robot mode
    		if (isDisabled()) {
    			// call DisabledInit() if we are now just entering disabled mode from
    			// either a different mode or from power-on
    		}
    		else if (isTest()) {
    			// call TestInit() if we are now just entering test mode from either
    			// a different mode or from power-on
    		} 
    		else if (isAutonomous()) {
    			// call Autonomous_Init() if this is the first time
    			// we've entered autonomous_mode
    			if(!autoInitialized){
    				autoInit();
    				autoInitialized = true;
    			}
    			
    			autoPeriodic();
    		}
    		else {
    			
    			if(!teleopInitialized){
    				teleopInit();
    				teleopInitialized = true;
    			}
    			teleopPeriodic();
    		}
    		robotPeriodic();
    		m_ds.waitForData(); 
    	}
    }
    
    /**
     * Called when the robot turns on
     */
    private void robotInit(){
    	DriveBase.init();
    	DashboardConnection.init();
    	DashboardConnection.addModes();
    	//TODO: Fill in (0,0) with actual values
    	PositionTracker.init(0, 0); 
    }
    
    /**
     * Called at the beginning of autonomous.
     */
    private void autoInit(){
    	
    }
    
    /**
     * Called periodically during autonomous
     */
    private void autoPeriodic(){
    	
    	
    }
    
    /**
     * Called at the beginning of teleop
     */
    public void teleopInit(){
    	
    }
    
    /**
     * Called periodically during teleop
     */
    public void teleopPeriodic(){
    	if(DashboardConnection.getDestinationPoint().getX() != -1 && 
    			DashboardConnection.getDestinationPoint().getY() != -1){
    		
    	}
    	else{
    		DriveBase.teleopDrive(HumanInterface.getLeftStick(), HumanInterface.getRightStick());
    	}
    }
    
    /**
     * Called periodically during the entire match. This should be used for any code that needs to run
     * at all times, to avoid pasting it twice.
     */
    public void robotPeriodic(){
    	DashboardConnection.updateDashboardValues();
    	PositionTracker.updateCoordinates();
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

}
