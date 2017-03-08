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
		LEFT_MOTOR_LEAD,
		LEFT_MOTOR_FOLLOW,
		RIGHT_MOTOR_LEAD,
		RIGHT_MOTOR_FOLLOW,
		PCM,
		PDP;
	}

	
	/**
	 * PWM outputs on the roboRIO.
	 */
	public enum PWM {
		CLIMBER_MOTOR_LEFT,
		CLIMBER_MOTOR_RIGHT,
		CLIMBER_ENCODER_A,
		CLIMBER_ENCODER_B,
		LEFT_DRIVE_ENCODER_A,
		LEFT_DRIVE_ENCODER_B,
		RIGHT_DRIVE_ENCODER_A,
		RIGHT_DRIVE_ENCODER_B;
	}
	
	/**
	 * Pneumatic devices plugged into the PCM
	 */
	public enum PCM {
		FRONT_PISTON_1,
		FRONT_PISTON_2,
		BACK_PISTON_1,
		BACK_PISTON_2;
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
			case LEFT_MOTOR_LEAD:
				return 5;
			case LEFT_MOTOR_FOLLOW:
				return 6;
			case RIGHT_MOTOR_LEAD:
				return 3;
			case RIGHT_MOTOR_FOLLOW:
				return 4;
			case PCM:
				return 0;
			case PDP:
				return 0;
			default:
				return -1;
		}
	}
	
	
	/**
	 * Get the port a specific pneumatic cylinder is plugged into on the PCM
	 * 
	 * @param p The cylinder to get the port for
	 * @return The port the cylinder is plugged into
	 */
	public static int portOf(PCM p){
		switch(p){
			case FRONT_PISTON_1:
				return 0;
			case FRONT_PISTON_2:
				return 1;
			case BACK_PISTON_1:
				return 2;
			case BACK_PISTON_2:
				return 3;
			default:
				return -1;
		}
	}
	
	public static int portOf(PWM p){
		switch(p){
			case CLIMBER_ENCODER_A:
				return 0;
			case CLIMBER_ENCODER_B:
				return 1;
			case CLIMBER_MOTOR_LEFT:
				return 2;
			case CLIMBER_MOTOR_RIGHT:
				return 3;
			case LEFT_DRIVE_ENCODER_A:
				return 4;
			case LEFT_DRIVE_ENCODER_B:
				return 5;
			case RIGHT_DRIVE_ENCODER_A:
				return 6;
			case RIGHT_DRIVE_ENCODER_B:
				return 7;
			default:
				return -1;
		}
	}
}
