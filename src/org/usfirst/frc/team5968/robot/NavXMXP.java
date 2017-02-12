package org.usfirst.frc.team5968.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;

/**
 * Manages the navX MXP
 * 
 * @author bk666
 * @author BeijingStrongbow
 */
public class NavXMXP {
	
	/**
	 * The navX
	 */
	private static AHRS navX;
	
	/**
	 * What acceleration is considered a collision. 
	 */
	private static final double COLLISION_THRESHOLD = 10; //TODO: test this

	/**
	 * Initializes the connection with the navX
	 */
	public static void init() {
		try {
			navX = new AHRS(SPI.Port.kMXP);
		} catch (RuntimeException ex) {
			DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
		}
		resetYaw();
	}

	/**
	 * positive means rotated clockwise.
	 * 
	 * @return The current yaw (rotation in z-axis) value in degrees (0 to 360). Clockwise is positive.
	 */
	public static double getYaw() {
		return navX.getFusedHeading();
	}

	/**
	 * positive angle means tilted backwards.
	 * 
	 * @return The current pitch (rotation in x-axis) value in degrees (-180 to
	 *         180).
	 */
	public static double getPitch() {
		return navX.getPitch();
	}

	/**
	 * positive means rolled left.
	 * 
	 * @return The current roll (rotation in y-axis) value in degrees (-180 to
	 *         180).
	 */
	public static double getRoll() {
		return navX.getRoll();
	}

	/**
	 * next call to navX.getYaw() will be relative to the current yaw value.
	 * 
	 */
	public static void resetYaw() {
		navX.zeroYaw();
		navX.zeroYaw();
	}
	
	/**
	 * Gets the x displacement in meters. Don't use for long periods of time, because error
	 * accumulates quickly. I hope a few seconds is ok.
	 * 
	 * @return The field-oriented X displacement measured by the navX since the last reset
	 */
	public static double getDisplacementX(){
		//Not entirely sure which direction this is on the robot.
		return navX.getDisplacementX();
	}
	
	/**
	 * Gets the y displacement. Don't use for long periods of time, because error
	 * accumulates quickly. I hope a few seconds is ok.
	 * 
	 * @return The field-oriented Y displacement measured by the navX since the last reset
	 */
	public static double getDisplacementY(){
		//Not entirely sure which direction this is on the robot.
		return navX.getDisplacementY();
	}
	
	/**
	 * Resets the displacement measured by the accelerometer
	 */
	public static void resetAccelerometer(){
		navX.resetDisplacement();
	}
	
	/**
	 * Converts a 0 to 360 (clockwise positive) degree angle to a 0 to 2 pi radian scale (counter clockwise positive)
	 * 
	 * @param The angle from 0 to 360 degrees to convert to radians
	 * @return The angle from 0 to 2 pi radians
	 */
	public static double convertAngleToRadians(double angle){
		angle = angle * Math.PI / 180;
		
		angle = 360 - angle;
		
		return angle;
	}
	
	/**
	 * Get whether a collision occurred 
	 * 
	 * @return Whether a collision occurred 
	 */
	public static boolean getCollisionHappened(){
		return navX.getWorldLinearAccelX() > COLLISION_THRESHOLD ||
				navX.getWorldLinearAccelY() > COLLISION_THRESHOLD;
	}
}
