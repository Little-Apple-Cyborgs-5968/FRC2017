public class RopeClimber{

  private static CANTalon leftMotor = new CANTalon(PortMap.portof(LEFT_MOTOR));   //Eric you're going to set the ports
  private static CANTalon rightMotor = new CANTalon(PortMap.portof(RIGHT_MOTOR));   //Same with this
  private Encoder climberEncoder = new Encoder(PortMap.portof(ENCODER));        //Same with this  
  
  private double startAngle = 5;                            //Inches
  private double robotLength = 33;                          //Inches  
  
  public static void init(){
  
    leftMotor.changeControlMode(CANTalon.TalonControlMode.Follower); 
    leftMotor.set(PortMap.portof(RIGHT_MOTOR);
  
    climberEncoder.setDistancePerPulse(8/2048);                                   //Inches
    private double Distance = 0;
  
  }
  
  public static boolean climberMotorStart(){                                  //Prepares to climb
  
    DriveBase.setRawFraction(0, 0);
    rightMotor.set(.8);
    boolean reachedDestination = false;
    
    double verticalAngle = NavXMXP.getPitch();
    
    if(verticalAngle > 5){
    
      if(Distance < 58){
      
        Distance = climberEncoder.getDistance + ((Math.tan(startAngle * (Math.PI / 180))) * robotLength);
        reachedDestination = false
        
      }if(Distance >= 58){
      
        rightMotor.set(0);                                     //I need to break it
        reachedDestination = true                              //Create if statement for boolean when calling method
        
      }  
    
    }
  }
} 
    
    
    
  
  
    
    
    
  
  
  
