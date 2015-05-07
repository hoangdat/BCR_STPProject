package com.stpproject.imgprocessing;

import java.util.concurrent.ArrayBlockingQueue;

import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.WriteFile;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.stpproject.semantics.LineItem;

public class TaskRunOCR implements Runnable {

	public static final String TAG = "TaskRunOCR";

	private String nameTest;
	private LineItem lineItem;
	private String dataPath;
	private String lang;
	private int modePS;
	ArrayBlockingQueue<LineItem> lstLineResult;
	//TessBaseAPI tessBase;

	public int getModePS() {
		return modePS;
	}

	public void setModePS(int modePS) {
		this.modePS = modePS;
	}

	public String getDataPath() {
		return dataPath;
	}

	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getNameTest() {
		return nameTest;
	}

	public void setNameTest(String nameTest) {
		this.nameTest = nameTest;
	}

	public LineItem getLineItem() {
		return lineItem;
	}

	public void setLineItem(LineItem lineItem) {
		this.lineItem = lineItem;
	}

	public TaskRunOCR(String name, LineItem line, String dataPath, String lang,
			ArrayBlockingQueue<LineItem> lstItems) {
		setNameTest(name);
		setLineItem(line);
		setDataPath(dataPath);
		setLang(lang);
		lstLineResult = lstItems;
		//this.tessBase = tessBase;
	}

	public void init() {

	}

	@Override
	public void run() {
		Log.d(TAG, "thread TaskRUNOCR DATHPH: " + nameTest + " bat dau chay");
		

		Bitmap btm = MyUtilsImage.rgb2gray(lineItem.getBtmRegion());

		long start = System.currentTimeMillis();
		
		String result = new String();
		lineItem.setStringResult(new String());
		synchronized (ExecutorAsyncPH.tessBase) {
			
			ExecutorAsyncPH.tessBase.setImage(btm);
			
			result = ExecutorAsyncPH.tessBase.getUTF8Text();
			//ExecutorAsyncPH.tessBase.end();
		}
		
		
		if (!result.isEmpty()) {
			lineItem.setHaveResult(true);
			lineItem.setStringResult(result);
			if (lstLineResult != null) {
				Log.d(TAG, "add line Item vao List Line");
				lstLineResult.add(lineItem);
			} else {
				Log.d(TAG, "luong " + nameTest + " result not null but not added to List Line");
			}
		}
		long elappe = System.currentTimeMillis() - start; 
		Log.d(TAG, "da thuc hien xong luong: " + nameTest + "hien co: " + lstLineResult.size() + " thoi gian thuc hien: " + elappe);
	}

}
