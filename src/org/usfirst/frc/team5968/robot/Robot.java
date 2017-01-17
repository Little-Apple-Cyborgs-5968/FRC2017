package org.usfirst.frc.team5968.robot;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends RobotBase {
	
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
        
    private void robotInit(){
    	DriveBase.init();
    	DashboardConnection.init();
    	DashboardConnection.addModes();
    	//TODO: Fill in (0,0) with actual values
    	PositionTracker.init(0, 0); 
    }
        
    private void autoInit(){
    	
    }
    
    private void autoPeriodic(){
    	
    	
    }
    public void teleopInit(){
    	
    }
    
    public void teleopPeriodic(){
    	if(DashboardConnection.getDestinationPoint().getX() != -1 && 
    			DashboardConnection.getDestinationPoint().getY() != -1){
    		
    	}
    	else{
    		DriveBase.teleopDrive(HumanInterface.getLeftStick(), HumanInterface.getRightStick());
    	}
    }
    
    public void robotPeriodic(){
    	DashboardConnection.updateDashboardValues();
    	PositionTracker.updateCoordinates();
    }
    
    public static void waitMillis(long millis){
    	long start = System.currentTimeMillis();
    	
    	while(System.currentTimeMillis() - (millis + start) < 0){}
    }

}
