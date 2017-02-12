package org.usfirst.frc.team5968.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Compressor;

//TODO: @allen458 fix some stuff and document the code. See the other classes for examples on documentation.

public class Pneumatics{
  
	private static DoubleSolenoid piston1 = new DoubleSolenoid(PortMap.portOf(PortMap.PCM.BACK_PISTON_1), PortMap.portOf(PortMap.PCM.BACK_PISTON_2));    //Eric maybe put this in PortMap?
	private static DoubleSolenoid piston2 = new DoubleSolenoid(PortMap.portOf(PortMap.PCM.FRONT_PISTON_1), PortMap.portOf(PortMap.PCM.FRONT_PISTON_2));    //Eric maybe put this in PortMap?
	private static boolean isUp = false;
	
	private static Compressor compressor;
	
	public static void init(){

		compressor = new Compressor(PortMap.portOf(PortMap.CAN.PCM));   //Same with this?
		compressor.setClosedLoopControl(true);
	}
  
	public static boolean getIsUp() {
		return isUp;
	}
	
	public static void DoubleSolenoidTOGGLE(){
    	if(piston1.get() == DoubleSolenoid.Value.kForward){
    		piston1.set(DoubleSolenoid.Value.kReverse);
    		piston2.set(DoubleSolenoid.Value.kReverse);
    	}
    	else{
    		piston2.set(DoubleSolenoid.Value.kForward);
    		piston1.set(DoubleSolenoid.Value.kForward);
    	}
    
	}
  
	public static void setSolenoidDown(){
		piston1.set(DoubleSolenoid.Value.kForward);
		piston2.set(DoubleSolenoid.Value.kForward);
		if(piston1.get() == DoubleSolenoid.Value.kForward){
			System.out.println("lskdj");
		}
		else{
			System.out.println("slkdslkd");
		}
	}
	public static void stopCompressor(){
		compressor.stop();
	}
}
    
 
