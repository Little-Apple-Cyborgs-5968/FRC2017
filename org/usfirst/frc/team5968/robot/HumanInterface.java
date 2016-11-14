package org.usfirst.frc.team5968.robot;

import org.usfirst.frc.team5968.robot.PortMap.USB;

import edu.wpi.first.wpilibj.Joystick;

public class HumanInterface {
	private static Joystick leftStick = new Joystick(PortMap.portOf(USB.LEFT_JOYSTICK));
	private static Joystick rightStick = new Joystick(PortMap.portOf(USB.RIGHT_JOYSTICK));
	private static Joystick xbox = new Joystick(PortMap.portOf(USB.XBOX));
	
	//Gets the value from the Y axis of the left stick, since that's
	//the only value we use
	public static double getLeftStick(){
		return leftStick.getY();
	}
	
	//Gets the value from the Y axis of the right stick, since that's
	//the only value we use
	public static double getRightStick(){
		return rightStick.getY();
	}
}
