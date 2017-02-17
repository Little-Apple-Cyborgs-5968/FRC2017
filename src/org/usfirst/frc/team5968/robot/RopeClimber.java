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
	private static final double maxCurrent = 70;    //Tune
	private static final double maxSpeed = .9;
	private static double motorSpeed = 0;
	private static double distance = ((Math.sin(startAngle * (Math.PI / 180))) * robotLength);
	
	private static boolean isSetToPoint1 = false;
	private static boolean isAccelerated = false;
	
	private static int direction = 0;
	
	private static boolean manualStart = false;
	
	private static double stopSpeed = .3;
	
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
		
		if(motorSpeed < maxSpeed){
			
			motorSpeed = motorSpeed + .01;
			setSpeed(motorSpeed);
			Timer.delay(.05);
			isAccelerated = false;
		}
		if(motorSpeed >= maxSpeed){
			isAccelerated = true;
		}
	}
		
	public static double getCurrent(){
		
		return Math.abs(pdp.getCurrent(14)); //right motor
	}	
	
 	public static boolean motorClimb(){	//Prepares to climb
		if(Timer.getMatchTime() > 30 || Timer.getMatchTime() == -1){
			return false;
		}
		boolean reachedDestination = false;
 
		if(!isSetToPoint1){
			
			setSpeed(.4);
			motorSpeed = .4;
			isSetToPoint1 = true;
		}
		
  		double verticalAngle = NavXMXP.getRoll();
  		if(Math.abs(verticalAngle) > startAngle){
  			System.out.println(getCurrent());
			DriveBase.setRawFraction(0, 0);
			climberEncoder.reset();
			if(!isAccelerated){
				
				climbingAcceleration();
			}
			
			distance += climberEncoder.getDistance();
  			if(distance < 48){
  				
  				reachedDestination = false;
        	}
  			if(distance >= 48){
      
  				setSpeed(maxSpeed - .1);
				reachedDestination = false;
  				
        	} 
			if(getCurrent() >= maxCurrent){
					setSpeed(stopSpeed);
					reachedDestination = true;	//Create if statement for boolean when calling method
					System.out.println("it stopped");
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
 	
 	public static void eStopClimber(){
 		setSpeed(stopSpeed);
 	}
} 
    
    
    
  
  
    
    
    
  
  
  
