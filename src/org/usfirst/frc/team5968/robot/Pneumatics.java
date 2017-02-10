package org.usfirst.frc.team5968.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Compressor;

//TODO: @allen458 fix some stuff and document the code. See the other classes for examples on documentation.

public class Pneumatics{
  
	private static DoubleSolenoid piston1 = new DoubleSolenoid(PortMap.portOf(PortMap.PCM.PISTON_1), PortMap.portOf(PortMap.PCM.PISTON_2));    //Eric maybe put this in PortMap?
	private static boolean isUp = false;
	
	public static void init(){

		Compressor compressor = new Compressor(PortMap.portOf(PortMap.CAN.PCM));   //Same with this?
		compressor.setClosedLoopControl(true);
    
	}
  
	public static boolean getIsUp() {
		return isUp;
	}
	
	public static void DoubleSolenoidTOGGLE(){
    	if(piston1.get() == DoubleSolenoid.Value.kForward){
    		piston1.set(DoubleSolenoid.Value.kReverse);
    	}
    	else{
    		piston1.set(DoubleSolenoid.Value.kForward);
    	}
    
	}
  
	public static void setSolenoidDown(){
		piston1.set(DoubleSolenoid.Value.kForward);
		if(piston1.get() == DoubleSolenoid.Value.kForward){
			System.out.println("lskdj");
		}
		else{
			System.out.println("slkdslkd");
		}
	}
	
}
    
 
