package org.usfirst.frc.team5968.robot;

/**
 * Keeps track of the ports everything is plugged into. It's split into separate enums because
 * last year I kept forgetting, for example, that the joysticks weren't plugged into the same 
 * port 0 as the encoders.
 * 
 * @author BeijingStrongbow
 */
public class PortMap {
	
	/**
	 * Devices plugged into the driver station USB
	 */
	public enum USB {
		LEFT_JOYSTICK,
		RIGHT_JOYSTICK,
		XBOX;
	}
	
	/**
	 * Devices on the CAN bus. The PCM should also be included, once the pneumatics code
	 * is added.
	 */
	public enum CAN {
		LEFT_MOTOR_FRONT,
		LEFT_MOTOR_BACK,
		RIGHT_MOTOR_FRONT,
		RIGHT_MOTOR_BACK;
	}
	
	/**
	 * Devices plugged into the digital IO on the roboRIO. Doesn't include anything right now.
	 */
	public enum DIO {
		
	}
	
	/**
	 * Get the port a specific USB device is plugged into on the driver station.
	 * 
	 * @param u The device to get the port for
	 * @return The port the device is plugged into
	 */
	public static int portOf(USB u){
		switch(u){
			case LEFT_JOYSTICK:
				return 0;
			case RIGHT_JOYSTICK:
				return 1;
			case XBOX:
				return 2;
			default:
				return -1;
		}
	}
	
	/**
	 * Get the port a specific CAN bus device is plugged into on the robot
	 * 
	 * @param c The device to get the port for
	 * @return The port the device is plugged into
	 */
	public static int portOf(CAN c){
		switch(c){
			case LEFT_MOTOR_FRONT:
				return 3;
			case LEFT_MOTOR_BACK:
				return 4;
			case RIGHT_MOTOR_FRONT:
				return 5;
			case RIGHT_MOTOR_BACK:
				return 6;
			default:
				return -1;
		}
	}
	
	/**
	 * Get the port a specific digital IO device is plugged into on the roboRIO
	 * 
	 * @param d The dievice to get the port for
	 * @return The port the device is plugged into
	 */
	public static int portOf(DIO d){
		return -1;
	}
}
