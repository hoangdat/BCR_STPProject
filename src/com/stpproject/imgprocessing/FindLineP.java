package com.stpproject.imgprocessing;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

public class FindLineP {

	public static Mat findCany(Mat mat) {
		Mat imgGray = new Mat();
		Mat imgGausi = new Mat();
		Mat imgCanny = new Mat();
		Mat img = mat;
		Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.GaussianBlur(img, imgGausi, new org.opencv.core.Size(9, 9), 0);
		Imgproc.Canny(imgGausi, imgCanny, 50, 50);

		return imgCanny;

	}

	public static Point computeIntersect(double[] a, double[] b) {
		double r1 = a[0];
		double te1 = a[1];
		double r2 = b[0];
		double te2 = b[1];

		Point pt = new Point();
		if (te1 == te2) {
			return new Point(-1, -1);
		} else {
			pt.y = (r1 * Math.cos(te2) - r2 * Math.cos(te1))
					/ Math.sin(te1 - te2);
			pt.x = r1 / Math.cos(te1) - pt.y * Math.tan(te1);
			return pt;
		}

	}

	public static Mat warp1(Mat inputMat, Point p1, Point p2, Point p3, Point p4) {
		int resultWidth = inputMat.width();
		int resultHeight = inputMat.height();

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

	public static boolean Check4Point(Mat img, Point p1, Point p2, Point p3,
			Point p4) {

		if (!checkL(img, p1, p2))
			return false;
		if (!checkL(img, p2, p3))
			return false;
		if (!checkL(img, p3, p4))
			return false;
		if (!checkL(img, p4, p1))
			return false;
		return true;

	}

	public static boolean checkL(Mat img, Point p1, Point p2) {
		if (!check1Point(img, p1))
			return false;
		if (!check1Point(img, p2))
			return false;
		double l = Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)
				* (p1.y - p2.y));
		double lCheck = img.height() / 4;
		if (l < lCheck) {
			return false;
		} else {
			return true;
		}

	}

	public static boolean check1Point(Mat img, Point p1) {
		double he = img.height();
		double wi = img.width();
		if (p1.x < -wi / 4) {
			// throw new MyException("try again please");
			return false;

		}
		if (p1.y < -he / 4)
			return false;
		if (p1.x > 5 * wi / 4)
			return false;
		if (p1.y > 5 * he / 4)
			return false;
		return true;
	}

	public static double getL(Point p1, Point p2) {
		double l = Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)
				* (p1.y - p2.y));
		return l;

	}

	public static Point interpolate(double[] a, Point P1, double l) {
		double rA = a[0];
		double theA = a[1];
		double x2 = P1.x + l * Math.sin(theA);
		double y2 = (-Math.cos(theA) / Math.sin(theA)) * x2 + rA
				/ Math.sin(theA);
		Point P2 = new Point(x2, y2);
		return P2;
	}

	private static double[] findLineLeft(Mat img, Mat imgCanny) {
		double[] dataLeft = null;
		Mat lines = new Mat();
		double thetaNguong = Math.PI / 9;
		Imgproc.HoughLines(imgCanny, lines, 1, Math.PI / 180, 80);
		double[] data;
		double rho, theta = 0, thetaMin, tera;
		double rLeft;
		if (lines.depth() == 0) {
			return null;
		}
		tera = lines.get(0, 0)[1];
		thetaMin = tera;

		rLeft = 10000;
		int iLeft;
		iLeft = 0;

		thetaMin = 0;
		for (int i = 0; i < lines.cols(); i++) {

			data = lines.get(0, i);
			rho = data[0];
			theta = data[1];
			if (rho < 0) {
				rho = Math.abs(rho);
				theta = -theta + Math.PI;

			}

			if (Math.abs(theta - thetaMin) < thetaNguong) {
				// find Left
				if (rho <= rLeft) {
					rLeft = rho;
					iLeft = i;
				}

			}
		}
		dataLeft = lines.get(0, iLeft);
		return dataLeft;

	}

	private static double[] findLineRight(Mat img, Mat imgCanny) {
		double[] dataRight = null;
		Mat lines = new Mat();
		double thetaNguong = Math.PI / 9;
		Imgproc.HoughLines(imgCanny, lines, 1, Math.PI / 180, 80);
		double[] data;
		double rho, theta = 0, thetaMin, tera;
		double rRight;
		if (lines.depth() == 0) {
			return null;
		}
		tera = lines.get(0, 0)[1];
		thetaMin = tera;

		rRight = 0;
		int iRight;
		iRight = 0;

		thetaMin = 0;
		for (int i = 0; i < lines.cols(); i++) {

			data = lines.get(0, i);
			rho = data[0];
			theta = data[1];
			if (rho < 0) {
				rho = Math.abs(rho);
				theta = -theta + Math.PI;

			}

			if (Math.abs(theta - thetaMin) < thetaNguong) {
				// find Right
				if (rho >= rRight) {
					rRight = rho;
					iRight = i;
				}

			}
		}
		dataRight = lines.get(0, iRight);
		return dataRight;

	}

	public static Mat findLineFull(Mat img, Mat imgCanny) {
		double thetaNguong = Math.PI / 9;
		Mat lines = new Mat();
		Imgproc.HoughLines(imgCanny, lines, 1, Math.PI / 180, 150);
		double[] data;
		double rho, theta = 0, thetaMax, thetaMin, tera;
		double rLeft, rRight, rTop, rDown;

		// tim Top va Down truoc

		if (lines.depth() == 0) {
			return img;
		}
		tera = lines.get(0, 0)[1];
		thetaMax = tera;
		thetaMin = tera;

		rLeft = rTop = 10000;
		rRight = rDown = 0;
		int iLeft, iRight, iTop, iDown;
		iLeft = iRight = iTop = iDown = 0;

		thetaMax = Math.PI / 2;
		thetaMin = 0;
		double thetaTop = 0, thetaDown = 0;

		for (int i = 0; i < lines.cols(); i++) {

			data = lines.get(0, i);
			rho = data[0];
			theta = data[1];
			if (rho < 0) {
				rho = Math.abs(rho);
				theta = -theta + Math.PI;

			}

			if (Math.abs(theta - thetaMin) < thetaNguong) {
				// find Left,Right
				if (rho <= rLeft) {
					rLeft = rho;
					iLeft = i;
				}
				if (rho >= rRight) {
					rRight = rho;
					iRight = i;
				}
			}
			if (Math.abs(thetaMax - theta) < thetaNguong) {
				// find Top,Down
				if (rho <= rTop) {
					rTop = rho;
					iTop = i;
					thetaTop = theta;
				}
				if (rho >= rDown) {
					rDown = rho;
					iDown = i;
					thetaDown = theta;
				}

			}

		}
		double[] aTop = lines.get(0, iTop);
		double[] aDown = lines.get(0, iDown);
		thetaDown = lines.get(0, iTop)[1];
		thetaDown = Math.PI / 2 - thetaDown;
		thetaTop = lines.get(0, iDown)[1];
		thetaTop = Math.PI / 2 - thetaTop;
		// tim den Left va Right

		Mat lines2 = new Mat();
		Imgproc.HoughLines(imgCanny, lines2, 1, Math.PI / 180, 120);
		thetaMax = Math.PI / 2;
		thetaMin = 0;
		if (lines2.depth() == 0) {
			return img;
		}

		for (int i = 0; i < lines.cols(); i++) {

			data = lines.get(0, i);
			rho = data[0];
			theta = data[1];
			if (rho < 0) {
				rho = Math.abs(rho);
				theta = -theta + Math.PI;

			}

			if (Math.abs(theta - thetaMin) < thetaNguong) {
				// find Left,Right
				if (rho <= rLeft) {
					rLeft = rho;
					iLeft = i;
				}
				if (rho >= rRight) {
					rRight = rho;
					iRight = i;
				}
			}

		}

		double[] aLeft = lines.get(0, iLeft);
		double[] aRight = lines.get(0, iRight);

		rLeft = aLeft[0];
		rRight = aRight[0];
		double wi = img.width();
		double denta = img.height() / 10;

		if (((rRight - rLeft) < denta) & (rRight > 2 * wi / 3) & (rRight < wi)) {

			// truong hop mat duong ben Trai

			double[] dataLeft = findLineLeft(img, imgCanny);
			aLeft = dataLeft;
		}

		if (((rRight - rLeft) < denta) & (rLeft > 0) & (rLeft < wi / 3)) {

			// truong hop mat duong ben Phai

			double[] dataRight = findLineRight(img, imgCanny);
			aRight = dataRight;
		}

		Point p1 = new Point(0, 0);
		Point p2 = new Point(0, 0);
		Point p3 = new Point(0, 0);
		Point p4 = new Point(0, 0);

		p1 = computeIntersect(aLeft, aTop);
		p2 = computeIntersect(aLeft, aDown);
		p3 = computeIntersect(aDown, aRight);
		p4 = computeIntersect(aRight, aTop);

		boolean isOk = Check4Point(img, p1, p2, p3, p4);
		if (isOk) {
			// toa doa quay va goi quay thetatop va iG;
			float Gx = (float) ((p1.x + p2.x + p3.x + p4.x) / 4);
			float Gy = (float) ((p1.y + p2.y + p3.y + p4.y) / 4);
			double L14 = Math.sqrt((p1.x - p4.x) * (p1.x - p4.x)
					+ (p1.y - p4.y) * (p1.y - p4.y));
			double L12 = Math.sqrt((p1.x - p2.x) * (p1.x - p2.x)
					+ (p1.y - p2.y) * (p1.y - p2.y));
			float degre = (float) ((thetaTop / Math.PI * 180 + thetaDown
					/ Math.PI * 180) / 2);
			float zoom = (float) ((img.height() + img.width()) / (L14 + L12));

			// cac gia tri xoay anh
			ValueOpenCv.xG = Gx;
			ValueOpenCv.yG = Gy;
			ValueOpenCv.degre = degre;
			ValueOpenCv.zoom = zoom;

			img = warp1(img, p1, p2, p3, p4);

		}
		return img;

	}
}
