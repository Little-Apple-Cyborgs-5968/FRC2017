package org.usfirst.frc.team5968.robot;

import org.usfirst.frc.team5968.robot.PortMap.USB;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

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
	
	/**
	 * Whether the lift control button was pressed on the last loop. This means it will only change
	 * the lift state if the button was previously unpressed, and is now pressed.
	 */
	private static boolean liftControlPressed = false;
	
	/**
	 * Whether the driver has taken over manual control of the climber
	 */
	private static boolean climberManual = false;
	
	private static Lights lights = Robot.getLights();
	private static Timer systemTimer = new Timer();
	private static double flashEndTime = 0.0;
	private static int remainingFlashCount = 0;
	private static boolean flashOnCycle = false;
	
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
	
	/**
	 * Checks all the button controls
	 */
	public static void buttonControls(){
		liftControl();
		manualClimb();
		//reverseControls();
		backUpForGear();
		turnAroundForFuel();
	}
	
	/**
	 * Control the bin bottom
	 * Uses Button 1 on either joystick
	 */
	private static void liftControl(){
		/*if((rightStick.getRawButton(1) || leftStick.getRawButton(1)) && !liftControlPressed){
			Pneumatics.DoubleSolenoidTOGGLE();
		}
		liftControlPressed = rightStick.getRawButton(1) || leftStick.getRawButton(1);*/
	}
	
	/**
	 * Control the climber manually. If it's still being controlled automatically, the human
	 * will take over control.
	 * Uses Button 5 on Left, Button 6 on Right
	 */
	private static void manualClimb(){
		if(leftStick.getRawButton(5) || rightStick.getRawButton(6)){
			RopeClimber.manualClimb();
			climberManual = true;
		}
		else if(climberManual){
			RopeClimber.eStopClimber();
		}
	}
	
	/**
	 * Whether the reverse controls button was pressed on the last iteration. This means the controls will
	 * only be reversed if the button wasn't pressed on the last iteration.
	 */
	private static boolean reverseControlsPressed = false;
	
	/**
	 * "Reverse" the front of the robot. I.e. if forward is toward the dump side, this will change it so
	 * forward is toward the climb side, or vice versa.
	 */
	/*private static void reverseControls(){
		if((rightStick.getRawButton(5) || leftStick.getRawButton(6)) && !reverseControlsPressed){
			DriveBase.reverseControls();
		}
		reverseControlsPressed = rightStick.getRawButton(5) || leftStick.getRawButton(6);
	}*/
	
	/**
	 * Whether the robot should be driving back to get a gear right now
	 */
	private static boolean driveBack = false;
	
	public static boolean isDrivingBack(){
		return driveBack;
	}
	
	/**
	 * The start time of the last backUpForGear. The drive will stop after 1.5 seconds if the robot
	 * hasn't reached its target by then. (There was an issue where the controls would freeze up after
	 * calling this because the robot never reached the target distance)
	 */
	private static long startTime = System.currentTimeMillis();
	
	/**
	 * The distance to reverse to collect a gear;
	 */
	private static final double BACK_UP_DISTANCE = 0.75;
	
	private static double distanceDriven = 0;
	
	/**
	 * The state of the last turn around. This should be used when you go to the retrieval zone to collect
	 * a gear then push a button, and the robot will turn around to immediately collect fuel.
	 */
	private enum TurnAroundState{
		DRIVE1,
		TURN,
		DRIVE2,
		IDLE;
	}
	
	/**
	 * Back up a set distance to collect a gear. The robot should be against the wall when this
	 * button is pressed. This will be canceled if the driver moves either joystick.
	 * Button 4 on Left, Button 5 on Right
	 */
	private static void backUpForGear(){
		if((leftStick.getRawButton(4) || rightStick.getRawButton(3))){
			startTime = System.currentTimeMillis();
			DriveBase.resetEncoders();
			driveBack = true;
		}
		
		if(driveBack){
			if(System.currentTimeMillis() - startTime > 1000){
				driveBack = false;
			}
			
			distanceDriven = (DriveBase.getRightDistance() - DriveBase.getLeftDistance()) / 2;
			System.out.println(distanceDriven);
			DriveBase.setRawFraction(0.25, 0.25);
			driveBack = distanceDriven < BACK_UP_DISTANCE;
		}
		else{
			DriveBase.setRawFraction(0.0, 0.0);
		}
	}
	
	public static boolean isLightsFlashing() {
		final double flashTime = 0.1;
		
		if((leftStick.getRawButton(8) || rightStick.getRawButton(8))) {
			flashEndTime = systemTimer.get() + flashTime;
			remainingFlashCount = 10;
			flashOnCycle = true;
		}
		
		if (systemTimer.get() >= flashEndTime) {
			remainingFlashCount--;
			flashEndTime = systemTimer.get() + flashTime;
			flashOnCycle = !flashOnCycle;
		}
		
		if (remainingFlashCount <= 0) {
			return false;
		}
		
		if(flashOnCycle) {
			lights.yellow();
			return true;
		}
		
		return false;
	}
	
	/**
	 * The state of turning around for a gear
	 */
	private static TurnAroundState turnState = TurnAroundState.IDLE;
	
	/**
	 * Turn around to collect fuel. This should be used when the robot is at the retrieval zone collecting
	 * a gear. The driver will press this button, and the robot will back up, turn around, and drive forward
	 * again and will be in position to collect fuel. This will cancel if the driver moves either joystick.
	 * Button 2 on Left, Button 3 on Right
	 */
	private static void turnAroundForFuel(){
		if(getLeftStick() != 0 || getRightStick() != 0){
			turnState = TurnAroundState.IDLE;
		}
		
		if((leftStick.getRawButton(2) || rightStick.getRawButton(2))){
			startTime = System.currentTimeMillis();
			turnState = TurnAroundState.DRIVE1;
		}
		
		if(turnState != TurnAroundState.IDLE){
			if(System.currentTimeMillis() - startTime >= 3000){
				turnState = TurnAroundState.IDLE;
			}
			else if(turnState == TurnAroundState.DRIVE1){
				if(DriveBase.driveDistance(AutoManager.getSafeTurnDistance() - BACK_UP_DISTANCE, 0.25)){
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
