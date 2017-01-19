package org.usfirst.frc.team5968.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 * Used for sending data to the dashboard. Just add the data to the NetworkTable
 * and the dashboard will have it.
 * 
 * @author BeijingStrongbow
 */
public class DashboardConnection {
	
	/**
	 * Whether the connection has been initialized
	 */
	private static boolean initialized = false;
	
	/**
	 * Table storing the data
	 */
	private static NetworkTable table;
	
	/**
	 * Valid automodes for the user to select. Obviously it's still WIP
	 */
	private static String[] autoModes = {"Dummy1", "Dummy2", "Dummy3"};
	
	/**
	 * Initializes the network table
	 */
	public static void init(){
		if(!initialized){
			table = NetworkTable.getTable("SmartDashboard");
			initialized = true;
		}
	}
	
	/*(
	 * Add the valid automodes to the network table
	 */
	public static void addModes() {
		if(!initialized){
			init();
		}
		table.putStringArray("options", autoModes);
		table.putStringArray("options", autoModes);
	}
	
	/**
	 * Update the diagnostics on the dashboard. 
	 */
	public static void updateDashboardValues(){
		if(!initialized){
			init();
		}
		table.putNumber("timeRemaining", Timer.getMatchTime());
	}
	
	/**
	 * Get the current destination point from the dashboard, if the user
	 * is using the touchscreen control.
	 * 
	 * @return The current point on the field selected by the user
	 */
	public static Point getDestinationPoint(){
		if(!initialized){
			init();
		}
		return new Point(table.getNumber("targetX", PositionTracker.getCurrentPoint().getX()), table.getNumber("targetY", PositionTracker.getCurrentPoint().getY()));
	}
	
	/**
	 * Send the robot's current location to the dashboard, so it can be drawn in on the
	 * field diagram.
	 * 
	 * @param loc The robot's current location on the field
	 * @param angle The robot's current angle, from 0 to 2 pi radians
	 */
	public static void putLocation(Point loc, double angle){
		if(!initialized){
			init();
		}
		
		table.putNumber("currentX", loc.getX());
		table.putNumber("currentY", loc.getY());
		table.putNumber("currentAngle", angle);
	}
}
