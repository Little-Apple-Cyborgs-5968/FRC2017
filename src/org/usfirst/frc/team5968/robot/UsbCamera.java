package org.usfirst.frc.team5968.robot;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCameraInfo;
import edu.wpi.first.wpilibj.CameraServer;

import org.opencv.core.Mat;

public class UsbCamera {
	
	private static CvSink cvSink;
	
	public static void init(){
		
		edu.wpi.cscore.UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(640, 480);
		cvSink = CameraServer.getInstance().getVideo();
        
        camera.setBrightness(0);
        camera.setExposureManual(15);
	}
	
	public static Mat getImage(){
		Mat image = new Mat();
		cvSink.grabFrame(image);
		return image;
	}
}
