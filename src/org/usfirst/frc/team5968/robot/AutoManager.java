package org.usfirst.frc.team5968.robot;

import org.usfirst.frc.team5968.robot.Point.Setpoint;
import org.usfirst.frc.team5968.robot.Robot.StartPoint;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class AutoManager{

    /**
     * Represents the current place in autonomous. Not all are used on all modes.
     */
    private enum AutoProgress {
    	STARTING,
    	DRIVE1_DONE,
    	TURN1_DONE,
    	DRIVE2_DONE,
    	DRIVE3_DONE,
    	TURN2_DONE,
    	DRIVE4_DONE,
    	CAMERA_TARGETING_DONE,
    	TURN3_DONE,
    	DRIVE5_DONE,
    	TURN4_DONE,
    	FINISHED;
    }
    
    
    /**
     * All the autonomous options
     */
    public enum AutoMode {
    	HOPPER_BOILER,
    	HOPPER,
    	GEAR,
    	CROSS,
    	BE_USELESS; //<- do nothing, if you didn't understand
    }
    
    /**
     * The distance away from the front of the boiler to stop to use camera vision. Inches.
     */
    private static final double STOP_DISTANCE_FROM_BOILER = 48;
    
    /**
     * The distance from a wall we need to be to turn without hitting the wall. Inches.
     */
    private static final double SAFE_TURN_DISTANCE = 24;
    
    /**
     * The distance to the goal as measured by the camera, if camera targeting has been done.
     */
    private static double distanceToGoal;
    
    /**
     * The angle to the goal as measured by the camera, if camera targeting has been done.
     */
    private static double angleToGoal;
    
    /**
     * The distance to drive after aiming with the camera
     */
    private static double distanceToDrive;
    
    /**
     * The angle to turn TO after aiming with the camera
     */
    private static double angleToTurn;
    
    /**
     * Our progress in the auto routine
     */
    private static AutoProgress autoProgress = AutoProgress.STARTING;
    
    private static boolean hopperDriveComplete = false;
    
    public static double getSafeTurnDistance(){
    	return SAFE_TURN_DISTANCE;
    }
    
    /**
     * Run autonomous
     * 
     * @param startPoint The point on the field where the robot started
     * @param mode The mode to run
     * @param alliance Which alliance we're on right now
     * @param hopper The hopper to drive to if HOPPER is the selected auto mode
     * @return Whether autonomous is finished
     * @throws UnsupportedOperationException If you try to do something it doesn't know how to do, or that is illegal. It's recommended that you call again with AutoMode.CROSS.
     */
    public static void doAuto(StartPoint startPoint, AutoMode mode, Alliance alliance, int hopper) throws UnsupportedOperationException{
    	while(autoProgress != AutoProgress.FINISHED){
    		if(Thread.interrupted()){
    			return;
    		}
    		switch(mode){
	    		case GEAR:
	    			autoProgress = gearAuto(autoProgress, alliance, startPoint);
	    			break;
	    		case HOPPER:
	    			if((alliance == Alliance.Red && hopper == 3) || (alliance == Alliance.Blue && hopper == 5)){
	    				DriverStation.reportError("We'll get a penalty if we go to that hopper! Imma do nothing instead.", false);
	    				throw new UnsupportedOperationException();
	    			}
	    			else if(((hopper == 1 || hopper == 2) && startPoint != StartPoint.RETRIEVAL_ZONE) ||
	    					((hopper == 3 || hopper == 4 || hopper == 5) && startPoint != StartPoint.KEY)){
	    				DriverStation.reportError("I don't know how to go to that hopper from here! Imma do nothing instead", false);
	    				throw new UnsupportedOperationException();
	    			}
	    			else{
	    				autoProgress = hopperAuto(autoProgress, alliance, hopper, startPoint);
	    			}
	    			break;
	    		case CROSS:
	    			autoProgress = crossAuto(autoProgress);
	    			break;
	    		case HOPPER_BOILER:
	    			if(startPoint != StartPoint.KEY){
	    				DriverStation.reportError("I can only do hopper + boiler if I start in the KEY!!", false);
	    				throw new UnsupportedOperationException();
	    			}
	    			else{
	    				if(!hopperDriveComplete){
	    					autoProgress = hopperAuto(autoProgress, alliance, hopper, startPoint);
	    					if(autoProgress == AutoProgress.FINISHED){
	    						autoProgress = AutoProgress.STARTING;
	    						hopperDriveComplete = true;
	    					}
	    				}
	    				else{
	    					autoProgress = hopperBoilerAuto(autoProgress, alliance);
	    				}
	    			}
	    			break;
    		}
    		if(autoProgress == AutoProgress.FINISHED){
    			return;
    		}
    	}
    }
    
    /**
     * Place a gear on a peg in auto
     * 
     * @param progress The current progress in autonomous
     * @return The progress after calling the method
     */
	private static AutoProgress gearAuto(AutoProgress progress, Alliance alliance, StartPoint startPoint){
		if(startPoint == StartPoint.MIDLINE){
			
			if(progress != AutoProgress.FINISHED && DriveBase.driveDistance(-114.8, .3)){
				progress = AutoProgress.FINISHED;
			}
			
		}
		else if(startPoint == StartPoint.KEY){
			
			double firstDriveDistance = Math.tan(5 * Math.PI / 6) * (223.06 + 0.5 * Robot.getRobotWidth() - 162) + 149.5;
			firstDriveDistance -= .5 * Robot.getRobotLength();
			//firstDriveDistance += 9;
			double secondDriveDistance = Math.sqrt(Math.pow(223.06 + 0.5 * Robot.getRobotWidth() - 192.53, 2) + Math.pow(firstDriveDistance + .5 * Robot.getRobotLength() - 131.88, 2));
			secondDriveDistance -= .5 * Robot.getRobotLength();
			secondDriveDistance += 6;
			switch(progress){
				case STARTING:
					if(DriveBase.driveDistance(-1 * firstDriveDistance, .3)){
						progress = AutoProgress.DRIVE1_DONE;
					}
					break;
				case DRIVE1_DONE:
					if(alliance == Alliance.Red){
						if(DriveBase.driveRotation(298)){
							progress = AutoProgress.TURN1_DONE;
						}
					}
					else{
						if(DriveBase.driveRotation(61)){
							progress = AutoProgress.TURN1_DONE;
						}
					}
					break;
				case TURN1_DONE:
					if(DriveBase.driveDistance(-1 * secondDriveDistance, .3)){
						progress = AutoProgress.FINISHED;
					}
					break;
				default:
					System.out.println("LOLOL I have no idea what to do");
					break;
			}
		}
		else if(startPoint == StartPoint.RETRIEVAL_ZONE){
			double firstDriveDistance = Math.tan(Math.PI / 6) * (118.43 + .5 * Robot.getRobotWidth() - 162) + 149.5;
			firstDriveDistance -= .5 * Robot.getRobotLength();
			double secondDriveDistance = Math.sqrt(Math.pow(118.43 + .5 * Robot.getRobotWidth() - 131.47, 2) + Math.pow(firstDriveDistance + .5 * Robot.getRobotLength() - 131.88, 2));
			secondDriveDistance -= .5 * Robot.getRobotLength();
			secondDriveDistance -= 2;
			switch(progress){
				case STARTING:
					if(DriveBase.driveDistance(-1 * firstDriveDistance, .3)){
						progress = AutoProgress.DRIVE1_DONE;
					}
					break;
				case DRIVE1_DONE:
					if(alliance == Alliance.Red){
						if(DriveBase.driveRotation(60)){
							progress = AutoProgress.TURN1_DONE;
						}
					}
					else{
						if(DriveBase.driveRotation(300)){
							progress = AutoProgress.TURN1_DONE;
						}
					}
					break;
				case TURN1_DONE:
					if(DriveBase.driveDistance(secondDriveDistance, .3)){
						progress = AutoProgress.FINISHED;
					}
					break;
				default:
					System.out.println("LOLOL I have no idea what to do");
					break;
			}
		}
		return progress;
	}
	
	/**
     * Get fuel from a hopper in auto
     * 
     * @param progress The current progress in autonomous
     * @param alliance The alliance we're on
     * @param hopper The hopper to drive to
     * @param StartPoint Where the robot started
     * @return The progress after calling the method
     */
	private static AutoProgress hopperAuto(AutoProgress progress, Alliance alliance, int hopper, StartPoint startPoint){
		double firstDriveDistance = 0;

		if(alliance == Alliance.Red){
			switch(hopper){
    			case 1:
    				firstDriveDistance = Point.getCoordinates(Setpoint.HOPPER1).getY(); 
    				break;
    			case 2:
    				firstDriveDistance = Point.getCoordinates(Setpoint.HOPPER2).getY();
    				break;
    			case 3:
    				firstDriveDistance = Point.getCoordinates(Setpoint.HOPPER3).getY();
    				break;
    			case 4:
    				firstDriveDistance = Point.getCoordinates(Setpoint.HOPPER4).getY();
    				break;
    			case 5:
    				firstDriveDistance = Point.getCoordinates(Setpoint.HOPPER5).getY();
    				break;
    		}
		}
		else{
			switch(hopper){
    			case 1:
    				firstDriveDistance = 652 - Point.getCoordinates(Setpoint.HOPPER1).getY();
    				break;
    			case 2:
    				firstDriveDistance = 652 - Point.getCoordinates(Setpoint.HOPPER2).getY();
    				break;
    			case 3:
    				firstDriveDistance = 652 - Point.getCoordinates(Setpoint.HOPPER3).getY();
    				break;
    			case 4:
    				firstDriveDistance = 652 - Point.getCoordinates(Setpoint.HOPPER4).getY();
    				break;
    			case 5:
    				firstDriveDistance = 652 - Point.getCoordinates(Setpoint.HOPPER5).getY();
    				break;
    		}
		}
		
		firstDriveDistance -= 28.25; //28.25 = distance from the center of the hopper to the center of one side
		
		if(startPoint == StartPoint.KEY || startPoint == StartPoint.RETRIEVAL_ZONE){
			switch(progress){
				case STARTING:
					//Pneumatics.setSolenoidDown();
					if(DriveBase.driveDistance(firstDriveDistance, .2)){
						progress = AutoProgress.DRIVE1_DONE;
					}
					break;
				case DRIVE1_DONE:
					if((alliance == Alliance.Red && (hopper == 1 || hopper == 2)) ||
							(alliance == Alliance.Blue && (hopper == 3 || hopper == 4))){
						if(DriveBase.driveRotation(-90)){
							progress = AutoProgress.TURN1_DONE;
						}
					}
					else{
						if(DriveBase.driveRotation(90)){
							progress = AutoProgress.TURN1_DONE;
						}
					}
					break;
				case TURN1_DONE:
					if(DriveBase.driveDistance(Robot.getDistanceFromWall(startPoint) - 0.5 * Robot.getRobotLength(), 0.2)){
						progress = AutoProgress.FINISHED;
					}
					break;
				default:
					System.out.println("LOLOL I have no idea what to do");
					break;
			}
		}
		return progress;
	}
	
	private static double currentAngle;
	
	/**
     * Get fuel from a hopper and dump it in the boiler in auto
     * 
     * @param progress The current progress in autonomous
     * @param alliance The alliance we're on.
     * @return The progress after calling the method
     */
	private static AutoProgress hopperBoilerAuto(AutoProgress progress, Alliance alliance){
		//the change in x and y in driving from the hopper to a point STOP_DISTANCE_FROM_BOILER away from the boiler. This is
		//after backing up DRIVE_BACK_DISTANCE inches.
		double dx = 637.5 - STOP_DISTANCE_FROM_BOILER * Math.cos(.76271) - (652 - SAFE_TURN_DISTANCE);
		double dy = 15.2 + STOP_DISTANCE_FROM_BOILER * Math.sin(.76271) - (Point.getCoordinates(Setpoint.HOPPER5).getY() - .5 * Robot.getRobotLength());
		double turnAngle;
		switch(progress){
			case STARTING:
				int hopper;
				if(alliance == Alliance.Red){
					hopper = 5;
				}
				else{
					hopper = 3;
				}
				Timer.delay(1.5);
				progress = AutoProgress.DRIVE2_DONE;
				break;
			//DRIVE1 and TURN1 are handled in the call to hopperAuto
			case DRIVE2_DONE:
				//System.out.println("DRIVE2_DONE");
				if(DriveBase.driveDistance(-1 * SAFE_TURN_DISTANCE, 0.2)){ 
					progress = AutoProgress.DRIVE3_DONE;
					currentAngle = NavXMXP.getYaw();
				}
				break;
			case DRIVE3_DONE:
				//System.out.println("DRIVE3_DONE");
				if(DriveBase.driveRotation(147)){
					progress = AutoProgress.TURN2_DONE;
				}
				break;
			case TURN2_DONE:
				if(DriveBase.driveDistanceWithVision(91.75, 0.2)){
					progress = AutoProgress.DRIVE4_DONE;
				}
				break;
			case DRIVE4_DONE:
				System.out.println("DRIVE4_DONE");
				boolean driven = false;
				if(alliance == Alliance.Red){
					driven = DriveBase.driveRightDistance(Robot.getRobotWidth() * 0.808087444); //46.3 degrees (angle of the boiler with the wall)
				}
				else{
					driven = DriveBase.driveLeftDistance(Robot.getRobotWidth() * 0.808087444);
				}	
														 
				if(driven){
					//Pneumatics.DoubleSolenoidTOGGLE();
					progress = AutoProgress.FINISHED;
				}	
				break;
			default:
				System.out.println("LOLOL I have no idea what to do");
				break;
		}
		
		return progress;
	}
	
	/**
     * Cross the baseline in auto
     * 
     * @param progress The current progress in autonomous
     * @return The progress after calling the method
     */
	private static AutoProgress crossAuto(AutoProgress progress){
		if(progress != AutoProgress.FINISHED && DriveBase.driveDistance(96, 0.3)){
			progress = AutoProgress.FINISHED;
		}
		return progress;
	}
}
