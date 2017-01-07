package org.usfirst.frc.team5968.robot;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.UsbCameraInfo;
import org.opencv.core.Mat;

public class UsbCamera {
	
	/*
	 * TODO: check if this stuff is actually right. From looking at the source code, I think it is,
	 * but no guarantees. Especially make sure that it doesn't attempt to start a server.
	 */
	private static CvSink cv;
	
	public static void init(){
		 UsbCameraInfo[] info = edu.wpi.cscore.UsbCamera.enumerateUsbCameras();
		 if(info.length > 0){
			 edu.wpi.cscore.UsbCamera camera = new edu.wpi.cscore.UsbCamera(info[0].name, info[0].dev);
			 cv = new CvSink("opencv_" + info[0].name);
		 }
	}
	
	public static Mat getImage(){
		Mat image = new Mat();
		cv.grabFrame(image);
		return image;
	}
}
