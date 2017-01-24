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
		RIGHT_MOTOR_BACK,
		PCM;
	}
	
	/**
	 * Devices plugged into the digital IO on the roboRIO. 
	 */
	public enum DIO {
		CLIMBER_ENCODER_A,
		CLIMBER_ENCODER_B,
	}
	
	/**
	 * PWM outputs on the roboRIO.
	 */
	public enum PWM {
		CLIMBER_MOTOR_LEFT,
		CLIMBER_MOTOR_RIGHT;
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
			case LEFT_MOTOR_FRONT:
				return 0;
			case LEFT_MOTOR_BACK:
				return 6;
			case RIGHT_MOTOR_FRONT:
				return 3;
			case RIGHT_MOTOR_BACK:
				return 4;
			case PCM:
				return 0;
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
		switch(d){
			case CLIMBER_ENCODER_A:
				return 0;
			case CLIMBER_ENCODER_B:
				return 1;
			default:
				return -1;
		}
	}
	
	/**
	 * Get the port a specific PWM device is plugged into on the roboRIO
	 * 
	 * @param p The device to get the port for
	 * @return The port the device is plugged into
	 */
	public static int portOf(PWM p){
		switch(p){
		 	case CLIMBER_MOTOR_LEFT:
		 		return 0;
			case CLIMBER_MOTOR_RIGHT:
				return 1;
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
}
