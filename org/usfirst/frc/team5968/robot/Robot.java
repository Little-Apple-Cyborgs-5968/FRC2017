
package org.usfirst.frc.team5968.robot;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tInstances;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tResourceType;
import edu.wpi.first.wpilibj.communication.UsageReporting;
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
    	UsageReporting.report(tResourceType.kResourceType_Framework, tInstances.kFramework_Iterative);
    	FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramStarting();
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
    		 
    			FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramTest();
    		} 
    		else if (isAutonomous()) {
    			// call Autonomous_Init() if this is the first time
    			// we've entered autonomous_mode
    			if(!autoInitialized){
    				autoInit();
    				autoInitialized = true;
    			}
    			
    			autoPeriodic();
				FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramAutonomous();
    		}
    		else {
    			FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramTeleop();
    			
    			if(!teleopInitialized){
    				teleopInit();
    				teleopInitialized = true;
    			}
    			teleopPeriodic();
    		}
    		periodic();
    		m_ds.waitForData(); 
    	}
    }
    
    private CameraVision cv = new CameraVision();
    
    private void robotInit(){
    	//UsbCamera.init();
    	DriveBase.init();
    	DashboardConnection.init();
    	//cv.init();
    }
    
    private enum AutoState {
    	DRIVING,
    	TURNING,
    	IDLE;
    }
    
    private AutoState autoState = AutoState.TURNING;
    
    private void autoInit(){
    	DashboardConnection.startTimer();
    }
    
    private void autoPeriodic(){
    	
    	
    }
    public void teleopInit(){
    }
    
    public void teleopPeriodic(){
    	//cv.getTarget();
    }
    
    public void periodic(){
    	DashboardConnection.updateCameraView();
    	DashboardConnection.updateDashboardValues();
    }
    
    public static void waitMillis(long millis){
    	long start = System.currentTimeMillis();
    	
    	while(System.currentTimeMillis() - (millis + start) < 0){}
    }

}
