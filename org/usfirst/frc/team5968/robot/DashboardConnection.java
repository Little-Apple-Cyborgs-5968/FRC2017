package org.usfirst.frc.team5968.robot;

import com.ni.vision.NIVision.Image;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/*
 * Used for sending data to the dashboard. Just add the data to the NetworkTable
 * and the dashboard will have it.
 */
public class DashboardConnection {
	
	private static boolean initialized = false;
	private static int x = 0;
	private static NetworkTable table;
	
	public static void init(){
		if(!initialized){
			table = NetworkTable.getTable("SmartDashboard");
			initialized = true;
		}
	}
	
	public static void startTimer(){
		table.putBoolean("timeRunning", true);
	}
	
	//Update the diagnostics on the dashboard
	public static void updateDashboardValues(){
		if(!initialized){
			init();
		}
		
		x++;
	}
	
	//Update camera view on the dashboard with a processed image
	public static void updateCameraView(Image i){
		/*if(!initialized){
			init();
		}
		
		stream.setImage(i);*/
	}
	
	//Update camera view on the dashboard with an unprocessed image
	//NOTE: if this is too slow and we aren't sending a processed image to the
	//dashboard anyway, we can use stream.startAutomaticCapture(String camera)
	public static void updateCameraView(){
		/*if(!initialized){
			init();
		}
		
		stream.setImage(UsbCamera.getImage());*/
	}
}
