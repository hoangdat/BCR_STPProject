package com.stpproject.imgprocessing;


import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

public class ImageBorder {

	public static Mat resizeMat(Mat mat) {
		Mat matresult = new Mat();
		Imgproc.resize(mat, matresult, new Size(1024, 600), 0, 0,
				Imgproc.INTER_CUBIC);
		return matresult;

	}

	public static Mat runCanny(Mat mat) {
		Mat imgGray = new Mat();
		Mat imgGausi = new Mat();
		Mat imgCanny = new Mat();
		Mat img = mat;
		Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.GaussianBlur(imgGray, imgGausi, new org.opencv.core.Size(1, 1),
				0);
		Imgproc.Canny(imgGausi, imgCanny, 50, 50);
		Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_DILATE,
				new Size(3, 3), new Point(1, 1));
		Imgproc.dilate(imgCanny, imgCanny, kernel);

		return imgCanny;

	}
	public static Mat findCany(Mat mat) {
		Mat imgGray = new Mat();
		Mat imgGausi = new Mat();
		Mat imgCanny = new Mat();
		Mat img = mat;
		Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.GaussianBlur(imgGray, imgGausi,
				new org.opencv.core.Size(15, 15), 0);
		Imgproc.Canny(img, imgCanny, 50, 50);
		Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(3,3), new Point(1,1));
		Imgproc.dilate(imgCanny, imgCanny, kernel);

		return imgCanny;

	}

	public static Mat runBorder(Mat imgCanny, Mat img) {

		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(imgCanny, contours, new Mat(), Imgproc.RETR_TREE,
				Imgproc.CHAIN_APPROX_SIMPLE);

		double maxArea = 0;
		MatOfPoint2f biggest = null;

		for (int i = 0; i < contours.size(); i++) {
			MatOfPoint tem_area = contours.get(i);
			double area = Imgproc.contourArea(tem_area);

			MatOfPoint2f new_mat = new MatOfPoint2f(tem_area.toArray());
			MatOfPoint2f approx = new MatOfPoint2f();

			if (area > 15000) {
				double peri = Imgproc.arcLength(new_mat, true);
				Imgproc.approxPolyDP(new_mat, approx, 0.02 * peri, true);

				if ((area > maxArea) & (approx.total() == 4)) {
					biggest = approx;
					maxArea = area;

				}
			}

		}

		double[] temp_double;
		temp_double = biggest.get(0, 0);
		Point p1 = new Point(temp_double[0], temp_double[1]);

		temp_double = biggest.get(1, 0);
		Point p2 = new Point(temp_double[0], temp_double[1]);

		temp_double = biggest.get(2, 0);
		Point p3 = new Point(temp_double[0], temp_double[1]);

		temp_double = biggest.get(3, 0);
		Point p4 = new Point(temp_double[0], temp_double[1]);

		Mat img2 = warp2(img, p1, p2, p3, p4);
		return img2;

	}

	public static Mat warp2(Mat inputMat, Point p1, Point p2, Point p3, Point p4) {
		int resultWidth = inputMat.width();
		int resultHeight = inputMat.height();
		Point m1 = null, m2 = null, m3 = null, m4 = null;

		// find m1,m3

		double[] l1 = new double[4];
		l1[0] = p1.x * p1.x + p1.y * p1.y;
		l1[1] = p2.x * p2.x + p2.y * p2.y;
		l1[2] = p3.x * p3.x + p3.y * p3.y;
		l1[3] = p4.x * p4.x + p4.y * p4.y;

		double min = l1[0], max = l1[0];
		for (int i = 0; i < 4; i++) {
			if (l1[i] < min) {
				min = l1[i];
			}
		}
		for (int i = 0; i < 4; i++) {
			if (l1[i] > max) {
				max = l1[i];
			}
		}

		if (min == l1[0])
			m1 = p1;
		if (min == l1[1])
			m1 = p2;
		if (min == l1[2])
			m1 = p3;
		if (min == l1[3])
			m1 = p4;

		if (max == l1[0])
			m3 = p1;
		if (max == l1[1])
			m3 = p2;
		if (max == l1[2])
			m3 = p3;
		if (max == l1[3])
			m3 = p4;

		// find m2,m4

		double[] l2 = new double[4];
		l2[0] = p1.x * p1.x + (p1.y - resultHeight) * (p1.y - resultHeight);
		l2[1] = p2.x * p2.x + (p2.y - resultHeight) * (p2.y - resultHeight);
		l2[2] = p3.x * p3.x + (p3.y - resultHeight) * (p3.y - resultHeight);
		l2[3] = p4.x * p4.x + (p4.y - resultHeight) * (p4.y - resultHeight);
		double min2 = l2[0], max2 = l2[0];
		for (int i = 0; i < 4; i++) {
			if (l2[i] < min2) {
				min2 = l2[i];
			}
		}
		for (int i = 0; i < 4; i++) {
			if (l2[i] > max2) {
				max2 = l2[i];
			}
		}
		if (min2 == l2[0])
			m2 = p1;
		if (min2 == l2[1])
			m2 = p2;
		if (min2 == l2[2])
			m2 = p3;
		if (min2 == l2[3])
			m2 = p4;

		if (max2 == l2[0])
			m4 = p1;
		if (max2 == l2[1])
			m4 = p2;
		if (max2 == l2[2])
			m4 = p3;
		if (max2 == l2[3])
			m4 = p4;

		Mat outputMat = new Mat(resultWidth, resultHeight, CvType.CV_8UC4);

		List<Point> source = new ArrayList<Point>();
		source.add(m1);
		source.add(m2);
		source.add(m3);
		source.add(m4);
		Mat startM = Converters.vector_Point2f_to_Mat(source);

		Point ocvPOut1 = new Point(0, 0);
		Point ocvPOut2 = new Point(0, resultHeight);
		Point ocvPOut3 = new Point(resultWidth, resultHeight);
		Point ocvPOut4 = new Point(resultWidth, 0);
		List<Point> dest = new ArrayList<Point>();
		dest.add(ocvPOut1);
		dest.add(ocvPOut2);
		dest.add(ocvPOut3);
		dest.add(ocvPOut4);
		Mat endM = Converters.vector_Point2f_to_Mat(dest);

		Mat perspectiveTransform = Imgproc
				.getPerspectiveTransform(startM, endM);

		Imgproc.warpPerspective(inputMat, outputMat, perspectiveTransform,
				new Size(resultWidth, resultHeight), Imgproc.INTER_CUBIC);

		return outputMat;
	}
	public static Mat warp1(Mat inputMat, Point p1, Point p2, Point p3, Point p4) {
		int resultWidth = 900;
		int resultHeight = 900;
	

		Mat outputMat = new Mat(resultWidth, resultHeight, CvType.CV_8UC4);

		List<Point> source = new ArrayList<Point>();
		source.add(p1);
		source.add(p2);
		source.add(p3);
		source.add(p4);
		Mat startM = Converters.vector_Point2f_to_Mat(source);

		Point ocvPOut1 = new Point(0, 0);
		Point ocvPOut2 = new Point(0, resultHeight);
		Point ocvPOut3 = new Point(resultWidth, resultHeight);
		Point ocvPOut4 = new Point(resultWidth, 0);
		List<Point> dest = new ArrayList<Point>();
		dest.add(ocvPOut1);
		dest.add(ocvPOut2);
		dest.add(ocvPOut3);
		dest.add(ocvPOut4);
		Mat endM = Converters.vector_Point2f_to_Mat(dest);

		Mat perspectiveTransform = Imgproc
				.getPerspectiveTransform(startM, endM);

		Imgproc.warpPerspective(inputMat, outputMat, perspectiveTransform,
				new Size(resultWidth, resultHeight), Imgproc.INTER_CUBIC);

		return outputMat;
	}

	public static Mat autostepbytep(Mat mat) {

		int SEHeight = 3;

		int SEWidth = 60;
		int mMorphoOpt = Imgproc.MORPH_CLOSE;
		int testSobelx = 1;
		int testSobely = 0;

		ArrayList<Rect> lstRectResult = new ArrayList<Rect>();
		Mat img = mat;
		Mat imgGray = new Mat();
		Mat imgSobel = new Mat();
		Mat imgThres = new Mat();
		Mat kernlSE = new Mat();
		Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_RGB2GRAY);

		Imgproc.Sobel(imgGray, imgSobel, -1, testSobelx, testSobely, 3, 1, 0,
				Imgproc.BORDER_DEFAULT);

		Imgproc.threshold(imgSobel, imgThres, 0, 255, Imgproc.THRESH_OTSU
				+ Imgproc.THRESH_BINARY);

		kernlSE = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(
				SEWidth, SEHeight));
		Imgproc.morphologyEx(imgThres, imgThres, mMorphoOpt, kernlSE);

		List<MatOfPoint> lstContour = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(imgThres, lstContour, hierarchy,
				Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
		int contourSz = lstContour.size();

		for (int i = 0; i < contourSz; i++) {
			Rect rect = Imgproc.boundingRect(lstContour.get(i));

			if (rect.width >= 100 && rect.height >= 10 && rect.height < 200) {

				lstRectResult.add(rect);
			}
		}
		//Mat matResult = drawRegions(mat, lstRectResult);
		return mat;

	}

	public static Mat drawRegions(Mat btm, ArrayList<Rect> lstBox) {

		for (int i = 0; i < lstBox.size(); i++) {
			Rect tmp_rect = lstBox.get(i);
			int xt = tmp_rect.x - 15;
			int yt = tmp_rect.y - 5;
			int xd = tmp_rect.x + tmp_rect.width + 20;
			int yd = tmp_rect.y + tmp_rect.height + 6;

			Point p1 = new Point(xt, yt);
			Point p2 = new Point(xt, yd);
			Point p3 = new Point(xd, yd);
			Point p4 = new Point(xd, yt);

			Core.line(btm, p1, p2, new Scalar(0, 255, 255), 2);
			Core.line(btm, p2, p3, new Scalar(0, 255, 255), 2);
			Core.line(btm, p3, p4, new Scalar(0, 255, 255), 2);
			Core.line(btm, p4, p1, new Scalar(0, 255, 255), 2);

		}

		return btm;
	}

	public static ArrayList<Mat> cropImage(Mat btm) {

		ArrayList<Mat> arr = new ArrayList<Mat>();

		int SEHeight = 3;

		int SEWidth = 40;
		int mMorphoOpt = Imgproc.MORPH_CLOSE;
		int testSobelx = 1;
		int testSobely = 0;

		ArrayList<Rect> lstRectResult = new ArrayList<Rect>();
		Mat img = btm;
		Mat imgGray = new Mat();
		Mat imgSobel = new Mat();
		Mat imgThres = new Mat();
		Mat kernlSE = new Mat();
		Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_RGB2GRAY);

		Imgproc.Sobel(imgGray, imgSobel, -1, testSobelx, testSobely, 3, 1, 0,
				Imgproc.BORDER_DEFAULT);

		Imgproc.threshold(imgSobel, imgThres, 0, 255, Imgproc.THRESH_OTSU
				+ Imgproc.THRESH_BINARY);

		kernlSE = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(
				SEWidth, SEHeight));
		Imgproc.morphologyEx(imgThres, imgThres, mMorphoOpt, kernlSE);

		List<MatOfPoint> lstContour = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(imgThres, lstContour, hierarchy,
				Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
		int contourSz = lstContour.size();

		for (int i = 0; i < contourSz; i++) {
			Rect rect = Imgproc.boundingRect(lstContour.get(i));

			if (rect.width >= 100 && rect.height >= 10 && rect.height < 600) {

				lstRectResult.add(rect);
			}
		}

		ArrayList<Rect> lstBox = lstRectResult;
		for (int i = 0; i < lstBox.size(); i++) {
			Rect tmp_rect = lstBox.get(i);
			int xt = tmp_rect.x - 15;
			int yt = tmp_rect.y - 5;
//			int xd = tmp_rect.x + tmp_rect.width + 20;
//			int yd = tmp_rect.y + tmp_rect.height + 6;

			Mat image = new Mat();
			image=btm;
			Rect rectCrop = new Rect(xt, yt, tmp_rect.width + 15,
					tmp_rect.height + 5);
			Mat imCrop = new Mat(image, rectCrop);
			arr.add(imCrop);

		}

		return arr;

	}

}
