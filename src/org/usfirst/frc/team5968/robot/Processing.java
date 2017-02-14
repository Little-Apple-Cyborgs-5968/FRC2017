package org.usfirst.frc.team5968.robot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import edu.wpi.first.wpilibj.XboxController;

public class Processing {
	private Mat hslThresholdOutput = new Mat();
	private ArrayList<MatOfPoint> findContoursOutput = new ArrayList<MatOfPoint>();
	private ArrayList<MatOfPoint> filterContoursOutput = new ArrayList<MatOfPoint>();
	private int topTape; // Will be the index in the filterContoursOutput ArrayList of the 4 in piece of tape
	private int bottomTape; // Will be the index in the filterContoursOutput ArrayList of the 2 in piece of tape
	public double goals[][] = new double[1100][11];
	public double gears[][] = new double[1100][11];
	Mat imgWithRect;
	
	Mat hslThresholdInput;
	double[] hslThresholdHue = {49, 98};
	double[] hslThresholdSaturation = {175, 255.0};
	double[] hslThresholdLuminance = {8, 255.0};
	
	ArrayList<MatOfPoint> filterContoursContours;
	
	double filterContoursMinArea = 100;
	double filterContoursMinPerimeter = 0.0;
	double filterContoursMinWidth = 0.0;
	double filterContoursMaxWidth = 1000.0;
	double filterContoursMinHeight = 0.0;
	double filterContoursMaxHeight = 1000.0;
	double[] filterContoursSolidity = {0, 100};
	double filterContoursMaxVertices = 1000000.0;
	double filterContoursMinVertices = 0.0;
	double filterContoursMinRatio = 0.0;
	double filterContoursMaxRatio = 1000.0;
	
	Mat findContoursInput;
	boolean findContoursExternalOnly = false;
	
	Rect topRect;
	Rect bottomRect;
	
	double tY;
	double tX;
	double bY;
	double bX;
	double tHeight;
	double bHeight;
	double tWidth;
	double bWidth;
	double tArea;
	double bArea;
	
	double lY;
	double lX;
	double rY;
	double rX;
	double lHeight;
	double rHeight;
	double lWidth;
	double rWidth;
	double lArea;
	double rArea;
	
	double height;
	double width;
	double distance; // ft d
	double angleToCorrect; // goal to the left of center is positive
	
	int isGoalScore = 0;
	
	double distanceFromGoal; // H
	double distanceToOrgin; // t
	double groundDistance;
	double realDistance; // D
	double screenWidth;
	double screenHeight;
	double maybeGroundDistance;
	double yOffset;

	int goalNum;
	int realGoalNum;
	double maxValue;
	
	int gearNum;
	int realGearNum;
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	/**
	 * This is the primary method that runs the entire pipeline and updates the outputs.
	 * @param source0 is the image
	 * @param goalOrNah is true if user wants to find Goal and false if user wants to find gear
	 */
	public boolean process(Mat source0, boolean goalOrNah) {
		
		hslThresholdInput = source0;
		
		hslThreshold(hslThresholdInput, hslThresholdHue, hslThresholdSaturation, hslThresholdLuminance, hslThresholdOutput);
		findContoursInput = hslThresholdOutput;
		
		findContours(findContoursInput, findContoursExternalOnly, findContoursOutput);
		filterContoursContours = findContoursOutput;
		
		filterContours(filterContoursContours, filterContoursMinArea, filterContoursMinPerimeter, filterContoursMinWidth, filterContoursMaxWidth, filterContoursMinHeight, filterContoursMaxHeight, filterContoursSolidity, filterContoursMaxVertices, filterContoursMinVertices, filterContoursMinRatio, filterContoursMaxRatio, filterContoursOutput);
		
		System.out.println(filterContoursOutput.size());
		
		if(goalOrNah) {
			if(!locateContours(filterContoursOutput, source0, true)) {
				System.out.println("goal no");
				return false;
			} else {
				System.out.println("goal yes");
				return true;
			}
		} else {
			if(!locateContours(filterContoursOutput, source0, false)) {
				System.out.println("gear no");
				return false;
			} else {
				System.out.println("gear yes");
				return true;
			}
		}
	}
	
	/**
	 * This method sets topTape and bottomTape to the indexes
	 * on filterContoursOutput for their respective pieces of tape
	 * NOTE: topTape and bottomTape will be set to -1 of no goal was found
	 * @param contours is the input list of contours
	 */
	public boolean locateContours(List<MatOfPoint> inputContours, Mat imageToSend, boolean goalOrNah) {
		if(goalOrNah) {
			goalNum = 0;
			realGoalNum = -1;
			maxValue = 0;
			if(inputContours.size() < 2) {
				topTape = -1;
				bottomTape = -1;
				return false;
			} else {
				for(int i = 0; i < inputContours.size(); i++) {
					for(int j = 0; j < inputContours.size(); j++) {
						if(i != j) {
							topRect = Imgproc.boundingRect(inputContours.get(i));
							bottomRect = Imgproc.boundingRect(inputContours.get(j));
							goalNum++;
							//System.out.println("Have no fear for I am here");
							isGoal(topRect, bottomRect, imageToSend, i, j, goalNum);
						}
						
					}
				}
				for(int i = 0; i <= goalNum; i++) {
					if(goals[i][10] > maxValue) {
						maxValue = goals[i][10];
						realGoalNum = i;
					}
				}
				height = (goals[realGoalNum][4] + goals[realGoalNum][9]) - goals[realGoalNum][2];
				distance = (400) / (1 * height * Math.tan(0.5986479));
				distance = distance * 12;
				groundDistance = Math.sqrt((distance * distance) - (56.25 * 56.25)); // should be 88 * 88
				screenWidth = imageToSend.size().width;
				screenHeight = imageToSend.size().height;
				distanceFromGoal = (screenWidth / 2) - (goals[realGoalNum][3] + (goals[realGoalNum][6] / 2));
				distanceToOrgin = (10 * distanceFromGoal) / height;
				realDistance = Math.sqrt((groundDistance * groundDistance) + (distanceToOrgin * distanceToOrgin));
				angleToCorrect = Math.atan(distanceToOrgin / groundDistance);
				yOffset = ((screenHeight / 2 ) - (goals[realGoalNum][2] + (height / 2)));
				maybeGroundDistance = (-0.146583 * yOffset) + 54.146594;
				/*
				System.out.println("********************************** Pixel offset y: " + yOffset);
				System.out.println("********************************** Angle: " + angleToCorrect);
				System.out.println("---------------------------------- groundDistance: " + maybeGroundDistance);
				*/
				return true;
			}
		}
		else {
			gearNum = 0;
			realGearNum = -1;
			maxValue = 0;
			if(inputContours.size() < 2) {
				topTape = -1;
				bottomTape = -1;
				return false;
			} else {
				for(int i = 0; i < inputContours.size(); i++) {
					for(int j = 0; j < inputContours.size(); j++) {
						if(i != j) {
							Rect topRect = Imgproc.boundingRect(inputContours.get(i));
							Rect bottomRect = Imgproc.boundingRect(inputContours.get(j));
							gearNum++;
							isGearTape(topRect, bottomRect, imageToSend, i, j, gearNum);
						}
						
					}
				}
				for(int i = 0; i <= gearNum	; i++) {
					if(gears[i][0] > maxValue) {
						maxValue = gears[i][7]; //how do you hack into the US nuclear codes?? Ask one of Yusuf's friend to do it
						realGearNum = i;
					}
				}
				distance = (.4166667 * 635) / (1 * ((gears[realGearNum][5] + gears[realGearNum][7]) / 2) * Math.tan(0.5986479));
				distance = distance * 12;
				System.out.println("********************   " + maxValue);
				Imgproc.rectangle(imageToSend, new Point(gears[realGearNum][1], gears[realGearNum][0]), new Point(gears[realGearNum][1] + gears[realGearNum][4], gears[realGearNum][0] + gears[realGearNum][5]), new Scalar(0, 255, 255, 255), 2);
				Imgproc.rectangle(imageToSend, new Point(gears[realGearNum][3], gears[realGearNum][2]), new Point(gears[realGearNum][3] + gears[realGearNum][6], gears[realGearNum][2] + gears[realGearNum][7]), new Scalar(0, 0, 255, 255), 2);
				Imgproc.putText(imageToSend, distance + "in", new Point(50, 50), Core.FONT_HERSHEY_SIMPLEX, 1.2f, new Scalar(255, 255, 255, 255), 2);
				imgWithRect = imageToSend;
				//Imgproc.putText(imageToSend, "This is a lot of text", new Point(50, 50), Core.FONT_HERSHEY_SIMPLEX, 2.0f, new Scalar(255, 255, 255, 255), 1);
				//Imgcodecs.imwrite("C:\\Users\\Nolan Blankenau\\workspace\\GRIPstandAlone\\src\\GearWithRect131.png", imageToSend);
				return true;
			}
		}
		
	}
	
	/**
	 * 
	 * @param theImage image to be processed
	 * @param theGoal is true if you want to find the goal and false if you want to find the gear
	 * @return the distance in inches to the goal
	 */
	public double getGroundDistance(Mat theImage, boolean theGoal) {
		if(process(theImage, theGoal)) {
			return groundDistance;
		} else {
			return -1;
		}
	}
	
	/**
	 * 
	 * @param theImage image to be processed
	 * @param theGoal is true if you want to find the goal and false if you want to find the gear
	 * @return the offset angle in radians
	 */
	public double getAngleOffset(Mat theImage, boolean theGoal) {
		if(process(theImage, theGoal)) {
			return angleToCorrect;
		} else {
			return -361;
		}
	}
	
	/**
	 * This method determines if the two rectangles represents the goal or not
	 * NOTE: Assume that Rect.x and Rect.y correspond to the top-right corner of the rectangle
	 * @param tRect represents what could be the top rectangle
	 * @param bRect represents what could be the bottom rectangle
	 * @return true if the rectangle variables do seem to act like the goal and false if otherwise
	 */
	public boolean isGoal(Rect tRect, Rect bRect, Mat imageWithRect, int iIndex, int jIndex, int goalNum) {
		isGoalScore = 0;
		tY = tRect.y;
		tX = tRect.x;
		bY = bRect.y;
		bX = bRect.x;
		tHeight = tRect.height;
		bHeight = bRect.height;
		tWidth = tRect.width;
		bWidth = bRect.width;
	    tArea = tHeight * tWidth;
		bArea = bHeight * bWidth;
		height = (bY + bHeight) - tY;
		
		if(tY < bY && Math.abs(tX - bX) < (tWidth + bWidth) && tY + tHeight < bY + bHeight) {

			double groupHeight = tHeight / (height * .4);
			double bottomHeight = bHeight / (height * .2);
			double dTop = (tHeight + (bY - (tY +tHeight))) / (height * .6);
			double lEdge = (tX - bX) / tWidth;
			double widthRatio = tWidth / bWidth;
			double heightRatio = tHeight / (bHeight * 2);
			System.out.println("goalNum " + goalNum);
			System.out.println("iIndex " + iIndex);
			goals[goalNum][0] = iIndex;
			goals[goalNum][1] = jIndex;
			goals[goalNum][2] = tY;
			goals[goalNum][3] = tX;
			goals[goalNum][4] = bY;
			goals[goalNum][5] = bX;
			goals[goalNum][6] = tWidth;
			goals[goalNum][7] = tHeight;
			goals[goalNum][8] = bWidth;
			goals[goalNum][9] = bHeight;
			goals[goalNum][10] = groupHeight + bottomHeight + dTop + lEdge + widthRatio + heightRatio;
	
			System.out.println("total " + goalNum + " " + goals[goalNum][10]);
			
		}
		
		/*if(tY < bY + height) {
			isGoalScore++;
			System.out.println("1");
		}
		if(tHeight < tWidth) {
			isGoalScore++;
			System.out.println("2");
		}
		if(bHeight < bWidth) {
			isGoalScore++;
			System.out.println("3");
		}
		if(tX > bX - bWidth && tX < bX + bWidth) {
			isGoalScore++;
			System.out.println("4");
		}
		if(Math.abs((tY + tHeight) - bY) < tHeight) {
			isGoalScore++;
			System.out.println("5");
		}

		if(isGoalScore == 4) {
			distance = (400) / (1 * height * Math.tan(0.5986479));
			System.out.println("Height " + height);
			System.out.println("Distance " + distance);
			distanceFromGoal = 0.0;
			groundDistance = Math.sqrt((distance * distance) - (6.1458333 * 6.1458333)); // should be 88 * 88
			screenWidth = imageWithRect.size().width;
			distanceFromGoal = (screenWidth / 2) - (tY + (tWidth / 2));
			distanceToOrgin = ((5 / 6) * distanceFromGoal) / height;
			realDistance = Math.sqrt((groundDistance * groundDistance) + (distanceToOrgin * distanceToOrgin));
			angleToCorrect = Math.sin(distanceToOrgin / groundDistance);
			
			System.out.println((angleToCorrect * 180) / Math.PI);
			Imgproc.rectangle(imageWithRect, new Point(tX, tY), new Point(tX + tWidth, tY + tHeight), new Scalar(0, 255, 255, 255), 2);
			Imgproc.rectangle(imageWithRect, new Point(bX, bY), new Point(bX + bWidth, bY + bHeight), new Scalar(0, 0, 255, 255), 2);
			Imgproc.putText(imageWithRect, String.valueOf(groundDistance) + "in", new Point(tX + tWidth + 5, tY), Core.FONT_HERSHEY_SIMPLEX, 0.6f, new Scalar(255, 255, 255, 255), 1);
			imgWithRect = imageWithRect;
			return true;
		} else {
			//Imgproc.putText(imageWithRect, isGoalScore + "", new Point(tX + tWidth + 5, tY), Core.FONT_HERSHEY_SIMPLEX, 0.4f, new Scalar(255, 255, 255, 255), 1);
			return false;
		}*/
		return false;
	}
	
	public boolean isGearTape(Rect lRect, Rect rRect, Mat imageWithRect, int iIndex, int jIndex, int gearNum) {
		
		lY = lRect.y;
		lX = lRect.x;
		rY = rRect.y;
		rX = rRect.x;
		lHeight = lRect.height;
		rHeight = rRect.height;
		lWidth = lRect.width;
		rWidth = rRect.width;
		lArea = lHeight * lWidth;
		rArea = rHeight * rWidth;
		width = (rX + rWidth) - lX;
		
		double lWidthRatio = lWidth / (width * .19512195122);
		double rWidthRatio = rWidth / (width * .19512195122);
		double heightToWidthRatio = ((lHeight + rHeight) / 2) / (width * .48780487804878);
		
		gears[gearNum][0] = lY;
		gears[gearNum][1] = lX;
		gears[gearNum][2] = rY;
		gears[gearNum][3] = rX;
		gears[gearNum][4] = lWidth;
		gears[gearNum][5] = lHeight;
		gears[gearNum][6] = rWidth;
		gears[gearNum][7] = rHeight;
		gears[gearNum][8] = lWidthRatio + rWidthRatio + heightToWidthRatio;
		
		System.out.println("gears " + gearNum + " " + gears[gearNum][8]);
		
		
		return true;
	}

	/**
	 * This method is a generated getter for the output of a HSL_Threshold.
	 * @return Mat output from HSL_Threshold.
	 */
	public Mat hslThresholdOutput() {
		return hslThresholdOutput;
	}

	/**
	 * This method is a generated getter for the output of a Find_Contours.
	 * @return ArrayList<MatOfPoint> output from Find_Contours.
	 */
	public ArrayList<MatOfPoint> findContoursOutput() {
		return findContoursOutput;
	}

	/**
	 * This method is a generated getter for the output of a Filter_Contours.
	 * @return ArrayList<MatOfPoint> output from Filter_Contours.
	 */
	public ArrayList<MatOfPoint> filterContoursOutput() {
		return filterContoursOutput;
	}


	/**
	 * Segment an image based on hue, saturation, and luminance ranges.
	 *
	 * @param input The image on which to perform the HSL threshold.
	 * @param hue The min and max hue
	 * @param sat The min and max saturation
	 * @param lum The min and max luminance
	 * @param output The image in which to store the output.
	 */
	private void hslThreshold(Mat input, double[] hue, double[] sat, double[] lum,
		Mat out) {
		Imgproc.cvtColor(input, out, Imgproc.COLOR_BGR2HLS);
		Core.inRange(out, new Scalar(hue[0], lum[0], sat[0]),
			new Scalar(hue[1], lum[1], sat[1]), out);
	}

	/**
	 * Sets the values of pixels in a binary image to their distance to the nearest black pixel.
	 * @param input The image on which to perform the Distance Transform.
	 * @param type The Transform.
	 * @param maskSize the size of the mask.
	 * @param output The image in which to store the output.
	 */
	private void findContours(Mat input, boolean externalOnly,
		List<MatOfPoint> contours) {
		Mat hierarchy = new Mat();
		contours.clear();
		int mode;
		if (externalOnly) {
			mode = Imgproc.RETR_EXTERNAL;
		}
		else {
			mode = Imgproc.RETR_LIST;
		}
		int method = Imgproc.CHAIN_APPROX_SIMPLE;
		Imgproc.findContours(input, contours, hierarchy, mode, method);
	}


	/**
	 * Filters out contours that do not meet certain criteria.
	 * @param inputContours is the input list of contours
	 * @param output is the the output list of contours
	 * @param minArea is the minimum area of a contour that will be kept
	 * @param minPerimeter is the minimum parameter of a contour that will be kept
	 * @param minWidth minimum width of a contour
	 * @param maxWidth maximum width
	 * @param minHeight minimum height
	 * @param maxHeight maximum height
	 * @param Solidity the minimum and maximum solidity of a contour
	 * @param minVertexCount minimum vertex Count of the contours
	 * @param maxVertexCount maximum vertex Count
	 * @param minRatio minimum ratio of width to height
	 * @param maxRatio maximum ratio of width to height
	 */
	private void filterContours(List<MatOfPoint> inputContours, double minArea,
		double minPerimeter, double minWidth, double maxWidth, double minHeight, double
		maxHeight, double[] solidity, double maxVertexCount, double minVertexCount, double
		minRatio, double maxRatio, List<MatOfPoint> output) {
		
		final MatOfInt hull = new MatOfInt();
		output.clear();
		//operation
		for (int i = 0; i < inputContours.size(); i++) {
			final MatOfPoint contour = inputContours.get(i);
			final Rect bb = Imgproc.boundingRect(contour);
			if (bb.width < minWidth || bb.width > maxWidth) continue;
			if (bb.height < minHeight || bb.height > maxHeight) continue;
			final double area = Imgproc.contourArea(contour);
			if (area < minArea) continue;
			if (Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true) < minPerimeter) continue;
			Imgproc.convexHull(contour, hull);
			MatOfPoint mopHull = new MatOfPoint();
			mopHull.create((int) hull.size().height, 1, CvType.CV_32SC2);
			for (int j = 0; j < hull.size().height; j++) {
				int index = (int)hull.get(j, 0)[0];
				double[] point = new double[] { contour.get(index, 0)[0], contour.get(index, 0)[1]};
				mopHull.put(j, 0, point);
			}
			final double solid = 100 * area / Imgproc.contourArea(mopHull);
			if (solid < solidity[0] || solid > solidity[1]) continue;
			if (contour.rows() < minVertexCount || contour.rows() > maxVertexCount)	continue;
			final double ratio = bb.width / (double)bb.height;
			if (ratio < minRatio || ratio > maxRatio) continue;
			output.add(contour);
		}
	}
}
