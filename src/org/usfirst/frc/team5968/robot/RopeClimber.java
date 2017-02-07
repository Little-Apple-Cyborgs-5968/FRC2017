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
	private static double motorSpeed = 0;
	private static double distance = 0;
	
	private static boolean isSetToPoint4 = false;
	private static boolean isAccelerated = false;
  
	public static void init(){
  
		climberEncoder.setDistancePerPulse(8/2048);		//Inches
	}
	
	public static void setSpeed(double motorSpeed){
		
		rightMotor.set(motorSpeed);
		leftMotor.set(-motorSpeed);
	}
	
	public static void climbingAcceleration(){
		
		if(motorSpeed < .9){
			
			motorSpeed = motorSpeed + .1;
			isAccelerated = false;
		}
		if motorSpeed >= .9){
			
			isAccelerated = true;
		}
	}
		
	public static double getCurrent(){
		
		return Math.abs(rightMotor.getOutputCurrent);
	}	
	
 	public static boolean motorClimb(){	//Prepares to climb
  		
		boolean reachedDestination = false;
  		
		if(!isSetToPoint4){
			
			setSpeed(.4);
			isSetToPoint4 = true;
		}
		
  		double verticalAngle = NavXMXP.getPitch();
    
  		if(verticalAngle > startAngle){
  			
			DriveBase.setRawFraction(0, 0);
			
			if(!isAccelerated){
				
				climberAcceleration();
			}
			
			distance = climberEncoder.getDistance() + ((Math.sin(startAngle * (Math.PI / 180))) * robotLength);
  			if(distance < 48){
  				
  				reachedDestination = false;
        		}
  			if(distance >= 48){
      
  				setSpeed(.8);
				reachedDestination = false;
  				
        		} 
			if(getCurrent() >= maxCurrent){
				
					reachedDestination = true;	//Create if statement for boolean when calling method
			}
  		}
  		return reachedDestination;
  	}
} 
    
    
    
  
  
    
    
    
  
  
  
