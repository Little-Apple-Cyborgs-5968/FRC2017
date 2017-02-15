package org.usfirst.frc.team5968.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;

public class RopeClimber implements Runnable {
	
	private static VictorSP rightMotor;
	private static VictorSP leftMotor;
	private static Encoder climberEncoder;  
  
	private static PowerDistributionPanel pdp = new PowerDistributionPanel(PortMap.portOf(PortMap.CAN.PDP));
	
	private static final double startAngle = 5;	//Inches
	private static final double robotLength = 33;	//Inches  
	private static final double maxCurrent = 40;    //Tune
	private static double motorSpeed = 0;
	private static double distance = 0;
	
	private static boolean isSetToPoint4 = false;
	private static boolean isAccelerated = false;
	
	private static int direction = 0;
	
	private static boolean manualStart = false;
	
	public void run() {
		while(!motorClimb()){
			if(Thread.interrupted()){
				return;
			}
		}
	}
  
	public static void init(){
		rightMotor = new VictorSP(PortMap.portOf(PortMap.PWM.CLIMBER_MOTOR_RIGHT));
		leftMotor = new VictorSP(PortMap.portOf(PortMap.PWM.CLIMBER_MOTOR_LEFT));
		climberEncoder = new Encoder(PortMap.portOf(PortMap.PWM.CLIMBER_ENCODER_A), PortMap.portOf(PortMap.PWM.CLIMBER_ENCODER_B));  
		climberEncoder.setDistancePerPulse(8 / 2048); //inches
	}
	
	public static void setSpeed(double motorSpeed){
		
		rightMotor.set(-motorSpeed);
		leftMotor.set(motorSpeed);
	}
	
	public static void climbingAcceleration(){
		
		if(motorSpeed < .9){
			
			motorSpeed = motorSpeed + .1;
			isAccelerated = false;
		}
		if(motorSpeed >= .9){
			
			isAccelerated = true;
		}
	}
		
	public static double getCurrent(){
		
		return Math.abs(pdp.getCurrent(14)); //right motor
	}	
	
 	public static boolean motorClimb(){	//Prepares to climb
		
		if(Timer.getMatchTime() > 30){
			return false;
		}
		direction = 1;
		boolean reachedDestination = false;
 
		if(!isSetToPoint4){
			
			setSpeed(.05);
			isSetToPoint4 = true;
		}
		
  		double verticalAngle = NavXMXP.getPitch();
    
  		if(verticalAngle > startAngle){
  			
			DriveBase.setRawFraction(0, 0);
			
			if(!isAccelerated){
				
				climbingAcceleration();
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
					
					setSpeed(0);
					reachedDestination = true;	//Create if statement for boolean when calling method
			}
  		}
  		return reachedDestination;
  	}
 	
 	/**
 	 * Get the direction the climber is spinning. -1 = backwards,
 	 * 0 = not moving, 1 = forward
 	 * @return
 	 */
 	public static int getDirection(){
 		return direction;
 	}
 	
 	/**
 	 * Get the approximate height the robot is at while climbing
 	 * 
 	 * @return The height the climber is off the ground.
 	 */
 	public static double getClimbHeight(){
 		return distance;
 	}
 	
 	/**
 	 * Manually start the climber
 	 */
 	public static void manualStartClimber(){
 		manualStart = true;
 	}
} 
    
    
    
  
  
    
    
    
  
  
  
