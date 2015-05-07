package com.stpproject.viewutil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.bcrstpproject.MainActivity;
import com.stpproject.imgprocessing.MyUtilsImage;
import com.stpproject.logutil.TestUtilities;

public class AsyncGetImageFromGallery extends AsyncTask<String, Void, File>{
	public static final String TAG = "AsyncGetImageFromGallery";
	
	MainActivity mContext;
	private ProgressDialog mProgressDialog;
	
	public AsyncGetImageFromGallery(Context context) {
		mContext = (MainActivity)context;
	}
	
	@Override
	protected void onPreExecute() {
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage("Retrieving ...");
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
		super.onPreExecute();
	}
	
	@Override
	protected File doInBackground(String... params) {
		String imgString = params[0];
		if (imgString == null) return null;
		
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imgString, options);
		
		options.inSampleSize = MyUtilsImage.calculateInSampleSize(options, 1920, 1080);
		
		options.inJustDecodeBounds = false;
		Bitmap btm = BitmapFactory.decodeFile(imgString, options);
		Bitmap btmScale = Bitmap.createScaledBitmap(btm, 1024, 600, true);
		btm.recycle();
		File fileImg = getOutputPhotoFile();
		ByteArrayOutputStream bOS = new ByteArrayOutputStream();
		btmScale.compress(Bitmap.CompressFormat.JPEG, 100, bOS);
		byte[] data = bOS.toByteArray();
		FileOutputStream fos = null;
		Log.d(TAG, "data ghi file anh: " + data.length);
		try {
			fos = new FileOutputStream(fileImg);
			fos.write(data);
			fos.close();
		} catch (FileNotFoundException e) {
			TestUtilities.writeLog(ManagerSystemGlobal.gloPathFileLog, e.getMessage());
			return null;
		} catch (IOException e) {
			TestUtilities.writeLog(ManagerSystemGlobal.gloPathFileLog, e.getMessage());
			return null;
		}
		btmScale.recycle();
		return fileImg;
	}
	
	private File getOutputPhotoFile() {
		File directory = mContext.getFilesDir();
//		Log.d(TAG, "file fir: " + directory.getPath());
		File folderImage = new File(directory, "ImageContact");
		if (!folderImage.exists()) {
			folderImage.mkdirs();
		}
		

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", new Locale("vi", "VN"))
				.format(new Date());
		return new File(folderImage.getPath() + File.separator + "IMG_"
				+ timeStamp + ".jpg");
	}
	
	@Override
	protected void onPostExecute(File result) {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			try {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		if (result != null && result.length() > 0) {
			Intent intent = new Intent(Constant.ACTION_RETRIEVE_IMAGE_RECEIVER);
			Bundle  bundle = new Bundle();
			bundle.putString(Constant.KEY_IMAGE_PATH_FOR_PROCESSING, result.getPath());
			intent.putExtras(bundle);
			mContext.sendBroadcast(intent);
		} else {
			Intent intent = new Intent(Constant.ACTION_RETRIEVE_IMAGE_RECEIVER);
			Bundle  bundle = new Bundle();
			bundle.putString(Constant.KEY_IMAGE_PATH_FOR_PROCESSING, Constant.VALUE_NOT_EXISTED);
			intent.putExtras(bundle);
			mContext.sendBroadcast(intent);
		}
		super.onPostExecute(result);
	}
}
