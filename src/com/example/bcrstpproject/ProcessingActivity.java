package com.example.bcrstpproject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.stpproject.imgprocessing.BitmapSystem;
import com.stpproject.imgprocessing.CopyFileAsyncTask;
import com.stpproject.imgprocessing.ExecutorAsyncPH;
import com.stpproject.imgprocessing.FindLineP;
import com.stpproject.imgprocessing.ImageBorder;
import com.stpproject.imgprocessing.MyUtilsImage;
import com.stpproject.imgprocessing.ValueOpenCv;
import com.stpproject.model.DataItem;
import com.stpproject.semantics.LineItem;
import com.stpproject.semantics.SemanticAnalysis;
import com.stpproject.viewutil.AsyncWriteImage;
import com.stpproject.viewutil.Constant;
import com.stpproject.viewutil.ManagerSystemGlobal;

public class ProcessingActivity extends Activity {
	
	static {
		if (!OpenCVLoader.initDebug()) {
			Log.i("Loi load libs OpenCV", "Loi roi");
		}
	}
	
	public static final String TAG = "ProcessingActivity";

	private String             mStrImagePath  = new String();
	private Bitmap             mBtmPattern;
	public  ImageView          imgViewPattern;
	
	
	public  long               mEllapsedPreProcessing = 0;
	public  long               mEllapsedExecutor      = 0;
	
	public ArrayBlockingQueue<LineItem> mListResultLine;
	TessResultReceiver myReceiver;
	
	
	@Override
	protected void onCreate(Bundle openBundle) {
		super.onCreate(openBundle);
		setContentView(R.layout.opencv);
		
		
		getData();
		initUI();
		
		myReceiver = new TessResultReceiver();
//	    TestBlockOtsu_preProcess();
	    //thread de tao hieu ung cho view
		TestBlockOtsu();
		//finish();
	}
	
	@Override
	protected void onResume() {
		IntentFilter intentFil = new IntentFilter();
		intentFil.addAction(Constant.ACTION_TESS_RESULT_RECEIVER);
		registerReceiver(myReceiver, intentFil);
		super.onResume();
	}
	@Override
	protected void onPause() {
		unregisterReceiver(myReceiver);
		super.onPause();
	}
	
	public void initUI() {
		imgViewPattern = (ImageView)findViewById(R.id.imgViewEffectWhileProcessing);
	}
	
	
	public void getData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mStrImagePath = bundle.getString(Constant.KEY_IMAGE_PATH_FOR_PROCESSING, null);
		
		if (mStrImagePath != null) {
			Options opts = new Options();
			opts.inMutable = true;
			mBtmPattern = BitmapFactory.decodeFile(mStrImagePath, opts); 
		}
	}
	
	public void TestBlockOtsu() {
		long start_1 = System.currentTimeMillis();
		Bitmap btm = mBtmPattern;
		
		Mat matIN = new Mat();
		Mat matGray = new Mat();
		Utils.bitmapToMat(btm, matIN);

		Mat matINresize = ImageBorder.resizeMat(matIN);
		long endResize = System.currentTimeMillis();
		Log.d(TAG, "thoi gian resize Image: "
				+ (System.currentTimeMillis() - start_1));
		Mat matCanny = FindLineP.findCany(matINresize);
		long endCanny = System.currentTimeMillis();
		Log.d(TAG, "thoi gian canny Image: "
				+ (System.currentTimeMillis() - endResize));
		Mat matCard = FindLineP.findLineFull(matINresize, matCanny);
		
		Bitmap btmOverride = Bitmap.createBitmap(matCard.width(), matCard.height(), Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(matCard, btmOverride);
		
		if (btmOverride != null) {
			//MyUtilsImage.writeImagePH(mStrImagePath, btmOverride);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			btmOverride.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			byte[] imageData = stream.toByteArray();
			AsyncWriteImage asyncWrite = new AsyncWriteImage(getApplicationContext(), mStrImagePath);
			asyncWrite.execute(imageData);
		}
		
		Log.d(TAG, "thoi gian findline Image: "
				+ (System.currentTimeMillis() - endCanny));
		Imgproc.cvtColor(matCard, matGray, Imgproc.COLOR_RGB2GRAY);


		long startDetectBlock = System.currentTimeMillis();
		ArrayList<Rect> lstBox = MyUtilsImage.detectTextBlock(matGray, 
								 MyUtilsImage.WIDTH_SE_MORPHOLOGY, MyUtilsImage.HEIGHT_SE_MORPHOLOGY);
		
		if (lstBox.isEmpty()) {
			popUpDialogError();
			return;
		}
		
		long elapsedDetect = System.currentTimeMillis() - startDetectBlock;
		Log.d(TAG, "thoi gian detectblock: " + elapsedDetect);

		long startCropImage = System.currentTimeMillis();
		ArrayList<LineItem> listLine = MyUtilsImage.cropBinarizingImageOtsu(matGray, lstBox);
		if (listLine.isEmpty()) {
			popUpDialogError();
			return;
		}
		long elapsedCropImg = System.currentTimeMillis() - startCropImage;
		Log.d(TAG, "thoi gian crop Image: " + elapsedCropImg);
		Log.d(TAG, "size detected: " + listLine.size());
		mListResultLine = new ArrayBlockingQueue<LineItem>(listLine.size());

		SharedPreferences share_pref = getSharedPreferences(Constant.PREFERENCE_FILE_NAME, MODE_PRIVATE);
		boolean b  = share_pref.getBoolean(Constant.KEY_IS_DATA_COPIED, false);
		if (!b) {
			CopyFileAsyncTask.createTessDataNotAsync();
		}
		String dataPath = share_pref.getString(Constant.KEY_PATH_TESS_DATA, 
							getFilesDir().getAbsolutePath() + Constant.DEFAULT_TESS_DATA_PATH_PARENT);
		String lang     = share_pref.getString(Constant.KEY_LANG_TESS, "vie");

		mEllapsedPreProcessing = System.currentTimeMillis() - start_1;
		
		ExecutorAsyncPH executor = new ExecutorAsyncPH(this, 5, dataPath, lang);
		executor.execute(listLine);
		
		RoleImage(matINresize, matCard);
	}
	
	
	private void RoleImage(Mat m1,Mat m2) {
		
		Log.d("duydeptrai", "mat 1 he= " + m1.height() +" wi=" +m1.width());
		Log.d("duydeptrai", "mat 2 he= " + m2.height() +" wi=" +m2.width());
		float xG=ValueOpenCv.xG;
		float yG=ValueOpenCv.yG;
		float degre=ValueOpenCv.degre;
		float zoom=ValueOpenCv.zoom;
		Log.d("duydeptrai", "xG= " + xG +" yG="+yG+" degre="+degre +" zoom="+ zoom);
//		Bitmap btm2 = mBtmPattern;
		final LinearLayout liImage = (LinearLayout) findViewById(R.id.liImage);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
//		int height = size.y;
		int wightshow = width;
		int heightshow = width * 600/1024;
		Log.d("duydeptrai", "heshow= " + wightshow +" wishow=" +heightshow);
		
		Mat m1resize=BitmapSystem.resizeMat(m1, wightshow, heightshow);
		Mat m2resize=BitmapSystem.resizeMat(m2, wightshow, heightshow);
		Bitmap b1show=BitmapSystem.mattobitmap(m1resize);
		final Bitmap b2show=BitmapSystem.mattobitmap(m2resize);
		
		imgViewPattern.setImageBitmap(b1show);
		
		
		long timeAnima = 3000;
		float pivotX = xG / 1024 * wightshow;
		float pivotY = yG / 600 * heightshow;
        
		Log.d("duydeptrai", "pivotX= " + pivotX+" pivotY="+pivotY+" degre="+degre +" zoom="+ zoom);
		
		if (zoom != 0) {
			ScaleAnimation animaScale = new ScaleAnimation(1.0f, zoom, 1.0f,
					zoom, pivotX, pivotY);
			RotateAnimation animaRota = new RotateAnimation(0, degre, pivotX,
					pivotY);
			AlphaAnimation animaAlpha = new AlphaAnimation(1, 0.1f);
			final AnimationSet sets = new AnimationSet(false);

			sets.addAnimation(animaScale);
			sets.addAnimation(animaRota);
			sets.addAnimation(animaAlpha);
			sets.setDuration(timeAnima);
			imgViewPattern.startAnimation(sets);

			//
			sets.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					imgViewPattern.setImageBitmap(b2show);
					imgViewPattern.setAlpha(0.8f);
					
					final AnimationSet sets2 = new AnimationSet(false);
					AlphaAnimation animaAlpha2 = new AlphaAnimation(0.2f, 1f);
					sets2.addAnimation(animaAlpha2);
					sets2.setDuration(1000);
					liImage.setAnimation(sets2);
					ProgressBar p1=(ProgressBar)findViewById(R.id.progressBar1);
					p1.setVisibility(View.VISIBLE);
					TextView tv=(TextView)findViewById(R.id.tvWait);
					tv.setVisibility(View.VISIBLE);
				}
			});
		} else {
			imgViewPattern.setImageBitmap(b2show);
			imgViewPattern.setAlpha(0.8f);
			ProgressBar p1=(ProgressBar)findViewById(R.id.progressBar1);
			p1.setVisibility(View.VISIBLE);
			TextView tv=(TextView)findViewById(R.id.tvWait);
			tv.setVisibility(View.VISIBLE);
		}
      	
	}
	
	private void popUpDialogError() {
		final AlertDialog.Builder popUpDialog = new AlertDialog.Builder(this);
		popUpDialog.setMessage("Please take a photo to scan again.");
		popUpDialog.setTitle("Image invalid!");
		
		popUpDialog.setPositiveButton("Go to Camera", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(ProcessingActivity.this, AndroidCamera.class);
				startActivity(intent);
				finish(); //khong co cai nay toan loi
			}
		});
		
		popUpDialog.setNegativeButton("Go Home", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		
		popUpDialog.create();
		popUpDialog.show();
	}


	public class TessResultReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			ManagerSystemGlobal.gloSendData = new DataItem();
			Log.d(TAG, "thoi gian tien xu ly: " + mEllapsedPreProcessing + " thoi gian Executor: " + mEllapsedExecutor 
					+ " ||||Tong thoi gian: " + (mEllapsedExecutor + mEllapsedPreProcessing));
//			Toast.makeText(getApplicationContext(), "thoi gian tien xu ly: " + mEllapsedPreProcessing + " thoi gian Executor: " + mEllapsedExecutor 
//					+ " ||||Tong thoi gian: " + (mEllapsedExecutor + mEllapsedPreProcessing), Toast.LENGTH_LONG).show();
			Log.d(TAG, "vao receiver roi");
			boolean isDone = intent
					.getBooleanExtra(ExecutorAsyncPH.DONE, false);
			Log.d(TAG, "so luong result " + mListResultLine.size());
			if (isDone && (!mListResultLine.isEmpty())) {
				Log.d(TAG, "vao isDone roi");
				long startSort = System.currentTimeMillis();
				ArrayList<LineItem> lstBox = MyUtilsImage.sortLineItems(mListResultLine);
				long elapsed_s = System.currentTimeMillis() - startSort;
				Log.d(TAG, "thoi gian sort la: " + elapsed_s);
//				 in log
//				 for (int i = 0; i < lstBox.size(); i++) {
//					 Log.d(TAG, i + ") " + lstBox.get(i).getStringResult());
//				 }
//				 in log
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inMutable = true;
//				Bitmap btm = BitmapFactory.decodeFile(mStrImagePath, opt);
				// mPictureGetInImageViewPath, opt);
//				Bitmap btm = mBtmCardCuted.copy(Bitmap.Config.ARGB_8888, true);
//				Bitmap btmDraw = MyUtilsImage.drawListItems(btm, lstBox);
				long startSemantic = System.currentTimeMillis();
				SemanticAnalysis semanticAnalysis = new SemanticAnalysis(lstBox);
				DataItem dataItem = semanticAnalysis.processSemantic();
				long elapssed = System.currentTimeMillis() - startSemantic;
				Log.d(TAG, "thoi gian semanticAnalysis: " + elapssed);
				
				//create contact
				Log.d("Tag", "name: " + ((dataItem.getNameCard() != null) ? dataItem.getNameCard():"khong biet"));
				
				ManagerSystemGlobal.gloSendData = dataItem;
				Intent intent_sendEdit = new Intent(ProcessingActivity.this, EditContactsActivity.class);
				intent_sendEdit.putExtra(Constant.KEY_SEND_FROM, Constant.VALUE_SEND_FROM_PROCESS);
				Bundle bundle = new Bundle();
				bundle.putString(Constant.KEY_IMAGE_PATH_FOR_PROCESSING, mStrImagePath);
				intent_sendEdit.putExtras(bundle);
				finish();
				startActivity(intent_sendEdit);
				
				//Toast.makeText(getApplicationContext(), "name: " + ((dataItem.getNameCard() != null) ? dataItem.getNameCard():"khong biet"), Toast.LENGTH_SHORT).show();
			} else {
				popUpDialogError();
			}
		}
	}
	
	@Override
	protected void onStop() {
		Log.d(TAG, "Finish xem co goi dcuoc ko");
		if (mBtmPattern != null) {
			mBtmPattern.recycle();
			mBtmPattern = null;
		}
		System.gc();
		imgViewPattern.setImageDrawable(null);
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		if (mBtmPattern != null) {
			mBtmPattern.recycle();
			mBtmPattern = null;
			System.gc();
		}
		imgViewPattern.setImageDrawable(null);
		System.gc();
		Log.d(TAG, "Finish destroy xem co goi dcuoc ko");
		super.onDestroy();
	}
	
	
}
