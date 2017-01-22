package org.usfirst.frc.team5968.robot;

import org.usfirst.frc.team5968.robot.PortMap.USB;

import edu.wpi.first.wpilibj.Joystick;

import edu.wpi.first.wpilibf.XboxController;

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
	 * Operator's xbox controller
	 */
	private static Joystick xbox = new Joystick(PortMap.portOf(USB.XBOX));
	
	/**
	 * If the joystick inputs are less than this amount, they will be set to 0.
	 */
	private static final double DEADZONE = .02;
	
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
	
	//TODO: @allen458 fix a few bugs
	
	public static void liftControl(){
		//there's already an instance you can use up there^^ It's called xbox. It's an instance of Joystick,
		//which is a bit confusing, but it's the right thing.
		XboxController controller = new XboxController();
		boolean isUp = true;
		Pneumatics lifter = new Pneumatics();
		
		//I believe this should be controller instead of counter
		
		//The method for getting a button is actually Joystick.getRawButton(int number). I don't know for sure what number A
		//is, but we can check.
		
		//The better code design here would be to have the pneumatics class worry about whether it should be going up or down.
		//The idea is a given class should only have access to what it needs to "know about," and there's no reason the human
		//interface should have to worry about what position the pistons are in.
		if(counter.getAButton() && isUp) {
			
			lifter.DoubleSolenoidUP();
			isUp = false;
			
		}	
		if(counter.getAButton() && !isUp){
			
			lifter.DoubleSolenoidDOWN();
			isUp = true;
		
		}
		
		
	}	
}
