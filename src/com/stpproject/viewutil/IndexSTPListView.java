package com.stpproject.viewutil;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;

public class IndexSTPListView extends ListView {
	
	IndexScrollerSTP mIndexScroller = null;
	private boolean mIsFastScrollEnabled = false;
	
	public float mDensity;
	public float mScaledDensity;
	public float mIndexbarWidth;
	public float mPreviewPadding;
	public float mWidthListView;

	public IndexSTPListView(Context context) {
		super(context);
		mDensity = context.getResources().getDisplayMetrics().density;
		mScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		
		mIndexbarWidth = 22 * mDensity;
		mPreviewPadding = 5 * mDensity;
	}
	

	public IndexSTPListView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mDensity = context.getResources().getDisplayMetrics().density;
		mScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		
		mIndexbarWidth = 20 * mDensity;
		mPreviewPadding = 5 * mDensity;
	}

	public IndexSTPListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDensity = context.getResources().getDisplayMetrics().density;
		mScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		
		mIndexbarWidth = 20 * mDensity;
		mPreviewPadding = 5 * mDensity;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (mIndexScroller != null) {
			mIndexScroller.draw(canvas);
		}
	}
	
	
	
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mWidthListView = w;
		if (mIndexScroller != null) {
			mIndexScroller.onSizeChanged(w, h, oldw, oldh);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		if (ev.getX() < (mWidthListView - mIndexbarWidth)) {
			mIndexScroller.mCurrentSection = -1;
			mIndexScroller.mIsIndexing = false;
			invalidate();
			return super.onTouchEvent(ev);
		}
		
		if (mIndexScroller != null && mIndexScroller.onTouchEvent(ev)) {
			return true;
		}
		
		return true;
	}
	
	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		if (mIndexScroller != null) {
			mIndexScroller.setAdapter(adapter);
		}
	}
	

	
	@Override
	public void setFastScrollEnabled(boolean enabled) {
		mIsFastScrollEnabled = enabled;
		if (mIsFastScrollEnabled) {
			mIndexScroller = new IndexScrollerSTP(getContext(), this);
			mIndexScroller.show();
		}
	}

}
