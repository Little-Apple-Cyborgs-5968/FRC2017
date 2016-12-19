package org.usfirst.frc.team5968.robot;

import com.ni.vision.NIVision.Image;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/*
 * Used for sending data to the dashboard. Just add the data to the NetworkTable
 * and the dashboard will have it.
 */
public class DashboardConnection {
	
	private static NetworkTable table;
	private static CameraServer stream;
	private static boolean initialized = false;
	
	public static void init(){
		table = NetworkTable.getTable("datatable");
		stream = CameraServer.getInstance();
		initialized = true;
	}
	
	//Update the diagnostics on the dashboard
	public static void updateDashboardValues(){
		table.putBoolean("Front left hot", DriveBase.isMotorTooHot(DriveBase.Motor.LEFT_FRONT));
		table.putBoolean("Front back hot", DriveBase.isMotorTooHot(DriveBase.Motor.LEFT_BACK));
		table.putBoolean("Front right hot", DriveBase.isMotorTooHot(DriveBase.Motor.RIGHT_FRONT));
		table.putBoolean("Back right hot", DriveBase.isMotorTooHot(DriveBase.Motor.RIGHT_BACK));
	}
	
	//Update camera view on the dashboard with a processed image
	public static void updateCameraView(Image i){
		if(!initialized){
			init();
		}
		
		stream.setImage(i);
	}
	
	//Update camera view on the dashboard with an unprocessed image
	//NOTE: if this is too slow and we aren't sending a processed image to the
	//dashboard anyway, we can use stream.startAutomaticCapture(String camera)
	public static void updateCameraView(){
		if(!initialized){
			init();
		}
		
		stream.setImage(UsbCamera.getImage());
	}
}
