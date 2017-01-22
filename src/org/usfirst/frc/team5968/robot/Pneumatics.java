package org.usfirst.frc.team5968.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Compressor;

//TODO: @allen458 fix some stuff and document the code. See the other classes for examples on documentation.

public class Pneumatics{
  
	//there are 2 pistons. They should be controlled together, though.
	private static DoubleSolenoid piston = new DoubleSolenoid(PortMap.portOf(PortMap.PCM.FRONT_PISTON_1), PortMap.portOf(PortMap.PCM.FRONT_PISTON_2));    //Eric maybe put this in PortMap?
    
	public static void init(){

		Compressor compressor = new Compressor(PortMap.portOf(PortMap.CAN.PCM));   //Same with this?
		compressor.setClosedLoopControl(true);
    
	}
  
	//I'm not really sure what a solenoid being "off" is. I don't think we need this method.
	public static void DoubleSolenoidOFF(){
    
		piston.set(DoubleSolenoid.Value.kOff);
    
	}
  
	//It would be better code design to have just one method that toggles the solenoid. See the note in HumanInterface
	public static void DoubleSolenoidUP(){
    
		piston.set(DoubleSolenoid.Value.kForward);
    
	}
  
	public static void DoubleSolenoidDOWN(){
    
		piston.set(DoubleSolenoid.Value.kReverse);
    
	}  
}  
    
 
