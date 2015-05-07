package com.stpproject.viewutil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Adapter;
import android.widget.SectionIndexer;

public class IndexScrollerSTP {

//	private float mIndexbarWidth;
	private IndexSTPListView mLstView;
	private RectF mIndexbarRect;
	//private float mPreviewPadding;
	private SectionIndexer mIndexer = null;
	private String[] mSections = null;
//	private float mDensity;
//	private float mScaledDensity;
	private int mListViewWidth;
	private int mListViewHeight;
	public boolean mIsIndexing = false;
	public int mCurrentSection = -1;
	private int mState = -1;
	
	public static final String TAG = "IndexScrollerSTP";

	
	private static final int STATE_SHOWING = 1;
	private static final int STATE_HIDE_PRE = 2;

	public IndexScrollerSTP(Context context, IndexSTPListView lstView) {
//		mDensity = context.getResources().getDisplayMetrics().density;
//		mScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;

		mLstView = lstView;
		mLstView.setAdapter(mLstView.getAdapter());

//		mIndexbarWidth = 20 * mDensity;
//		mPreviewPadding = 5 * mDensity;
	}

	public void draw(Canvas canvas) {
		Paint indexbarPaint = new Paint();
		indexbarPaint.setColor(Color.WHITE);
		indexbarPaint.setAntiAlias(true);

		canvas.drawRect(mIndexbarRect, indexbarPaint);
		
		Paint barPaint = new Paint();
		barPaint.setARGB(255, 189, 189, 189);
		barPaint.setAntiAlias(true);
		canvas.drawLine(mIndexbarRect.left, mIndexbarRect.top, mIndexbarRect.left, mIndexbarRect.bottom, barPaint);

		if (mSections != null && mSections.length > 0) {
			if (mCurrentSection >= 0) {
				Paint previewPaint = new Paint();
				previewPaint.setColor(Color.BLACK);
				previewPaint.setAlpha(96);
				previewPaint.setAntiAlias(true);
				previewPaint.setShadowLayer(3, 0, 0, Color.argb(64, 0, 0, 0));

				Paint previewTextPaint = new Paint();
				previewTextPaint.setColor(Color.WHITE);
				previewTextPaint.setAntiAlias(true);
				previewTextPaint.setTextSize(50 * mLstView.mScaledDensity);

				float previewTextWidth = previewTextPaint
						.measureText(mSections[mCurrentSection]);
				float previewSize = 2 * mLstView.mPreviewPadding
						+ previewTextPaint.descent()
						- previewTextPaint.ascent();
				RectF previewRect = new RectF(
						(mListViewWidth - previewSize) / 2,
						(mListViewHeight - previewSize) / 2,
						(mListViewWidth - previewSize) / 2 + previewSize,
						(mListViewHeight - previewSize) / 2 + previewSize);

				canvas.drawRoundRect(previewRect, 5 * mLstView.mDensity, 5 * mLstView.mDensity,
						previewPaint);
				canvas.drawText(
						mSections[mCurrentSection],
						previewRect.left + (previewSize - previewTextWidth) / 2
								- 1,
						previewRect.top + mLstView.mPreviewPadding
								- previewTextPaint.ascent() + 1,
						previewTextPaint);
			}
		}

		Paint indexPaint = new Paint();
		indexPaint.setColor(Color.BLACK);
		indexPaint.setAntiAlias(true);
		indexPaint.setTextSize(15 * mLstView.mScaledDensity);

		float sectionHeight = mIndexbarRect.height() / mSections.length;
		float paddingTop = (sectionHeight - (indexPaint.descent() - indexPaint
				.ascent())) / 2;
		for (int i = 0; i < mSections.length; i++) {
			float paddingLeft = (mLstView.mIndexbarWidth - indexPaint
					.measureText(mSections[i])) / 2;
			canvas.drawText(mSections[i], mIndexbarRect.left + paddingLeft,
					mIndexbarRect.top + sectionHeight * i + paddingTop
							- indexPaint.ascent(), indexPaint);
			if (i != (mSections.length - 1)){
				canvas.drawLine(mIndexbarRect.left, mIndexbarRect.top + sectionHeight * (i+1), mIndexbarRect.right, mIndexbarRect.top + sectionHeight * (i+1), barPaint);
			}
			
		}
	}

	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.d(TAG, "w: " + w + " h: " + h);
		mListViewWidth = w;
		mListViewHeight = h;
		mIndexbarRect = new RectF(w - mLstView.mIndexbarWidth, 0, w, h);
	}

	private boolean contains(float x, float y) {
		return (x >= mIndexbarRect.left && y >= mIndexbarRect.top && y <= mIndexbarRect.top
				+ mIndexbarRect.height());
	}

	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (contains(ev.getX(), ev.getY())) {
				mIsIndexing = true;
				mCurrentSection = getSectionByPoint(ev.getY());
				int iSelector = mIndexer.getPositionForSection(mCurrentSection);
				if (iSelector != -1) {
					mLstView.setSelection(iSelector);
				}
				return true;
			}
			break;

		

		case MotionEvent.ACTION_MOVE:
			if (mIsIndexing) {
				if (contains(ev.getX(), ev.getY())) {
					mCurrentSection = getSectionByPoint(ev.getY());
					int iSelector = mIndexer.getPositionForSection(mCurrentSection);
					if (iSelector != -1) {
						mLstView.setSelection(iSelector);
					}
				} 
				return true;
			}
			break;
			
			
		case MotionEvent.ACTION_UP:
			if (mIsIndexing) {
				mIsIndexing = false;
				mCurrentSection = -1;
				mState = STATE_HIDE_PRE;
				fade(10);
				return true;
			}
			
			
			break;
		}
		return false;
	}

	public void setAdapter(Adapter adapter) {
		if (adapter instanceof SectionIndexer) {
			mIndexer = (SectionIndexer) adapter;
			mSections = (String[]) mIndexer.getSections();
		}
	}

	private int getSectionByPoint(float y) {
		if (mSections == null || mSections.length == 0)
			return 0;
		if (y < mIndexbarRect.top)
			return 0;
		if (y >= mIndexbarRect.top + mIndexbarRect.height())
			return mSections.length - 1;
		return (int) ((y - mIndexbarRect.top) / ((mIndexbarRect.height()) / mSections.length));
	}



	private void fade(long delay) {
		mHandler.removeMessages(0);
		mHandler.sendEmptyMessageAtTime(0, SystemClock.uptimeMillis() + delay);
	}
	
	public void show() {
		mState = STATE_SHOWING;
		fade(0);
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (mState) {
			case STATE_SHOWING:
				//mLstView.invalidate();
				fade(10);
				break;
				
			case STATE_HIDE_PRE:
				
				mLstView.invalidate();
				fade(10);
				break;
			}
		}

	};
}
