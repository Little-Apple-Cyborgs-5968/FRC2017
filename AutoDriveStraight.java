package org.usfirst.frc.team5968.robot;

import com.kauailabs.navx.frc.AHRS;

public class AutoDriveStraight {
	
	private NavXMXP navX = new NavXMXP();
	private final double TOLERANCE = 0.5;
	
    public void driveStraight() {
		navX.resetYaw();
    	while (navX.getYaw() < TOLERANCE) {
    		AutoShootManager.getDrive().setRaw(0.5, 0.5);
    	}
		
    	while (Math.abs(navX.getYaw()) >= TOLERANCE) {
    		if (navX.getYaw() < 0) {
    			AutoShootManager.getDrive().setRaw(0.6, 0.5);
    			navX.resetYaw();
    		} else if (navX.getYaw() > 0) {
    			AutoShootManager.getDrive().setRaw(0.5, 0.6);
    			navX.resetYaw();
    		}
    	}
        
    }
	
    
}
