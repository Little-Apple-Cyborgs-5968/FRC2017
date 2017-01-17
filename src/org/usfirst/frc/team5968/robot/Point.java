package org.usfirst.frc.team5968.robot;

public class Point {
	private double x;
	
	private double y;
	
	private boolean isHopper;
	
	private boolean isBoiler;
	
	private boolean isChute;
	
	public Point(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
}
