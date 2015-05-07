package com.stpproject.imgprocessing;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

public class BitmapSystem {


        public static Mat resizeMat(Mat mat) {
		Mat matresult = new Mat();
		Imgproc.resize(mat, matresult, new Size(1024, 600), 0, 0,
				Imgproc.INTER_CUBIC);
		return matresult;
	}
	public static Mat resizeMat(Mat mat,int wi,int he) {
		Mat matresult = new Mat();
		Imgproc.resize(mat, matresult, new Size(wi, he), 0, 0,
				Imgproc.INTER_CUBIC);
		return matresult;
	}


	public static Mat bitmaptomat(Bitmap bmp) {
		Mat matConverted = new Mat();
		Utils.bitmapToMat(bmp, matConverted);
		if (matConverted.empty()) {
			throw new NullPointerException("Mat converted is null");
		}
		return matConverted;

	}

	public static Bitmap mattobitmap(Mat ImageMat) {
		Bitmap resultBitmap = Bitmap.createBitmap(ImageMat.cols(),
				ImageMat.rows(), Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(ImageMat, resultBitmap);
		return resultBitmap;

	}
	public Bitmap resizedBitmap(Bitmap bm, int newWidth, int newHeight) {
	    int width = bm.getWidth();
	    int height = bm.getHeight();
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    // CREATE A MATRIX FOR THE MANIPULATION
	    Matrix matrix = new Matrix();
	    // RESIZE THE BIT MAP
	    matrix.postScale(scaleWidth, scaleHeight);

	    // "RECREATE" THE NEW BITMAP
	    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	    return resizedBitmap;
	}

}
