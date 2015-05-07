package com.stpproject.viewutil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class AsyncWriteImage extends AsyncTask<byte[], Void, File>{

	Context mContext;
	String  mImageFile; 
	
	public AsyncWriteImage(Context context) {
		this.mContext = context;
		this.mImageFile = null;
	}
	
	public AsyncWriteImage(Context context, String imageFile) {
		this.mContext = context;
		this.mImageFile = imageFile;
	}
	
	@Override
	protected File doInBackground(byte[]... params) {
		byte[] data = params[0];
		File pictureFile;
		if (mImageFile == null) {
			pictureFile = getOutputPhotoFile(); // check xem sao thoi diem nay no la cai gi debug
		} else {
			pictureFile = new File(mImageFile);
		}
		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			fos.write(data);
			fos.close();
		} catch (IOException ioEx) {
			return null;
		}
		
		return pictureFile;
	}
	
	@Override
	protected void onPostExecute(File result) {
		if (mImageFile == null) {
			if (result.exists()) {
				Intent intent = new Intent(Constant.ACTION_WRITEIMAGE_DONE_RECEIVER);
				Bundle bundle = new Bundle();
				bundle.putString(Constant.KEY_IMAGE_PATH_FOR_PROCESSING, result.getPath());
				intent.putExtras(bundle);
				mContext.sendBroadcast(intent);
			} else {
				Intent intent = new Intent(Constant.ACTION_WRITEIMAGE_DONE_RECEIVER);
				Bundle bundle = new Bundle();
				bundle.putString(Constant.KEY_IMAGE_PATH_FOR_PROCESSING, Constant.VALUE_NOT_EXISTED);
				intent.putExtras(bundle);
				mContext.sendBroadcast(intent);
			}
		}
		super.onPostExecute(result);
	}
	
	private File getOutputPhotoFile() {
		File directory = mContext.getFilesDir();
		//Log.d(TAG, "file fir: " + directory.getPath());
		File folderImage = new File(directory, "ImageContact");
		if (!folderImage.exists()) {
			folderImage.mkdirs();
		}
		
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", new Locale("vi", "VN"))
				.format(new Date());
		return new File(folderImage.getPath() + File.separator + "IMG_"
				+ timeStamp + ".jpg");
	}
}
