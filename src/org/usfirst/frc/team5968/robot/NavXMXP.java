package org.usfirst.frc.team5968.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;

public class NavXMXP {
	private static AHRS navX;

	public static void init() {
		try {
			navX = new AHRS(SPI.Port.kMXP);
		} catch (RuntimeException ex) {
			DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
		}
	}

	/**
	 * positive means rotated clockwise.
	 * 
	 * @return The current yaw(rotation in z-axis) value in degrees (-180 to
	 *         180).
	 * @see {@link AHRS#getYaw()}
	 */
	public static double getYaw() {
		return navX.getFusedHeading();
	}

	/**
	 * positive angle means tilted backwards.
	 * 
	 * @return The current pitch(rotation in x-axis) value in degrees (-180 to
	 *         180).
	 * @see {@link AHRS#getPitch()}
	 */
	public static double getPitch() {
		return navX.getPitch();
	}

	/**
	 * positive means rolled left.
	 * 
	 * @return The current roll(rotation in y-axis) value in degrees (-180 to
	 *         180).
	 * @see {@link AHRS#getRoll()}
	 */
	public static double getRoll() {
		return navX.getRoll();
	}

	/**
	 * next call to {@link #getYaw()} will be relative to the current yaw value.
	 * 
	 * @see {@link AHRS#zeroYaw()}
	 */
	public static void resetYaw() {
		navX.zeroYaw();
	}
	
	/*
	 * Gets the x displacement in meters. Don't use for long periods of time, because error
	 * accumulates quickly. I hope a few seconds is ok.
	 */
	public static double getDisplacementX(){
		//Not entirely sure which direction this is on the robot.
		return navX.getDisplacementX();
	}
	
	/*
	 * Gets the y displacement. Don't use for long periods of time, because error
	 * accumulates quickly. I hope a few seconds is ok.
	 */
	public static double getDisplacementY(){
		//Not entirely sure which direction this is on the robot.
		return navX.getDisplacementY();
	}
	
	/*
	 * Resets the displacement measured by the accelerometer
	 */
	public static void resetAccelerometer(){
		navX.resetDisplacement();
	}
	
	/*
	 * Converts a -180 to 180 degree angle to a 0 to 2 pi radian scale
	 */
	public static double convertAngleToRadians(double angle){
		if(angle >= 0 && angle <= Math.PI / 2){
			angle = Math.PI / 2 - angle;
		}
		else if(angle > Math.PI / 2){
			angle = Math.PI + angle;
		}
		else if(angle >= -1 * Math.PI / 2 && angle < 0){
			angle = Math.PI / 2 - angle; // angle is negative
		}
		else{ //if angle > -180
			angle = Math.PI + (-1 * angle - Math.PI / 2);
		}
		
		return angle;
	}
}
