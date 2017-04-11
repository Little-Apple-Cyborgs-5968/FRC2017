package org.usfirst.frc.team5968.robot;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.Relay;
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
public class Lights {
	private DigitalOutput w = new DigitalOutput(0);
	private DigitalOutput b = new DigitalOutput(8);
	private DigitalOutput r = new DigitalOutput(1);
	private DigitalOutput g = new DigitalOutput(9);
	
	private static Relay ledRing = new Relay(0);
    
    public void green() {
    	g.set(true);
    	r.set(false);
    	b.set(false);
    	w.set(false);
    }

    public void red() {
    	g.set(false);
    	r.set(true);
    	b.set(false);
    	w.set(false);
    }
    
    public void blue() {
    	g.set(false);
    	r.set(false);
    	b.set(true);
    	w.set(false);
    }
    
    public void white() {
    	g.set(false);
    	r.set(false);
    	b.set(false);
    	w.set(true);
    }
    
    public void purple() {
    	g.set(false);
    	r.set(true);
    	b.set(true);
    	w.set(false);
    }
    
    public void turquoise() {
    	g.set(true);
    	r.set(false);
    	b.set(true);
    	w.set(false);
    }
    
    public void yellow() {
    	w.set(false);
    	r.set(true);
    	g.set(true);
    	b.set(false);
    }
    
    public void off() {
    	w.set(false);
    	r.set(false);
    	g.set(false);
    	b.set(false);
    }
    
    public void allianceColor() {
    	if (DriverStation.getInstance().getAlliance() == Alliance.Red) {
    		red();
    	} else if(DriverStation.getInstance().getAlliance() == Alliance.Blue) {
    		blue();
    	} else {
    		white();
    	}
    }
    
    public void climbFlashing() {
    	if(((int)Timer.getMatchTime()) % 2 == 0) {
    		turquoise();
    	} else {
    		white();
    	}
    }
    
    public void endGearBackUpFlashing(double time) {
    	
    	
    }
    
    /**
     * Turn the LED ring on/off
     */
    public static void toggleLedRing(){
    	if(ledRing.get() == Relay.Value.kForward){
    		ledRing.set(Relay.Value.kOff);
    	}
    	else{
    		ledRing.set(Relay.Value.kForward);
    	}
    }
}
