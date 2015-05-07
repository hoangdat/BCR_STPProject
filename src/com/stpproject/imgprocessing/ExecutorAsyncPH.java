package com.stpproject.imgprocessing;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.bcrstpproject.ProcessingActivity;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.stpproject.semantics.LineItem;
import com.stpproject.viewutil.Constant;



public class ExecutorAsyncPH extends
		AsyncTask<ArrayList<LineItem>, String, ArrayBlockingQueue<LineItem>> {

	public static final String TAG = "ExecutorAsyncPH";
	public static final String DONE = "Done";
	private String datapath;
	private String lang;
	private int poolSize;
	private ExecutorService mPool;

	private ProcessingActivity mContext;
	public static TessBaseAPI tessBase = new TessBaseAPI();
//	private ProgressDialog mProgressDialog;

	public ExecutorAsyncPH(Context context, int poolSize, String dataPath, String lang) {
		this.datapath = dataPath;
		this.lang = lang;
		this.poolSize = poolSize;
		mContext = (ProcessingActivity)context;
	}

	@Override
	protected void onPreExecute() {
//		mProgressDialog = new ProgressDialog(mContext);
//		mProgressDialog.setMessage("Analyzing ...");
//		mProgressDialog.setCanceledOnTouchOutside(false);
//		mProgressDialog.setCancelable(false);
//		mProgressDialog.show();
		super.onPreExecute();
	}

	@Override
	protected ArrayBlockingQueue<LineItem> doInBackground(
			ArrayList<LineItem>... params) {
		long startInit = System.currentTimeMillis();
		
		
		tessBase.setDebug(true);
		tessBase.init(this.datapath, this.lang);
		tessBase.setPageSegMode(1);
		long elapssed = System.currentTimeMillis() - startInit;
		
		Log.d(TAG, "thoi gian init la: " + elapssed);
		
		long startAsyncExecutor = System.currentTimeMillis();
		ArrayList<LineItem> lstItemIN = (ArrayList<LineItem>) params[0];
		int sizeIN = lstItemIN.size();
		ArrayBlockingQueue<LineItem> lstResultOUT = new ArrayBlockingQueue<LineItem>(sizeIN);
		mPool = Executors.newFixedThreadPool(poolSize);

		for (int i = 0; i < sizeIN; i++) {
			String name = "task " + i;
			Log.d(TAG, name + " chuan bi submit");
			LineItem lineItem = lstItemIN.get(i);
			
			mPool.submit(new TaskRunOCR(name, lineItem, datapath, lang, lstResultOUT));
			Log.d(TAG, name + " da duoc submit");
		}

		mPool.shutdown();
		Log.d(TAG, "----------------------------------------dang cho");
		try {
			boolean a = mPool.awaitTermination(20, TimeUnit.SECONDS);
			if (a) {
				Log.d(TAG,
						"Cac task da thuc hien xong truoc timeOut, so luong ket qua: "
								+ lstResultOUT.size());
			} else {
				Log.d(TAG, "Cac task bi ngat vi timeOut");
			}
			
			long elapsedExe = System.currentTimeMillis() - startAsyncExecutor;
			mContext.mEllapsedExecutor = elapsedExe;
			Log.d("===================total THoi gian+++++++++=", "thoi gian thuc hien executorAsync: " + elapsedExe);
			tessBase.end();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return lstResultOUT;
	}
	
	@Override
	protected void onPostExecute(ArrayBlockingQueue<LineItem> result) {
//		if (mProgressDialog != null && mProgressDialog.isShowing()) {
//			try {
//				mProgressDialog.dismiss();
//				mProgressDialog = null;
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//		}
		
		mContext.mListResultLine = result;
		Log.d(TAG, "onPostExecute");
		Intent intent = new Intent(Constant.ACTION_TESS_RESULT_RECEIVER);
		intent.putExtra(DONE, true);
		mContext.sendBroadcast(intent);
		super.onPostExecute(result);
	}
}
