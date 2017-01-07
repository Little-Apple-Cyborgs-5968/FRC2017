package org.usfirst.frc.team5968.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/*
 * Used for sending data to the dashboard. Just add the data to the NetworkTable
 * and the dashboard will have it.
 */
public class DashboardConnection {
	
	private static boolean initialized = false;
	private static NetworkTable table;
	private static String[] autoModes = {"Dummy1", "Dummy2", "Dummy3"};
	
	public static void init(){
		if(!initialized){
			table = NetworkTable.getTable("SmartDashboard");
			initialized = true;
		}
	}
	
	public static void addModes() {
		table.putStringArray("options", autoModes);
	}
	
	//Update the diagnostics on the dashboard
	public static void updateDashboardValues(){
		if(!initialized){
			init();
		}
		table.putNumber("timeRemaining", Timer.getMatchTime());
	}
}
