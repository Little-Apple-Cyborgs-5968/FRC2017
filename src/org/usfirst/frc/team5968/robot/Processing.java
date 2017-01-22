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
	Mat imgWithRect;
	
	Mat hslThresholdInput;
	double[] hslThresholdHue = {33, 85};
	double[] hslThresholdSaturation = {42, 255.0};
	double[] hslThresholdLuminance = {13, 255.0};
	
	ArrayList<MatOfPoint> filterContoursContours;
	
	double filterContoursMinArea = 150;
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
	double height;
	double distance; // cm
	double angleToCorrect; // goal to the left of center is positive
	
	int isGoalScore = 0;
	
	double distanceFromGoal;
	double realDistance;
	double groundDistance;
	double screenWidth;

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	/**
	 * This is the primary method that runs the entire pipeline and updates the outputs.
	 */
	public Mat process(Mat source0) {
		
		hslThresholdInput = source0;
		
		hslThreshold(hslThresholdInput, hslThresholdHue, hslThresholdSaturation, hslThresholdLuminance, hslThresholdOutput);
		findContoursInput = hslThresholdOutput;
		
		findContours(findContoursInput, findContoursExternalOnly, findContoursOutput);
		filterContoursContours = findContoursOutput;
		
		filterContours(filterContoursContours, filterContoursMinArea, filterContoursMinPerimeter, filterContoursMinWidth, filterContoursMaxWidth, filterContoursMinHeight, filterContoursMaxHeight, filterContoursSolidity, filterContoursMaxVertices, filterContoursMinVertices, filterContoursMinRatio, filterContoursMaxRatio, filterContoursOutput);
		
		System.out.println(filterContoursOutput.size());
		
		if(!locateContours(filterContoursOutput, source0)) {
			System.out.println("goal no");
			return source0;
		} else {
			System.out.println("goal yes");
			return imgWithRect;
		}
	}
	
	/**
	 * This method sets topTape and bottomTape to the indexes
	 * on filterContoursOutput for their respective pieces of tape
	 * NOTE: topTape and bottomTape will be set to -1 of no goal was found
	 * @param contours is the input list of contours
	 */
	public boolean locateContours(List<MatOfPoint> inputContours, Mat imageToSend) {
		if(inputContours.size() < 2) {
			topTape = -1;
			bottomTape = -1;
		} else {
			for(int i = 0; i < inputContours.size(); i++) {
				for(int j = 0; j < inputContours.size(); j++) {
					if(i != j) {
						topRect = Imgproc.boundingRect(inputContours.get(i));
						bottomRect = Imgproc.boundingRect(inputContours.get(j));
						if(isGoal(topRect, bottomRect, imageToSend)) {
							topTape = i;
							bottomTape = j;
							return true;
						}
					}
					
				}
			}
		}
		return false;
	}
	
	/**
	 * This method determines if the two rectangles represents the goal or not
	 * NOTE: Assume that Rect.x and Rect.y correspond to the top-right corner of the rectangle
	 * @param tRect represents what could be the top rectangle
	 * @param bRect represents what could be the bottom rectangle
	 * @return true if the rectangle variables do seem to act like the goal and false if otherwise
	 */
	public boolean isGoal(Rect tRect, Rect bRect, Mat imageWithRect) {
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
		
		if(tY < bY + height) {
			isGoalScore++;
		}
		if(tHeight < tWidth) {
			isGoalScore++;
		}
		if(bHeight < bWidth) {
			isGoalScore++;
		}
		if(tX > bX - bWidth && tX < bX + bWidth) {
			isGoalScore++;
		}
		if(Math.abs((tY + tHeight) - bY) < tHeight) {
			isGoalScore++;
		}

		if(isGoalScore == 5) {
			distance = (23 * 480) / (2 * height * Math.tan(0.6964867));
			System.out.println("Height " + height);
			System.out.println("Distance " + distance);
			distanceFromGoal = 0.0;
			groundDistance = Math.sqrt((distance * distance) - (88 * 88));
			screenWidth = imageWithRect.size().width;
			distanceFromGoal = (screenWidth / 2) - (tY + (tWidth/2));
			realDistance = (10 * distanceFromGoal) / height;
			angleToCorrect = Math.sin(realDistance / groundDistance);
			System.out.println((angleToCorrect * 180) / Math.PI);
			Imgproc.rectangle(imageWithRect, new Point(tX, tY), new Point(tX + tWidth, tY + tHeight), new Scalar(0, 255, 255, 255), 2);
			Imgproc.rectangle(imageWithRect, new Point(bX, bY), new Point(bX + bWidth, bY + bHeight), new Scalar(0, 0, 255, 255), 2);
			Imgproc.putText(imageWithRect, String.valueOf(distance) + "in", new Point(tX + tWidth + 5, tY), Core.FONT_HERSHEY_SIMPLEX, 0.6f, new Scalar(255, 255, 255, 255), 1);
			imgWithRect = imageWithRect;
			return true;
		} else {
			return false;
		}
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
