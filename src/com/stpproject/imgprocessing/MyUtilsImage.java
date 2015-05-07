package com.stpproject.imgprocessing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.example.bcrstpproject.R;
import com.googlecode.leptonica.android.AdaptiveMap;
import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.Convert;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.WriteFile;
import com.stpproject.model.RectItem;
import com.stpproject.semantics.LineItem;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;



public class MyUtilsImage {

	public static final String TAG = "=====MyUtilsImag=======";
	public static final double RATIO_W2H_MIN = 3.8d;
	public static final double RATIO_W2H_MAX = 50d;
	public static int testSobelx = 1;
	public static int testSobely = 0;
	public static int testMorOp = Imgproc.MORPH_CLOSE;
	
	//chuan
	public static final int WIDTH_SE_MORPHOLOGY = 25;
	public static final int HEIGHT_SE_MORPHOLOGY = 3;


	public static ArrayList<Rect> lstRectResult = new ArrayList<Rect>();



	// chua test
	public static Mat convertImage2Mat(Bitmap btm, String extension) {

		Mat matIn = new Mat(btm.getHeight(), btm.getWidth(), CvType.CV_8UC1);
		Utils.bitmapToMat(btm, matIn);
		MatOfByte matEncode = new MatOfByte();
		boolean isEncode = Highgui.imencode(extension, matIn, matEncode);
		if (!isEncode) {
			throw new NullPointerException("Encode failed");
		}
		return matEncode;
	}

	public static Mat convertImage2Mat(Bitmap btmIn) {
		Mat matConverted = new Mat();
		Utils.bitmapToMat(btmIn, matConverted);
		if (matConverted.empty()) {
			throw new NullPointerException("Mat converted is null");
		}
		return matConverted;
	}

	public static Mat convertImage2Mat(String imagePath) {
		Bitmap btmIn = BitmapFactory.decodeFile(imagePath);
		Mat matOut = convertImage2Mat(btmIn);
		return matOut;
	}

	public static Bitmap convertImage2Bitmap(Bitmap btm, String extension) {
		Bitmap btmResult = resizeBitmap(btm);
		if (btm == null) {
			Log.i(TAG, " Bitmap convertImage2Bitmap(btm, extension) null cmnr");
		}
		Mat matOut = convertImage2Mat(btmResult, extension);
		Utils.matToBitmap(matOut, btmResult);
		return btmResult;
	}

	public static Bitmap resizeBitmap(Bitmap btm) {
		Bitmap resize = Bitmap.createScaledBitmap(btm,
				(int) (btm.getWidth() * 0.5), (int) (btm.getHeight() * 0.5),
				true);
		return resize;
	}

	// public static ImageData rgb2gray(String imagePath) {
	// Bitmap btm = BitmapFactory.decodeFile(imagePath);
	// Mat matImg = new Mat(btm.getWidth(), btm.getHeight(), CvType.CV_8UC1);
	// Utils.bitmapToMat(btm, matImg);
	// Mat resultMat = new Mat();
	// Imgproc.cvtColor(matImg, resultMat, Imgproc.COLOR_RGB2GRAY);
	// Utils.matToBitmap(resultMat, btm);
	// ByteArrayOutputStream byteArrStream = new ByteArrayOutputStream();
	// Log.i("Test btm compress", "(btm)truoc khi compress: width " +
	// btm.getWidth() + " height: " + btm.getHeight());
	// btm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrStream);
	// //ImageData imgData = new ImageData(btm.getWidth(), btm.getHeight());
	// Log.i("Test btm compress", "(btm)sau khi compress: width " +
	// btm.getWidth() + " height: " + btm.getHeight());
	// byte[] array = byteArrStream.toByteArray();
	// Bitmap btm_tmp = BitmapFactory.decodeByteArray(array, 0, array.length);
	// Log.i("Test btm compress", "tao bitmap tam: width " + btm.getWidth() +
	// " height: " + btm.getHeight());
	// ImageData imgData = new ImageData(btm_tmp.getWidth(),
	// btm_tmp.getHeight());
	// imgData.setData(array);
	// return imgData;
	// }

	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    
	    Log.d(TAG, "calculated Sample Size: h-w: " + height + "-" + width);
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }
	
	    return inSampleSize;
	}
	
	public static Bitmap rgb2gray(Bitmap btm) {
		Bitmap btmGet = btm;
		Mat matImg = new Mat(btm.getWidth(), btm.getHeight(), CvType.CV_8UC1);
		Utils.bitmapToMat(btm, matImg);
		Mat resultMat = new Mat();
		Imgproc.cvtColor(matImg, resultMat, Imgproc.COLOR_RGB2GRAY);
		Utils.matToBitmap(resultMat, btmGet);
		// imgView.setImageBitmap(btmGet);
		return btmGet;
	}


	public static Bitmap rgb2gray(String imagePath) {
		Bitmap btm = BitmapFactory.decodeFile(imagePath);
		Bitmap btmGet = rgb2gray(btm);
		return btmGet;
	}


	public static Bitmap preProcessImage(String imagePath) {
		Bitmap btm = BitmapFactory.decodeFile(imagePath);

		Pix pix = ReadFile.readBitmap(btm);
		Pix pixGray = Convert.convertTo8(pix);
		// pix = AdaptiveMap.backgroundNormMorph(pixGray);
		pix = AdaptiveMap.backgroundNormMorph(pixGray, 8, 3, 180);

		byte[] subData = subStractData(pixGray.getData(), pix.getData());
		pix = Pix.createFromPix(subData, pixGray.getWidth(),
				pixGray.getHeight(), pixGray.getDepth());

		btm = WriteFile.writeBitmap(pix);

		return btm;
	}

	public static Bitmap preProcessImageMorph(String imagePath, int Reduction,
			int SelSize, int valBg) {
		Bitmap btm = BitmapFactory.decodeFile(imagePath);

		Pix pix = ReadFile.readBitmap(btm);
		Pix pixGray = Convert.convertTo8(pix);

		Pix pixMor = AdaptiveMap.backgroundNormMorph(pixGray, Reduction,
				SelSize, valBg);
		Pix pixBin = Binarize.otsuAdaptiveThreshold(pixMor, 32, 32, 2, 2, 0.1f);
		pix = Binarize.otsuAdaptiveThreshold(pix, 32, 32, 2, 2, 0.1f);
		byte[] subData = subStractData(pix.getData(), pixBin.getData());
		pix = Pix.createFromPix(subData, pixGray.getWidth(),
				pixGray.getHeight(), pixGray.getDepth());

		btm = WriteFile.writeBitmap(pix);

		return btm;
	}

	public static Bitmap preProcessImageMorphBin(String imagePath,
			int Reduction, int SelSize, int valBg, int size, int smoothS,
			float f) {
		Bitmap btm = BitmapFactory.decodeFile(imagePath);

		Pix pix = ReadFile.readBitmap(btm);
		Pix pixGray = Convert.convertTo8(pix);
		// pix = AdaptiveMap.backgroundNormMorph(pixGray);

		Pix pixMor = AdaptiveMap.backgroundNormMorph(pixGray, Reduction,
				SelSize, valBg);
		Pix pixBin = Binarize.otsuAdaptiveThreshold(pixMor, size, size,
				smoothS, smoothS, f);
		pix = Binarize.otsuAdaptiveThreshold(pix, size, size, smoothS, smoothS,
				f);

		byte[] subData = subStractData(pix.getData(), pixBin.getData());
		pix = Pix.createFromPix(subData, pix.getWidth(), pix.getHeight(),
				pix.getDepth());

		btm = WriteFile.writeBitmap(pix);

		return btm;
	}

	public static byte[] subStractData(byte[] host, byte[] sub) {
		int nSize = host.length;
		if (nSize != sub.length) {
			throw new IllegalArgumentException(
					"Argument not have the same size");
		}
		byte[] result = new byte[nSize];
		for (int i = 0; i < nSize; i++) {
			int tmp = (int) host[i] - (int) sub[i];
			result[i] = (byte) tmp;
		}
		return result;
	}


	/**
	 * apply for image with size 1024*600
	 * */
	public static ArrayList<Rect> eliminateNonTextBlock(ArrayList<Rect> arrRects) {
		ArrayList<Rect> arrRectResult = new ArrayList<Rect>();
		int iSize = arrRects.size();

		for (int i = 0; i < iSize; i++) {
			Rect rect = arrRects.get(i);
			int iWidth = rect.width;
			int iHeight = rect.height;

			double dRatioW2H = iWidth * 1.0d / iHeight;
			if ((dRatioW2H >= RATIO_W2H_MIN) && (dRatioW2H <= RATIO_W2H_MAX)) {
				arrRectResult.add(rect);
			}
		}

		return arrRectResult;
	}

	public static Mat genarateImageWithoutNonTextBlock(Mat matIn,
			ArrayList<Rect> arrRects) {
		if (matIn.channels() != 1) {
			return null;
		}

		Mat matOut = Mat.zeros(matIn.rows(), matIn.cols(), CvType.CV_8U);
		int iSize = arrRects.size();

		for (int i = 0; i < iSize; i++) {
			Rect rect = arrRects.get(i);
			int iWidth = rect.width;
			int iHeight = rect.height;

			for (int x = rect.x; x < iWidth; x++) {
				for (int y = rect.y; y < iHeight; y++) {

				}
			}
		}

		return matOut;
	}

	/**
	 * 1 2 3 4 x 5 6 7 8
	 * 
	 * */
	public static double[] getNeighboursPixel(Mat matIn, int x, int y) {
		if (matIn.channels() != 1) {
			return null;
		}
		double[] dPixelNeighbors = new double[8];
		int iIndex = 0;
		for (int i = y - 1; i < y + 2; i++) {
			for (int j = x - 1; j < x + 2; j++) {
				if (i == y && j == x) {
					continue;
				}
				if (i < 0 || (i > (matIn.height() - 1)) || j < 0
						|| (j > (matIn.width() - 1))) {
					dPixelNeighbors[iIndex] = 0;
				} else {
					double[] dTmp = matIn.get(i, j);
					dPixelNeighbors[iIndex] = dTmp[0];
				}
				iIndex++;
			}
		}

		return dPixelNeighbors;
	}

	public static double[] getNeighboursInBoundingBox(Mat matIN, Rect rBox,
			int x, int y) {
		if (matIN.channels() != 1) {
			return null;
		}
		int iRect_x = rBox.x;
		int iRect_y = rBox.y;
		int iRect_w = rBox.width;
		int iRect_h = rBox.height;
		double[] dPixelNeighbours = new double[8];
		int iIndex = 0;
		for (int i = y - 1; i < y + 2; i++) {
			for (int j = x - 1; j < x + 2; j++) {
				if (i == y && j == x) {
					continue;
				}
				if ((i < 0) || (i > (matIN.height() - 1)) || (j < 0)
						|| (j > (matIN.width() - 1)) || (i < iRect_y)
						|| (i > (iRect_y + iRect_h - 1)) || (j < iRect_x)
						|| (j > (iRect_x + iRect_w - 1))) {

					dPixelNeighbours[iIndex] = 0;

				} else {
					double[] dTmp = matIN.get(i, j);
					dPixelNeighbours[iIndex] = dTmp[0];
				}
				iIndex++;
			}
		}
		return dPixelNeighbours;
	}

	public static Bitmap mat2Bitmap(Bitmap origin, Mat img) {
		Bitmap result = origin.copy(Bitmap.Config.ARGB_8888, true);
		Utils.matToBitmap(img, result);
		if (result != null) {
			return result;
		}
		throw new NullPointerException("Return null");
	}

	public static ArrayList<Rect> detectTextBlock(Mat matGray,
			int iWidthSEMorph, int iHeightSEMorph) {
		Mat matSobel = new Mat();
		Mat matThres = new Mat();
		Mat kernlSE = new Mat();

		Imgproc.Sobel(matGray, matSobel, -1, 1, 0, 3, 1, 0,
				Imgproc.BORDER_DEFAULT);
		Imgproc.threshold(matSobel, matThres, 0, 255, Imgproc.THRESH_OTSU
				+ Imgproc.THRESH_BINARY);
		kernlSE = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(
				iWidthSEMorph, iHeightSEMorph));
		Imgproc.morphologyEx(matThres, matThres, Imgproc.MORPH_CLOSE, kernlSE);
		List<MatOfPoint> lstContour = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(matThres, lstContour, hierarchy,
				Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

		int contourSz = lstContour.size();
		Log.d(TAG, "size countour = " + contourSz);
		ArrayList<Rect> lstRectResult = new ArrayList<Rect>();

		for (int i = 0; i < contourSz; i++) {
			Rect rect = Imgproc.boundingRect(lstContour.get(i));
			int iRectWidth = rect.width;
			int iRectHeight = rect.height;

			Log.d(TAG, "Rect thu: " + i + "rect: x:" + rect.x + ",y:" + rect.y
					+ ",w:" + rect.width + ",h:" + rect.height);

			if (iRectWidth >= 100 && iRectHeight >= 10 && iRectHeight < 120) { // giai
				// thich
				Log.d(TAG, "Rect thu: " + i + " duoc chon, x:" + rect.x + ",y:"
						+ rect.y + ",w:" + rect.width + ",h:" + rect.height);

				// them 25-3
				double dRatioW2H = iRectWidth * 1.0d / iRectHeight;
				if ((dRatioW2H >= RATIO_W2H_MIN)
						&& (dRatioW2H <= RATIO_W2H_MAX)) {
					lstRectResult.add(rect);
				}
				// them 25-3

			}

		}
		return lstRectResult;
	}

	/**
	 * @param btm
	 * @param widthSE
	 *            width of structure element
	 * @param heightSE
	 *            height of structure element
	 * 
	 * @return
	 * */
	public static ArrayList<Rect> detectTextBlock(Bitmap btm, int widthSE,
			int heightSE) {
		// lstRectResult = new ArrayList<Rect>(); khong can
		Mat imgGray = new Mat();
		Mat imgSobel = new Mat();
		Mat imgThres = new Mat();
		Mat kernlSE = new Mat();
		Mat img = new Mat();
		Utils.bitmapToMat(btm, img);
		
		Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_RGB2GRAY);

		Imgproc.Sobel(imgGray, imgSobel, -1, 1, 0, 3, 1, 0,
				Imgproc.BORDER_DEFAULT);
		Imgproc.threshold(imgSobel, imgThres, 0, 255, Imgproc.THRESH_OTSU
				+ Imgproc.THRESH_BINARY);
		kernlSE = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(
				widthSE, heightSE));
		Imgproc.morphologyEx(imgThres, imgThres, Imgproc.MORPH_CLOSE, kernlSE);
		List<MatOfPoint> lstContour = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(imgThres, lstContour, hierarchy,
				Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

		int contourSz = lstContour.size();
		Log.d(TAG, "size countour = " + contourSz);
		ArrayList<Rect> lstRectResult = new ArrayList<Rect>();
		for (int i = 0; i < contourSz; i++) {
			Rect rect = Imgproc.boundingRect(lstContour.get(i));
			int iRectWidth = rect.width;
			int iRectHeight = rect.height;
			Log.d(TAG, "Rect thu: " + i + "rect: x:" + rect.x + ",y:" + rect.y
					+ ",w:" + rect.width + ",h:" + rect.height);

			if (iRectWidth >= 100 && iRectHeight >= 10 && iRectHeight < 120) { // giai
				// thich
				Log.d(TAG, "Rect thu: " + i + " duoc chon, x:" + rect.x + ",y:"
						+ rect.y + ",w:" + rect.width + ",h:" + rect.height);

				// them 25-3
				double dRatioW2H = iRectWidth * 1.0d / iRectHeight;
				if ((dRatioW2H >= RATIO_W2H_MIN)
						&& (dRatioW2H <= RATIO_W2H_MAX)) {
					lstRectResult.add(rect);
				}
				// them 25-3
			}
		}
		return lstRectResult;
	}

	public static Bitmap drawListItems(Bitmap btm, ArrayList<LineItem> lstBox) {
		Bitmap btmResult = btm;
		Canvas canvas = new Canvas(btmResult);

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStrokeWidth(0);

		for (int i = 0; i < lstBox.size(); i++) {
			if (i == 0) {
				paint.setColor(Color.RED);
			} else if (i == 1) {
				paint.setColor(Color.BLUE);
			} else if (i == 2) {
				paint.setColor(Color.CYAN);
			} else if (i == 3) {
				paint.setColor(Color.GREEN);
			} else if (i == 4) {
				paint.setColor(Color.YELLOW);
			} else if (i == 5) {
				paint.setColor(Color.CYAN);
			} else if (i == 6) {
				paint.setColor(Color.WHITE);
			} else if (i == 7) {
				paint.setColor(Color.LTGRAY);
			} else if (i == 8) {
				paint.setColor(Color.BLACK);
			} else if (i == 9) {
				paint.setColor(Color.MAGENTA);
			} else if (i == 10) {
				paint.setColor(Color.RED);
			} else if (i == 11) {
				paint.setColor(Color.BLUE);
			} else {
				paint.setColor(Color.BLACK);
			}
			LineItem tmp_rect = lstBox.get(i);
			int xt = tmp_rect.getRectRegion().x;
			int yt = tmp_rect.getRectRegion().y;
			int xd = tmp_rect.getRectRegion().x
					+ tmp_rect.getRectRegion().width;
			int yd = tmp_rect.getRectRegion().y
					+ tmp_rect.getRectRegion().height;
			canvas.drawLine(xt, yt, xd, yt, paint);
			canvas.drawLine(xt, yt, xt, yd, paint);
			canvas.drawLine(xt, yd, xd, yd, paint);
			canvas.drawLine(xd, yt, xd, yd, paint);
			// canvas.drawRect(tmp_rect, paint);
			int he = yd - yt;
			Log.i("Test Rect Box", "xt:" + xt + " yt:" + yt + " xd:" + xd
					+ " yd:" + yd + "height: " + he);
		}

		return btmResult;
	}

	public static Bitmap drawRegions(Bitmap btm, ArrayList<Rect> lstBox) {
		Bitmap btmResult = btm;
		Canvas canvas = new Canvas(btmResult);

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStrokeWidth(0);

		for (int i = 0; i < lstBox.size(); i++) {
			if (i == 0) {
				paint.setColor(Color.RED);
			} else if (i == 1) {
				paint.setColor(Color.BLUE);
			} else if (i == 2) {
				paint.setColor(Color.CYAN);
			} else if (i == 3) {
				paint.setColor(Color.GREEN);
			} else if (i == 4) {
				paint.setColor(Color.YELLOW);
			} else if (i == 5) {
				paint.setColor(Color.CYAN);
			} else if (i == 6) {
				paint.setColor(Color.WHITE);
			} else if (i == 7) {
				paint.setColor(Color.LTGRAY);
			} else if (i == 8) {
				paint.setColor(Color.BLACK);
			} else if (i == 9) {
				paint.setColor(Color.MAGENTA);
			} else if (i == 10) {
				paint.setColor(Color.RED);
			} else if (i == 11) {
				paint.setColor(Color.BLUE);
			}
			Rect tmp_rect = lstBox.get(i);
			int xt = tmp_rect.x;
			int yt = tmp_rect.y;
			int xd = tmp_rect.x + tmp_rect.width;
			int yd = tmp_rect.y + tmp_rect.height;
			canvas.drawLine(xt, yt, xd, yt, paint);
			canvas.drawLine(xt, yt, xt, yd, paint);
			canvas.drawLine(xt, yd, xd, yd, paint);
			canvas.drawLine(xd, yt, xd, yd, paint);
			// canvas.drawRect(tmp_rect, paint);
			int he = yd - yt;
			Log.i("Test Rect Box", "xt:" + xt + " yt:" + yt + " xd:" + xd
					+ " yd:" + yd + "height: " + he);
		}

		return btmResult;
	}

	public static Bitmap morphologyCV(Bitmap btmIn, int morOpt, int threshMode,
			double SEWidth, double SEHeight) {
		Mat imgGray = new Mat();
		Mat imgThres = new Mat();
		Mat imgMor = new Mat();
		Mat kernSE = new Mat();
		Mat img = new Mat();

		Utils.bitmapToMat(btmIn, img);
		Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_RGB2GRAY);
		Imgproc.threshold(imgGray, imgThres, 127, 255, 0);
		kernSE = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(
				SEWidth, SEHeight));
		Imgproc.morphologyEx(imgThres, imgMor, morOpt, kernSE);

		Bitmap btmResult = btmIn;
		Utils.matToBitmap(imgMor, btmResult);
		if (btmResult == null) {
			throw new NullPointerException("Bitmap result null");
		}
		return btmResult;
	}


	public static ArrayList<LineItem> cropImage(Bitmap btm,
			ArrayList<Rect> lstBox) {


		ArrayList<LineItem> lstItem = new ArrayList<LineItem>();

		for (int i = 0; i < lstBox.size(); i++) {
			Rect tmp_rect = lstBox.get(i);

			int xt = tmp_rect.x;
			int yt = tmp_rect.y;
			int wi = tmp_rect.width;
			int hi = tmp_rect.height;

			if (tmp_rect.x - 5 > 0) {
				xt = xt - 5;
				wi = wi + 5;
			}
			if (tmp_rect.y - 5 > 0) {
				yt = yt - 5;
				hi = hi + 5;
			}

			Bitmap bm2 = Bitmap.createBitmap(btm, xt, yt, wi, hi);
			LineItem lineItem = new LineItem();
			lineItem.setBtmRegion(bm2);
			lineItem.setParentHeight(btm.getHeight());
			lineItem.setParentWidth(btm.getWidth());
			lineItem.setRectRegion(new Rect(xt, yt, wi, hi));

			lstItem.add(lineItem);
			Log.i("Test LineItem",
					"size btm: " + btm.getWidth() + " x " + btm.getHeight());
			Log.i("Test LineItem", "xt:" + xt + " yt:" + yt + " wi:" + wi
					+ " hi:" + hi + " size " + bm2.getByteCount());
		}
		return lstItem;
	}

	public static ArrayList<LineItem> sortLineItems(
			ArrayBlockingQueue<LineItem> lstItems) {

		ArrayList<LineItem> lstIN = new ArrayList<LineItem>();
		while (true) {
			LineItem lineItem = lstItems.poll();
			if (lineItem == null) {
				break;
			}
			lstIN.add(lineItem);
		}

		int szIn = lstIN.size();
		for (int i = 1; i < szIn; i++) {
			LineItem tmp_Item = lstIN.get(i);
			int j = i;
			while ((j > 0 && lstIN.get(j - 1).getRectRegion().y > tmp_Item
					.getRectRegion().y)
					|| (j > 0
							&& lstIN.get(j - 1).getRectRegion().y == tmp_Item
									.getRectRegion().y && lstIN.get(j - 1)
							.getRectRegion().x > tmp_Item.getRectRegion().x)) {
				LineItem tmp_Item_J_1 = lstIN.get(j - 1);
				lstIN.set(j, tmp_Item_J_1);
				j = j - 1;
			}
			lstIN.set(j, tmp_Item);
		}

		return lstIN;
	}
	public static ArrayList<LineItem> cropBinarizingImageOtsu(Mat matGray, ArrayList<Rect> lstBox) {
		long startOtsuBinBlock = System.currentTimeMillis();
		ArrayList<LineItem> lstItem = new ArrayList<LineItem>();
		
		for (int i = 0; i < lstBox.size(); i++) {
			Rect rectTmp = lstBox.get(i);
			
			int xt = rectTmp.x;
			int yt = rectTmp.y;
			int wi = rectTmp.width;
			int hi = rectTmp.height;

			if (rectTmp.x - 5 > 0) {
				xt = xt - 5;
				wi = wi + 5;
			}
			if (rectTmp.y - 5 > 0) {
				yt = yt - 5;
				hi = hi + 5;
			}
			
			Mat matSub = matGray.submat(new Rect(xt, yt, wi, hi));
			Mat matSubBin = new Mat();
			Imgproc.threshold(matSub, matSubBin, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
			
			Bitmap btmRegion = Bitmap.createBitmap(matSubBin.width(), matSubBin.height(), Bitmap.Config.ARGB_8888);
			Utils.matToBitmap(matSubBin, btmRegion);

			LineItem lineItem = new LineItem();
			lineItem.setBtmRegion(btmRegion);
			lineItem.setParentHeight(matGray.height());
			lineItem.setParentWidth(matGray.width());
			lineItem.setRectRegion(new Rect(xt, yt, wi, hi));

			lstItem.add(lineItem);
			Log.i(TAG,
					"Da crop thanh cong: "
							+ i
							+ "========================================================================");
		}
		Log.d(TAG, "thoi gian blockOtsu: " + (System.currentTimeMillis() - startOtsuBinBlock));
		return lstItem;
		
	}

	public static ArrayList<LineItem> cropBinarizingImage(Mat matIn,
			ArrayList<Rect> lstBox) {
		long startHome = System.currentTimeMillis();
		ArrayList<LineItem> lstItem = new ArrayList<LineItem>();

		for (int i = 0; i < lstBox.size(); i++) {
			Rect rectTmp = lstBox.get(i);

			MinMaxIntensity minmaxI = findMinAndMaxIntensity(matIn, rectTmp);

			double dMeanMaxMinIntensity = (minmaxI.iMaxIntensity + minmaxI.iMinIntensity) / 2.0d;

			int xt = rectTmp.x;
			int yt = rectTmp.y;
			int wi = rectTmp.width;
			int hi = rectTmp.height;

			if (rectTmp.x - 5 > 0) {
				xt = xt - 5;
				wi = wi + 5;
			}
			if (rectTmp.y - 5 > 0) {
				yt = yt - 5;
				hi = hi + 5;
			}

			Log.d(TAG, "mean of man min: " + dMeanMaxMinIntensity
					+ " of rect: (" + xt + ", " + yt + " w, h:" + wi + ", "
					+ hi);
			Mat matOut = Mat.zeros(new Size(wi, hi), CvType.CV_8U);
			// binary luon mean(min, max) = threshold
			for (int y = yt; y < yt + hi - 1; y++) {
				for (int x = xt; x < xt + wi - 1; x++) {

					double[] dTmpIntensity = matIn.get(y, x);
					Log.d(TAG, "dTmpIntensity: " + dTmpIntensity[0]);
					if (dTmpIntensity[0] < dMeanMaxMinIntensity) {
						matOut.put(y - yt, x - xt, 255.0);
						Log.d(TAG, "tai diem: (" + (y) + ", " + (x) + ")"
								+ "dTmpIntensity: " + dTmpIntensity[0]
								+ " cua rect: " + xt + " " + yt + " w-h" + wi
								+ " " + hi);
					}

				}
			}

			Bitmap btmRegion = Bitmap.createBitmap(wi, hi,
					Bitmap.Config.ARGB_8888);

			Utils.matToBitmap(matOut, btmRegion);

			LineItem lineItem = new LineItem();
			lineItem.setBtmRegion(btmRegion);
			lineItem.setParentHeight(matIn.height());
			lineItem.setParentWidth(matIn.width());
			lineItem.setRectRegion(new Rect(xt, yt, wi, hi));

			lstItem.add(lineItem);
			Log.i(TAG,
					"Da crop thanh cong: "
							+ i
							+ "========================================================================");
		}
		Log.d(TAG, "thoi gian blockHome: " + (System.currentTimeMillis() - startHome));
		return lstItem;
	}
	
	public static Bitmap drawRectInEdit(Bitmap btm, RectItem rect) {
		Bitmap btmResult = btm.copy(Bitmap.Config.RGB_565, true);
		Canvas canvas = new Canvas(btmResult);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.parseColor("#F5A9BC"));
		paint.setAlpha(178);
		
		RectF rectF = new RectF();
		rectF.top = rect.getY();
		rectF.left = rect.getX();
		rectF.bottom = rect.getY() + rect.getHeight();
		rectF.right = rect.getX() + rect.getWidth();
		canvas.drawRect(rectF, paint);
		
		return btmResult;
	}

	public static MinMaxIntensity findMinAndMaxIntensity(Mat matGray, Rect rect) {
		int iRectX = rect.x;
		int iRectY = rect.y;
		int iRectW = rect.width;
		int iRectH = rect.height;

		int iIndexMaxX = -5;
		int iIndexMaxY = -5;
		int iIndexMinX = -6;
		int iIndexMinY = -6;
		int iMinIntensity = 257;
		int iMaxIntensity = -1;

		for (int y = iRectY; y < iRectY + iRectH - 1; y++) {
			for (int x = iRectX; x < iRectX + iRectW - 1; x++) {
				double[] dTmpIntensity = matGray.get(y, x);
				if (dTmpIntensity[0] > iMaxIntensity) {
					iIndexMaxX = x;
					iIndexMaxY = y;
					iMaxIntensity = (int) dTmpIntensity[0];
				}

				if (dTmpIntensity[0] < iMinIntensity) {
					iIndexMinX = x;
					iIndexMinY = y;
					iMinIntensity = (int) dTmpIntensity[0];
				}
			}
		}

		if (iIndexMaxX < 0 || iIndexMaxY < 0) {
			return null;
		}

		return new MinMaxIntensity(iIndexMaxX, iIndexMaxY, iMaxIntensity,
				iIndexMinX, iIndexMinY, iMinIntensity);
	}
	
	//test ham nay, sau khi delete co viet vao dc nua ko hay phai khoi tao lai
	public static void writeImagePH(String imagePath, Bitmap btmImage) {
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		btmImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] imageData = stream.toByteArray();
		
		File imageFile = new File(imagePath);
		
		if (!imageFile.exists()) {
			imageFile.delete();
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(imageFile);
			fos.write(imageData);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class MinMaxIntensity {
	int iMaxX;
	int iMaxY;
	int iMaxIntensity;

	int iMinX;
	int iMinY;
	int iMinIntensity;

	public MinMaxIntensity(int iMaxX, int iMaxY, int iMaxIntentsity, int iMinX,
			int iMinY, int iMinIntensity) {
		this.iMaxX = iMaxX;
		this.iMaxY = iMaxY;
		this.iMaxIntensity = iMaxIntentsity;

		this.iMinX = iMinX;
		this.iMinY = iMinY;
		this.iMinIntensity = iMinIntensity;
	}

}
