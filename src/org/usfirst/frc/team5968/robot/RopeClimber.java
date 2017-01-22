package org.usfirst.frc.team5968.robot;

import org.usfirst.frc.team5968.robot.PortMap.DIO;

import edu.wpi.first.wpilibj.Encoder;
import com.ctre.CANTalon;

//TODO @allen458: fix some bugs, and document your code. Look at the other classes for examples on that.

public class RopeClimber{
	
	//sorry, these should actually be VictorSP instances. The port will be, for example,
	//PortMap.portOf(PortMap.PWM.CLIMBER_MOTOR_LEFT) for the left side
	private static CANTalon leftMotor = new CANTalon(-1);
	private static CANTalon rightMotor = new CANTalon(-1);
	private static Encoder climberEncoder = new Encoder(PortMap.portOf(DIO.CLIMBER_ENCODER_A), PortMap.portOf(DIO.CLIMBER_ENCODER_B));  
  
	//these should be final I think
	private static double startAngle = 5;	//Inches
	private static double robotLength = 33;	//Inches  
  
	public static void init(){
		//since they are actually VictorSP's, there's no follower mode. You'll probably want to write a method
		//to set both the right and left motors to the same speed instead.
		leftMotor.changeControlMode(CANTalon.TalonControlMode.Follower); 
		leftMotor.set(-1);
  
		climberEncoder.setDistancePerPulse(8/2048);		//Inches
	}
	//the convention for variable naming is a camel case (so lower case for the first letter of the first word, then upper case
	//for the first letters of all the other words. For an example name, thisIsADouble.
	private static double Distance = 0;
  
  	public static boolean climberMotorStart(){	//Prepares to climb
  
  		DriveBase.setRawFraction(0, 0);
  		//will need to call the method to set both the right and left motors, since there's no follower mode
  		rightMotor.set(.8);
  		boolean reachedDestination = false;
  		
  		double verticalAngle = NavXMXP.getPitch();
    
  		if(verticalAngle > 5){
  			//You'll need to go like 1 inch more than 58, because the bottom of the touchpad is 58 inches up and we need to push
  			//the touchpad.
  			if(Distance < 58){
  				//seems weird to calculate distance traveled here. I think it'll work though
  				Distance = climberEncoder.getDistance() + ((Math.tan(startAngle * (Math.PI / 180))) * robotLength);
  				reachedDestination = false;
        
  			}
  			if(Distance >= 58){
      
  				rightMotor.set(0);	//I need to brake it
  				reachedDestination = true;	//Create if statement for boolean when calling method
        
  			} 
  		}
  		return reachedDestination;
  	}
} 
    
    
    
  
  
    
    
    
  
  
  
