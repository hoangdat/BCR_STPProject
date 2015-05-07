package com.example.bcrstpproject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.stpproject.viewutil.AsyncWriteImage;
import com.stpproject.viewutil.Constant;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AndroidCamera extends Activity implements SurfaceHolder.Callback {
	
	public static final String TAG = "AndroidCamera";
	
	Context  			  mCameracontext;
	Camera                mCamera;
	CameraSurfaceView     mCameraSurfaceView;

	private SurfaceHolder mSurfaceHolder;
	boolean               previewing = false;
	LayoutInflater        mControlInflater = null;
	ImageView             btFocus, btFlash;
	Button                btTakePicture;
	Boolean               isFocus = true;
	Boolean               isAuto = true;
	ToggleButton          toggleLangue;
	
	SaveFileDoneReceiver mSaveReceiver;
	
	
	String                mCurrentPhotoPath;
	
	int                   i1 = 1, i2 = 1; //khong hieu
	
	LinearLayout          mLnrFocus, mLnrFlash;
	RelativeLayout        mRelativeFocus_on, mRelativeFocus_off, mRelativeFocus_vocuc, mRelativeFlash_off,
						  mRelativeFlash_on, mRelativeFlash_auto, mRelativeFlash, mRelativeFocus;
	
	public static DrawingView mDrawingView;
	public static boolean touch1 = true;
	public static boolean touch2 = true;
	
	public  static MediaPlayer mpBeep;
    public  static MediaPlayer mpTach;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_camera);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		getWindow().setFormat(PixelFormat.UNKNOWN);
		mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.camerapreview);
		mSurfaceHolder = mCameraSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mSaveReceiver = new SaveFileDoneReceiver();
		
		mDrawingView = new DrawingView(this);
		LayoutParams layoutParamsDrawing = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.addContentView(mDrawingView, layoutParamsDrawing);
		
		mpBeep = MediaPlayer.create(this, R.raw.beep);
        mpTach = MediaPlayer.create(this, R.raw.tach);

		mControlInflater = LayoutInflater.from(getBaseContext());
		View viewControl = mControlInflater.inflate(R.layout.control, null);
		LayoutParams layoutParamsControl = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.addContentView(viewControl, layoutParamsControl);

		mLnrFlash = (LinearLayout) findViewById(R.id.liFlash);
		mLnrFocus = (LinearLayout) findViewById(R.id.liFocus);
		mLnrFlash.setVisibility(View.GONE);
		mLnrFocus.setVisibility(View.GONE);
		mRelativeFlash_auto = (RelativeLayout) findViewById(R.id.Relaflash_auto);
		mRelativeFlash_off = (RelativeLayout) findViewById(R.id.Relaflash_off);
		mRelativeFlash_on = (RelativeLayout) findViewById(R.id.Relaflash_on);
		mRelativeFocus_on = (RelativeLayout) findViewById(R.id.Relafocus_on);
		mRelativeFocus_off = (RelativeLayout) findViewById(R.id.Relafocus_off);
		mRelativeFocus_vocuc = (RelativeLayout) findViewById(R.id.Relafocus_vocuc);
		toggleLangue=(ToggleButton)findViewById(R.id.toggleButtonLangue);

		mRelativeFlash_on.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Camera.Parameters parameters = mCamera.getParameters();
				parameters.setFlashMode(Parameters.FLASH_MODE_ON);
				mCamera.setParameters(parameters);
				refrescamrera();
				mLnrFlash.setVisibility(View.GONE);
				btFlash.setBackgroundResource(R.drawable.flash_on);
				mDrawingView.setHaveTouch(false, true, null);
				mDrawingView.invalidate();
			}
		});
		mRelativeFlash_off.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Camera.Parameters parameters = mCamera.getParameters();
				parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
				mCamera.setParameters(parameters);
				refrescamrera();
				mLnrFlash.setVisibility(View.GONE);
				btFlash.setBackgroundResource(R.drawable.flash_off);
				mDrawingView.setHaveTouch(false, true, null);
				mDrawingView.invalidate();
			}
		});

		mRelativeFlash_auto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Camera.Parameters parameters = mCamera.getParameters();
				parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
				mCamera.setParameters(parameters);
				refrescamrera();
				mLnrFlash.setVisibility(View.GONE);
				btFlash.setBackgroundResource(R.drawable.flash_auto);
				mDrawingView.setHaveTouch(false, true, null);
				mDrawingView.invalidate();

			}
		});

		mRelativeFocus_on.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mLnrFocus.setVisibility(View.GONE);
				isAuto = true;
				btFocus.setBackgroundResource(R.drawable.focus_macro);
				mDrawingView.setHaveTouch(false, true, null);
				mDrawingView.invalidate();

				int a = mDrawingView.getWidth();
				int b = mDrawingView.getHeight();
				float x = a / 2;
				float y = b / 2;
				float touchMajor = 300;
				float touchMinor = 300;
				Rect tfocusRect = new Rect((int) (x - touchMajor / 2),
						(int) (y - touchMinor / 2), (int) (x + touchMajor / 2),
						(int) (y + touchMinor / 2));
				Rect targetFocusRect = new Rect(tfocusRect.left * 2000 //final 
						/ mDrawingView.getWidth() - 1000, tfocusRect.top * 2000
						/ mDrawingView.getHeight() - 1000, tfocusRect.right
						* 2000 / mDrawingView.getWidth() - 1000,
						tfocusRect.bottom * 2000 / mDrawingView.getHeight()
								- 1000);
				final List<Camera.Area> focusList = new ArrayList<Camera.Area>();
				Camera.Area focusArea = new Camera.Area(targetFocusRect, 1000);
				focusList.add(focusArea);
				Parameters para = mCamera.getParameters();
				para.setFocusMode(Parameters.FLASH_MODE_AUTO);
				para.setFocusAreas(focusList);
				para.setMeteringAreas(focusList);
				mCamera.setParameters(para);
				mCamera.autoFocus(myAutoFocusCallback);
				// drawingView.setHaveTouch(true,false, tfocusRect);
				mDrawingView.invalidate();
				btFocus.setBackgroundResource(R.drawable.focus_auto);

			}
		});
		mRelativeFocus_off.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mLnrFocus.setVisibility(View.GONE);
				btFocus.setBackgroundResource(R.drawable.focus_macro);
				mDrawingView.setHaveTouch(false, true, null);
				mDrawingView.invalidate();

				isAuto = false;
				Camera.Parameters parameters = mCamera.getParameters();
				parameters.setFocusMode(Parameters.FOCUS_MODE_MACRO);
				mCamera.setParameters(parameters);
				refrescamrera();

			}
		});
		mRelativeFocus_vocuc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mLnrFocus.setVisibility(View.GONE);
				btFocus.setBackgroundResource(R.drawable.focus_vocuc);
				mDrawingView.setHaveTouch(false, true, null);
				mDrawingView.invalidate();

				isAuto = false;
				Camera.Parameters parameters = mCamera.getParameters();
				parameters.setFocusMode(Parameters.FOCUS_MODE_INFINITY);
				mCamera.setParameters(parameters);
				refrescamrera();

			}
		});

		btTakePicture = (Button) findViewById(R.id.btTakepicture);
		btTakePicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mLnrFlash.setVisibility(View.GONE);
				mLnrFocus.setVisibility(View.GONE);
				if (isAuto) {

					int a = mDrawingView.getWidth();
					int b = mDrawingView.getHeight();
					float x = a / 2;
					float y = b / 2;
					float touchMajor = 300;
					float touchMinor = 300;
					Rect tfocusRect = new Rect((int) (x - touchMajor / 2),
							(int) (y - touchMinor / 2),
							(int) (x + touchMajor / 2),
							(int) (y + touchMinor / 2));
					final Rect targetFocusRect = new Rect(tfocusRect.left
							* 2000 / mDrawingView.getWidth() - 1000,
							tfocusRect.top * 2000 / mDrawingView.getHeight()
									- 1000, tfocusRect.right * 2000
									/ mDrawingView.getWidth() - 1000,
							tfocusRect.bottom * 2000 / mDrawingView.getHeight()
									- 1000);
					final List<Camera.Area> focusList = new ArrayList<Camera.Area>();
					Camera.Area focusArea = new Camera.Area(targetFocusRect,
							1000);
					focusList.clear();
					focusList.add(focusArea);
					Parameters para = mCamera.getParameters();
					para.setFocusMode(Parameters.FLASH_MODE_AUTO);
					para.setFocusAreas(focusList);
					para.setMeteringAreas(focusList);
					mCamera.setParameters(para);
					mCamera.autoFocus(myAutoFocusCallback);
					mDrawingView.setHaveTouch(true, false, tfocusRect);
					mDrawingView.invalidate();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mpTach.start();
					mCamera.takePicture(null, null, myPictureCallback_JPG);
				} else {
					mCamera.takePicture(null, null, myPictureCallback_JPG);
				}
			}
		});

		btFocus = (ImageView) findViewById(R.id.btFocus);
		mRelativeFocus = (RelativeLayout) findViewById(R.id.reFocus);
		mRelativeFocus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (touch1) {
					mLnrFlash.setVisibility(View.GONE);
					mLnrFocus.setVisibility(View.VISIBLE);
					mDrawingView.setHaveTouch(false, false, null);
					mDrawingView.invalidate();
					touch1 = false;
					touch2=true;
				} else {
					mLnrFlash.setVisibility(View.GONE);
					mLnrFocus.setVisibility(View.GONE);
					mDrawingView.setHaveTouch(false, true, null);
					mDrawingView.invalidate();
					touch1 = true;
					touch2=true;
				}

			}
		});

		btFlash = (ImageView) findViewById(R.id.btFlash);
		mRelativeFlash = (RelativeLayout) findViewById(R.id.reFlash);
		mRelativeFlash.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				if (touch2) {
					mLnrFocus.setVisibility(View.GONE);
					mLnrFlash.setVisibility(View.VISIBLE);
					mDrawingView.setHaveTouch(false, false, null);
					mDrawingView.invalidate();
					touch2 = false;
					touch1=true;
				} else {
					mLnrFlash.setVisibility(View.GONE);
					mLnrFocus.setVisibility(View.GONE);
					mDrawingView.setHaveTouch(false, true, null);
					mDrawingView.invalidate();
					touch2 = true;
					touch1=true;
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constant.ACTION_WRITEIMAGE_DONE_RECEIVER);
		registerReceiver(mSaveReceiver, intentFilter);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		unregisterReceiver(mSaveReceiver);
		super.onPause();
	}

	public void touchFocus(final Rect tfocusRect) {
		mLnrFlash.setVisibility(View.GONE);
		mLnrFocus.setVisibility(View.GONE);

		if (isFocus == true) {
			try {
				final Rect targetFocusRect = new Rect(tfocusRect.left * 2000
						/ mDrawingView.getWidth() - 1000, tfocusRect.top * 2000
						/ mDrawingView.getHeight() - 1000, tfocusRect.right
						* 2000 / mDrawingView.getWidth() - 1000,
						tfocusRect.bottom * 2000 / mDrawingView.getHeight()
								- 1000);

				final List<Camera.Area> focusList = new ArrayList<Camera.Area>();
				Camera.Area focusArea = new Camera.Area(targetFocusRect, 1000);

				focusList.add(focusArea);

				Parameters para = mCamera.getParameters();
				para.setFocusMode(Parameters.FLASH_MODE_AUTO);
				para.setFocusAreas(focusList);
				para.setMeteringAreas(focusList);
				mCamera.setParameters(para);

				mCamera.autoFocus(myAutoFocusCallback);

				mDrawingView.setHaveTouch(true, false, tfocusRect);
				mDrawingView.invalidate();
				Log.i("CameraAutoFocus", "touchFocus");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback() {

		@Override
		public void onAutoFocus(boolean arg0, Camera arg1) {

			if (arg0) {

				Log.i("CameraAutoFocus", "onAutoFocus");

			}

			float focusDistances[] = new float[3];
			arg1.getParameters().getFocusDistances(focusDistances);

			mDrawingView.setHaveTouch(false, true, null);

			mDrawingView.invalidate();
		}
	};

	ShutterCallback myShutterCallback = new ShutterCallback() {

		@Override
		public void onShutter() {
			Log.i("CameraAutoFocus", "onShutter");

		}
	};

	PictureCallback myPictureCallback_JPG = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

//			File pictureFile = getOutputPhotoFile();
//			try {
//				
//				//thuc hien viec nay async 
//				FileOutputStream fos = new FileOutputStream(pictureFile);
//				fos.write(data);
//				fos.close();
//				
//				Intent intent = new Intent(getApplicationContext(),
//						ProcessingActivity.class);
//				Bundle bundle  = new Bundle();
//				bundle.putCharSequence(Constant.KEY_IMAGE_PATH_FOR_PROCESSING, pictureFile.getPath());
//				intent.putExtras(bundle);
//				startActivity(intent);
//				finish();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			AsyncWriteImage asyncWrite = new AsyncWriteImage(getApplicationContext());
			asyncWrite.execute(data);
			//thuc hien ve cai man hinh camera de thong bao
			//mCamera.startPreview();
			//Toast.makeText(getApplicationContext(), "Da chup anh!Sau nay se lam hieu ung", Toast.LENGTH_SHORT).show();
		}
	};

	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if (isFocus) {
			requestCameraAutoFocusMode();
		}
		if (previewing) {

			mCamera.stopPreview();
			previewing = false;
		}

		if (mCamera != null) {
			try {
				mCamera.setPreviewDisplay(mSurfaceHolder);
				mCamera.startPreview();

				previewing = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void requestCameraAutoFocusMode() {
		Camera.Parameters params2 = mCamera.getParameters();
		params2.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		mCamera.setParameters(params2);
		Log.i("CameraAutoFocus", "requestCameraAutoFous2");

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
		if (isFocus) {
			requestCameraAutoFocusMode();
			Log.i("CameraAutoFocus", "surfaceCreated");
		}
		// // camera.setFaceDetectionListener(faceDetectionListener);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
		previewing = false;
		Log.i("CameraAutoFocus", "surfaceDestroyed");
	}

	public void refrescamrera() {
		if (mSurfaceHolder.getSurface() == null) {
			// preview surface does not exist
			mCamera = null;
			mCamera.release();
			return;
		}
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
			mCamera.startPreview();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();

		}
		Log.i("CameraAutoFocus", "refrescamrera");
	}

	public void setCamera(Camera camera) {
		//camera = camera;
		mCamera = camera;
	}

	class DrawingView extends View {

		Paint drawingPaint, drawingKhung;

		public boolean haveTouch = false;

		public boolean haveKhung = true;
		Rect touchArea;

		public DrawingView(Context context) {
			super(context);

			drawingPaint = new Paint();
			drawingPaint.setColor(Color.GREEN);
			drawingPaint.setStyle(Paint.Style.STROKE);
			drawingPaint.setStrokeWidth(2);

			// add khung
			drawingKhung = new Paint();
			drawingKhung.setColor(Color.RED);
			drawingKhung.setStyle(Paint.Style.STROKE);
			drawingKhung.setStrokeWidth(5);
			//
			// haveTouch = false;
			// haveKhung=true;
		}

		public void setHaveTouch(boolean t, boolean h, Rect tArea) {
			haveTouch = t;
			haveKhung = h;
			touchArea = tArea;
		}

		@Override
		protected void onDraw(Canvas canvas) {

			if (haveTouch) {
				// drawingPaint.setColor(Color.BLUE);
				drawingPaint.setColor(Color.parseColor("#04aed9"));
				canvas.drawRect(touchArea.left, touchArea.top, touchArea.right,
						touchArea.bottom, drawingPaint);
				mpBeep.start();
			}
			if (haveKhung) {
				// add khung
				drawingKhung.setColor(Color.parseColor("#04aed9"));
				drawingKhung.setStyle(Paint.Style.STROKE);
				drawingKhung.setStrokeWidth(5);
				int a = mDrawingView.getWidth();
				int b = mDrawingView.getHeight();
				int l = b / 5;
				int t = 10;
				canvas.drawLine(a / t, b / t, a / t + l, b / t, drawingKhung);
				canvas.drawLine(a / t, b / t, a / t, b / t + l, drawingKhung);

				canvas.drawLine(a / t, b * (t - 1) / t - l, a / t, b * (t - 1)
						/ t, drawingKhung);
				canvas.drawLine(a / t, b * (t - 1) / t, a / t + l, b * (t - 1)
						/ t, drawingKhung);

				canvas.drawLine(a * (t - 2) / t - l, b * (t - 1) / t, a
						* (t - 2) / t, b * (t - 1) / t, drawingKhung);
				canvas.drawLine(a * (t - 2) / t, b * (t - 1) / t, a * (t - 2)
						/ t, b * (t - 1) / t - l, drawingKhung);

				canvas.drawLine(a * (t - 2) / t, b / t + l, a * (t - 2) / t, b
						/ t, drawingKhung);
				canvas.drawLine(a * (t - 2) / t, b / t, a * (t - 2) / t - l, b
						/ t, drawingKhung);
				//
			}
		}

	}
	
	public class SaveFileDoneReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			String imageFileDone = bundle.getString(Constant.KEY_IMAGE_PATH_FOR_PROCESSING, Constant.VALUE_NOT_EXISTED);
			if (imageFileDone != Constant.VALUE_NOT_EXISTED) {
				Intent intent_ = new Intent(getApplicationContext(), ProcessingActivity.class);
				Bundle bundle_  = new Bundle();
				bundle_.putCharSequence(Constant.KEY_IMAGE_PATH_FOR_PROCESSING, imageFileDone);
				intent_.putExtras(bundle_);
				startActivity(intent_);
				finish();
			}
		}
		
	}
	
	public void toggleclick(View view) {
	    // Is the toggle on?
	    boolean on = ((ToggleButton) view).isChecked();

	    if (on) {
	       Toast.makeText(getApplicationContext(), "English", Toast.LENGTH_LONG).show();
	    } else {
	        Toast.makeText(getApplicationContext(), "Vietnamese", Toast.LENGTH_LONG).show();
	    }
	}
}