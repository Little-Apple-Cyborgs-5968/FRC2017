package org.usfirst.frc.team5968.robot;

public class PortMap {
	public enum USB {
		LEFT_JOYSTICK,
		RIGHT_JOYSTICK,
		XBOX;
	}
	
	public enum CAN {
		LEFT_MOTOR_FRONT,
		LEFT_MOTOR_BACK,
		RIGHT_MOTOR_FRONT,
		RIGHT_MOTOR_BACK;
	}
	
	public enum DIO {
		
	}
	
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
	
	public static int portOf(DIO d){
		return -1;
	}
}
