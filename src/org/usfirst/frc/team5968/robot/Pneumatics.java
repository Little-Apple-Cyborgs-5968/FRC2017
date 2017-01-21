
public class Pneumatics{
  
  DoubleSolenoid piston = new DoubleSolenoid(1, 2);    //Eric maybe put this in PortMap?
    
  public static void Compressor(){

    Compressor compressor = new Compressor(0);   //Same with this?
    compressor.setClosedLoopControl(true);
    
  }
  
  public static void DoubleSolenoidOFF(){
    
    piston.set(DoubleSolenoid.Value.kOff);
    
  }
  
  public static void DoubleSolenoidUP(){
    
    piston.set(DoubleSolenoid.Value.kForward);
    
  }
  
  public static void DoubleSolenoidDOWN(){
    
    pistion.set(DoubleSolenoid.Value.kReverse);
    
  }  
    
    
 
