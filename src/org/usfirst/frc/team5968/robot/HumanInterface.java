package org.usfirst.frc.team5968.robot;

import org.usfirst.frc.team5968.robot.PortMap.USB;

import edu.wpi.first.wpilibj.Joystick;

/**
 * Appropriately, this class handles interfacing with the drivers. It only does the joysticks and Xbox controller
 * though - the dashboard interface is separate.
 * 
 * @author BeijingStrongbow
 */
public class HumanInterface {
	
	/**
	 * Joystick controlling the left side of the robot
	 */
	private static Joystick leftStick = new Joystick(PortMap.portOf(USB.LEFT_JOYSTICK));
	
	/**
	 * Joystick controlling the right side of the robot
	 */
	private static Joystick rightStick = new Joystick(PortMap.portOf(USB.RIGHT_JOYSTICK));
	
	/**
	 * If the joystick inputs are less than this amount, they will be set to 0.
	 */
	private static final double DEADZONE = .02;
	
	private static boolean isPRESSED = false;
	/**
	 * Gets the value from the Y axis of the left stick, since that's
	 * the only value we use
	 * 
	 * @return The Y value read by the left joystick
	 */
	public static double getLeftStick(){
		double y = leftStick.getY();
		if(Math.abs(y) <= DEADZONE){
			y = 0;
		}
		return y; 
	}
	
	/**
	 * Gets the value from the Y axis of the right stick, since that's
	 * the only value we use
	 * 
	 * @return The Y value read by the right joystick
	 */
	public static double getRightStick(){
		double y = rightStick.getY();
		if(Math.abs(y) <= DEADZONE){
			y = 0;
		}
		return y;
	}
	
	public static void liftControl(){
		if((rightStick.getRawButton(1) || leftStick.getRawButton(1)) && !isPRESSED){
			Pneumatics.DoubleSolenoidTOGGLE();
		}
		isPRESSED = rightStick.getRawButton(1) || leftStick.getRawButton(1);
	}
	
	/**
	 * Stop the climber if it isn't stopping, the robot is falling apart, the field is exploding, etc.
	 */
	public static void emergencyStopClimberControl(){
		if(rightStick.getRawButton(2) || leftStick.getRawButton(2)){
			Robot.getClimberThread().interrupt();
		}
	}
}
