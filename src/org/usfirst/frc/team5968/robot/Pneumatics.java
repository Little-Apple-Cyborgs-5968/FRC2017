package org.usfirst.frc.team5968.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;

//TODO: @allen458 fix some stuff and document the code. See the other classes for examples on documentation.

public class Pneumatics{
  
	private static DoubleSolenoid piston1 = new DoubleSolenoid(PortMap.portOf(PortMap.PCM.BACK_PISTON_1), PortMap.portOf(PortMap.PCM.BACK_PISTON_2));    //Eric maybe put this in PortMap?
	private static DoubleSolenoid piston2 = new DoubleSolenoid(PortMap.portOf(PortMap.PCM.FRONT_PISTON_1), PortMap.portOf(PortMap.PCM.FRONT_PISTON_2));    //Eric maybe put this in PortMap?
	
	private static Compressor compressor;
	
	private static final double pressureSensorSupplyVoltage = 5;
	private static AnalogInput pressureSensor = new AnalogInput(0);
	
	public static void init(){

		compressor = new Compressor(PortMap.portOf(PortMap.CAN.PCM));   //Same with this?
		compressor.setClosedLoopControl(true);
		setSolenoidDown();
	}
  
	public static boolean getIsUp() {
		return false;//piston1.get() == DoubleSolenoid.Value.kReverse;
	}
	
	public static void DoubleSolenoidTOGGLE(){
    	if(piston1.get() == DoubleSolenoid.Value.kForward){
    		piston1.set(DoubleSolenoid.Value.kReverse);
			//Timer.delay(.4);
    		piston2.set(DoubleSolenoid.Value.kReverse);
    	}
    	else{
    		piston2.set(DoubleSolenoid.Value.kForward);
			//Timer.delay(.35);
    		piston1.set(DoubleSolenoid.Value.kForward);
    	}
    
	}
  
	public static void setSolenoidDown(){
		piston1.set(DoubleSolenoid.Value.kForward);
		//Timer.delay(.35);
		piston2.set(DoubleSolenoid.Value.kForward);
	}
	public static void stopCompressor(){
		compressor.stop();
	}
	
	/*
	 * Get whether the pressure is too low to use the pneumatics. We need the sensor before
	 * we actually do this.
	 */
	public static boolean isPressureLow(){
		
		boolean pressureTooLow = true;
		double outputVoltage = pressureSensor.getVoltage();
		double pressure = 250.0 * (outputVoltage/pressureSensorSupplyVoltage) - 25.0; 
		
		if(pressure >= 45){
			pressureTooLow = false;
		}else{
			pressureTooLow = true;
		}
		
		return pressureTooLow;
	}
}
    
 
