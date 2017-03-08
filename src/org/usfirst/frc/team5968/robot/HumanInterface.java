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
	private static final double DEADZONE = .01;
	
	private static boolean isPRESSED = false;
	
	private static boolean climberManual = false;
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
			RopeClimber.eStopClimber();
		}
	}
	
	public static void manualClimb(){
		if(leftStick.getRawButton(5) || rightStick.getRawButton(6)){
			RopeClimber.manualClimb();
			climberManual = true;
		}
		else if(climberManual){
			RopeClimber.eStopClimber();
		}
	}
	
	private static boolean pressed = false;
	
	public static void reverseControls(){
		if((rightStick.getRawButton(5) || leftStick.getRawButton(6)) && !pressed){
			DriveBase.reverseControls();
		}
		pressed = rightStick.getRawButton(5) || leftStick.getRawButton(6);
	}
	
	private static boolean driveBack = false;
	
	private static long startTime = System.currentTimeMillis();
	
	public enum TurnAroundState{
		DRIVE1,
		TURN,
		DRIVE2,
		IDLE;
	}
	
	public static void backUpForGear(){
		if((leftStick.getRawButton(4) || rightStick.getRawButton(3))){
			startTime = System.currentTimeMillis();
			driveBack = true;
		}
		
		if(driveBack){
			if(DriveBase.driveDistance(6, .3) || System.currentTimeMillis() - startTime > 1500){
				driveBack = false;
			}
		}
	}
	
	private static TurnAroundState turnState = TurnAroundState.IDLE;
	
	public static void turnAroundForFuel(){
		if((leftStick.getRawButton(2) || rightStick.getRawButton(2))){
			startTime = System.currentTimeMillis();
			turnState = TurnAroundState.DRIVE1;
		}
		
		if(turnState != TurnAroundState.IDLE){
			if(System.currentTimeMillis() - startTime >= 3000){
				turnState = TurnAroundState.IDLE;
			}
			else if(turnState == TurnAroundState.DRIVE1){
				if(DriveBase.driveDistance(AutoManager.getSafeTurnDistance(), 0.25)){
					turnState = TurnAroundState.TURN;
				}
			}
			else if(turnState == TurnAroundState.TURN){
				if(DriveBase.driveRotation(180)){
					turnState = TurnAroundState.DRIVE2;
				}
			}
			else if(turnState == TurnAroundState.DRIVE2){
				if(DriveBase.driveDistance(AutoManager.getSafeTurnDistance(), 0.25)){
					turnState = TurnAroundState.IDLE;
				}
			}
		}
	}
}
