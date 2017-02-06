package org.usfirst.frc.team5968.robot;

import org.usfirst.frc.team5968.robot.PortMap.DIO;

import edu.wpi.first.wpilibj.Encoder;
import com.ctre.CANTalon;

public class RopeClimber{
	
	private static VictorSP rightMotor = new VictorSP(PortMap.PWM.CLIMBER_MOTOR_RIGHT);
	private static VictorSP leftMotor = new VictorSP(PortMap.PWM.CLIMBER_MOTOR_LEFT);
	private static Encoder climberEncoder = new Encoder(PortMap.portOf(DIO.CLIMBER_ENCODER_A), PortMap.portOf(DIO.CLIMBER_ENCODER_B));  
  
	private static final double startAngle = 5;	//Inches
	private static final double robotLength = 33;	//Inches  
	private static final double maxCurrent = 40;    //Tune
	private static double distance = 0;
  
	public static void init(){
  
		climberEncoder.setDistancePerPulse(8/2048);		//Inches
	}
	
	public static void setSpeed(double motorSpeed){
		
		rightMotor.set(motorSpeed);
		leftMotor.set(-motorSpeed);
	}
	
	public static double getCurrent(){
		
		return Math.abs(rightMotor.getOutputCurrent);
	}	
	
 	public static boolean motorClimb(){	//Prepares to climb
  
  		DriveBase.setRawFraction(0, 0);
  		setSpeed(.9);
  		
		boolean reachedDestination = false;
  		
  		double verticalAngle = NavXMXP.getPitch();
    
  		if(verticalAngle > startAngle){
  			
			distance = climberEncoder.getDistance() + ((Math.sin(startAngle * (Math.PI / 180))) * robotLength);
  			if(distance < 48){
  				
  				reachedDestination = false;
        		}
  			if(distance >= 48){
      
  				setSpeed(.8);
				reachedDestination = false;
  				if(getCurrent() >= maxCurrent){
				
					reachedDestination = true;	//Create if statement for boolean when calling method
				}
        		} 
  		}
  		return reachedDestination;
  	}
} 
    
    
    
  
  
    
    
    
  
  
  
