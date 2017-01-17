package org.usfirst.frc.team5968.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/*
 * Used for sending data to the dashboard. Just add the data to the NetworkTable
 * and the dashboard will have it.
 */
public class DashboardConnection {
	
	//Whether the connection has been initialized
	private static boolean initialized = false;
	
	//Table storing the data
	private static NetworkTable table;
	
	//Valid automodes for the user to select
	private static String[] autoModes = {"Dummy1", "Dummy2", "Dummy3"};
	
	//Initialize the table
	public static void init(){
		if(!initialized){
			table = NetworkTable.getTable("SmartDashboard");
			initialized = true;
		}
	}
	
	//Add auto modes to the network table
	public static void addModes() {
		if(!initialized){
			init();
		}
		table.putStringArray("options", autoModes);
		table.putStringArray("options", autoModes);
	}
	
	//Update the diagnostics on the dashboard
	public static void updateDashboardValues(){
		if(!initialized){
			init();
		}
		table.putNumber("timeRemaining", Timer.getMatchTime());
	}
	
	//Get the current destination point from the dashboard
	public static Point getDestinationPoint(){
		if(!initialized){
			init();
		}
		return new Point(table.getNumber("targetX", PositionTracker.getCurrentPoint().getX()), table.getNumber("targetY", PositionTracker.getCurrentPoint().getY()));
	}
	
	//Send the current location to the dashboard
	public static void putLocation(Point loc, double angle){
		if(!initialized){
			init();
		}
		
		table.putNumber("currentX", loc.getX());
		table.putNumber("currentY", loc.getY());
		table.putNumber("currentAngle", angle);
	}
}
