package org.usfirst.frc.team5968.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Compressor;

//TODO: @allen458 fix some stuff and document the code. See the other classes for examples on documentation.

public class Pneumatics{
  
	//there are 2 pistons. They should be controlled together, though.
	private static DoubleSolenoid piston1 = new DoubleSolenoid(PortMap.portOf(PortMap.PCM.BACK_PISTON_1), PortMap.portOf(PortMap.PCM.FRONT_PISTON_2));    //Eric maybe put this in PortMap?
    	private static DoubleSolenoid piston2 = new DoubleSolenoid(PortMap.portOf(PortMap.PCM.FRONT_PISTON_1), PortMap.portOf(PortMap.PCM.BACK_PISTON_2));
	private static boolean IsUP = false;
	
	public static void init(){

		Compressor compressor = new Compressor(PortMap.portOf(PortMap.CAN.PCM));   //Same with this?
		compressor.setClosedLoopControl(true);
    
	}
  
	public boolean getIsUP() {
		return IsUP;
	}
	//It would be better code design to have just one method that toggles the solenoid. See the note in HumanInterface
	public static void DoubleSolenoidTOGGLE(){
    		
		if(IsUP){
			
			piston1.set(DoubleSolenoid.Value.kForward);
			piston2.set(DoubleSolenoid.Value.kForward);
			IsUP = false;
			
		}
		if(!IsUP){
			
			piston1.set(DoubleSolenoid.Value.kReverse);
			piston2.set(DoubleSolenoid.Value.kReverse);
			IsUP = true;
			
		}
    
	}
  
	
}
    
 
