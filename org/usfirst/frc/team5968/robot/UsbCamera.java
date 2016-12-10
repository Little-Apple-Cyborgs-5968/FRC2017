package org.usfirst.frc.team5968.robot;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.IMAQdxCameraControlMode;
import com.ni.vision.NIVision.Image;

public class UsbCamera {
	
	private static Image image;
	private static int session;
	private static boolean initialized = false;
	
	public static void init(){
		//image can't be null when you pass it to NIVision.IMAQdxGrab()
		image = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
		session = NIVision.IMAQdxOpenCamera("cam0", IMAQdxCameraControlMode.CameraControlModeController);
		NIVision.IMAQdxConfigureGrab(session);
		NIVision.IMAQdxStartAcquisition(session);
		initialized = true;
	}
	
	public static Image getImage(){
		if(!initialized){
			init();
		}
		NIVision.IMAQdxGrab(session, image, 1);
		return image;
	}
}
