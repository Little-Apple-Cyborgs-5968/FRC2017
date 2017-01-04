package org.usfirst.frc.team5968.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class AutoModes {
	static NetworkTable table;
	
	public static void addModes() {
		table = NetworkTable.getTable("/SmartDashboard/autonomous/options");
		table.putString("Test 1", "Test 2");
	}
}
