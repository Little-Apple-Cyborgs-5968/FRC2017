
package org.usfirst.frc.team5968.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Lights extends IterativeRobot {
	PWM r = new PWM(0);
	PWM g = new PWM(1);
	PWM b = new PWM(2);
	Pneumatics p = new Pneumatics();
	int timer = 0;
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void upBrighness() {
	    for(int x =0; x<=255; x++){
	    	r.setRaw(x);
	    	g.setRaw(x);
	    	b.setRaw(x);
	    }
    }
    
	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString line to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the switch structure below with additional strings.
	 * If using the SendableChooser make sure to add them to the chooser code above as well.
	 */
    public void off() {
    	r.setRaw(0);
    	g.setRaw(0);
    	b.setRaw(0);
    	
    }

    /**
     * This function is called periodically during autonomous
     */
    public void green() {
    	g.setRaw(26);
    }

    /**
     * This function is called periodically during operator control
     */
    public void pneumatics() {
       
    	if(p.isUp()) {
    		r.setRaw(146);
    		g.setRaw(14);
    		b.setRaw(14);
    	}
    	if(!p.isUp())  {
    		b.setRaw(87);
    	}
    	
    }
    public void climbing() {
    	timer++;
        if ((timer / 1000) % 2 == 0) {
        	r.setRaw(1);
        	b.setRaw(1);
        	
        	if((timer/19.6) % 2==0) {
        	int v = (r.getRaw()) + 1;
        } else {
        	r.setRaw(255);
        	g.setRaw(255);
        	b.setRaw(255);
        	
        	if(timer>=5000){
        	r.setRaw(60);
        	g.setRaw(242);
        	b.setRaw(193);
        	    }
        	}
        }
    }
    
}
