package org.usfirst.frc.team5968.robot;

import org.usfirst.frc.team5968.robot.PortMap.CAN;
import org.usfirst.frc.team5968.robot.PortMap.DIO;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
public class DriveBase {
	
	private static NavXMXP navX = new NavXMXP();
	private static CANTalon leftMotorFront = new CANTalon(PortMap.portOf(CAN.LEFT_MOTOR_FRONT));
	private static CANTalon leftMotorBack = new CANTalon(PortMap.portOf(CAN.LEFT_MOTOR_BACK));
	private static CANTalon rightMotorFront = new CANTalon(PortMap.portOf(CAN.RIGHT_MOTOR_FRONT));
	private static CANTalon rightMotorBack = new CANTalon(PortMap.portOf(CAN.RIGHT_MOTOR_BACK));
	
	private static Encoder leftEncoder = new Encoder(PortMap.portOf(DIO.LEFT_ENCODER1), PortMap.portOf(DIO.LEFT_ENCODER2));
	private static Encoder rightEncoder = new Encoder(PortMap.portOf(DIO.RIGHT_ENCODER1), PortMap.portOf(DIO.RIGHT_ENCODER2));
	
	private static final double TOLERANCE = 0.5; //.5 degrees for angles, .5 inches for distance
	private static final double P = -.2;

	public static void driveInit(){
		leftEncoder.setDistancePerPulse(.0469); //this is in inches
		rightEncoder.setDistancePerPulse(.0469); //this is in inches
	}
	
    private static void driveStraight(double initialSpeed){
    	//resetNavX();
    	if (Math.abs(navX.getYaw()) < TOLERANCE){
    		setRaw(initialSpeed, initialSpeed);
    	}
		
    	else{
    		if (navX.getYaw() < TOLERANCE){
    			setRaw(initialSpeed + navX.getYaw() * P, initialSpeed);
    			navX.resetYaw();
    		} else if (navX.getYaw() > TOLERANCE){
    			setRaw(initialSpeed, initialSpeed + navX.getYaw() * P);
    			navX.resetYaw();
    		}
    	}
        
    }
    
    public static void setRaw(double leftSpeed, double rightSpeed){
    	leftMotorFront.set(leftSpeed);
    	leftMotorBack.set(leftSpeed);
    	rightMotorFront.set(-1 * rightSpeed);
    	rightMotorBack.set(-1 * rightSpeed);
    }
    
    public static void resetNavX(){
    	navX.resetYaw();
    }
    
    public static void resetEncoders(){
    	leftEncoder.reset();
    	rightEncoder.reset();
    }
}
