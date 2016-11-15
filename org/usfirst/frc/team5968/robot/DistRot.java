package org.usfirst.frc.team5968.robot;

public class DistRot {
	
	private DriveBase drive = new DriveBase();
	private NavXMXP navX = drive.getNavX();
	private double speed = 0.5;
	private double TOLERANCE = 0.2;
	private boolean doneDriving = false;
	
	public boolean driveDistance(double inches) {
		// DriveBase.resetEncoders();
    	if (Math.abs(drive.getDistance() - inches) >= TOLERANCE) {
    		if (drive.getDistance() < inches) {
    			DriveBase.setRaw(speed, speed);
    		} else if (drive.getDistance() > inches) {
    			DriveBase.setRaw(-speed, -speed); 
    		}
    		return false;
    	}
    	else {
    		DriveBase.setRaw(0, 0);
    		return true;
    	}
	}
	
	public boolean driveRotation(double radians) {
		// navX.resetYaw();
    	if (Math.abs(navX.getYaw() - radians) >= TOLERANCE) {
    		if (navX.getYaw() > radians) {
    			DriveBase.setRaw(-speed, speed);
    		} else if (navX.getYaw() < radians){
    			DriveBase.setRaw(speed, -speed);
    		}
    		return false;
    	}    
    	else {
    		DriveBase.setRaw(0, 0);
    		return true;
    	}
	}
}
