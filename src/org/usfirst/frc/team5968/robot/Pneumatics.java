package org.usfirst.frc.team5968.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Compressor;

//TODO: @allen458 fix some stuff and document the code. See the other classes for examples on documentation.

public class Pneumatics{
  
	private static DoubleSolenoid piston1 = new DoubleSolenoid(PortMap.portOf(PortMap.PCM.BACK_PISTON_1), PortMap.portOf(PortMap.PCM.BACK_PISTON_2));    //Eric maybe put this in PortMap?
    private static DoubleSolenoid piston2 = new DoubleSolenoid(PortMap.portOf(PortMap.PCM.FRONT_PISTON_1), PortMap.portOf(PortMap.PCM.FRONT_PISTON_2));
	private static boolean isUp = false;
	
	public static void init(){

		Compressor compressor = new Compressor(PortMap.portOf(PortMap.CAN.PCM));   //Same with this?
		compressor.setClosedLoopControl(true);
    
	}
  
	public static boolean getIsUp() {
		return isUp;
	}
	//It would be better code design to have just one method that toggles the solenoid. See the note in HumanInterface
	public static void DoubleSolenoidTOGGLE(){
    		
		if(isUp){
			
			piston2.set(DoubleSolenoid.Value.kForward);
			Timer.delay(.35);
			piston1.set(DoubleSolenoid.Value.kForward);
			isUp = false;
			
		}
		else{
			
			piston1.set(DoubleSolenoid.Value.kReverse);
			Timer.delay(.425);
			piston2.set(DoubleSolenoid.Value.kReverse);
			isUp = true;
			
		}
    
	}
  
	
}