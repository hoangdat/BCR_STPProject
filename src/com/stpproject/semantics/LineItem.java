package com.stpproject.semantics;

import org.opencv.core.Rect;

import android.graphics.Bitmap;

public class LineItem {
	// Độ chênh lệch khoảng cách tối đa để coi là liền kề
	private static final int VERTICAL_DISTANCE_MAX = 15;
	// Độ chênh lệch chiều cao tối đa để coi là bằng nhau
	private static final int SUB_HEIGHT_MAX = 25;
	// Độ chồng chéo tối đa theo chiều dọc để coi là liền kề
	private static final int OVERLAP_MAX = -15;
	// Độ chồng chéo tối đa theo chiều ngang để coi là liền kề
	private static final int OVERLAP_HORIZONTAL_MAX = -20;

	private Bitmap btmRegion;
	private String stringResult;
	private Rect rectRegion;
	private int parentWidth;
	private int parentHeight;
	private boolean isProcess = false;
	private boolean isHaveResult = false;

	public boolean isHaveResult() {
		return isHaveResult;
	}

	public void setHaveResult(boolean isHaveResult) {
		this.isHaveResult = isHaveResult;
	}

	public Bitmap getBtmRegion() {
		return btmRegion;
	}

	public void setBtmRegion(Bitmap btmRegion) {
		this.btmRegion = btmRegion;
	}

	public String getStringResult() {
		return stringResult;
	}

	public void setStringResult(String stringResult) {
		this.stringResult = stringResult;
	}

	public Rect getRectRegion() {
		return rectRegion;
	}

	public void setRectRegion(Rect rectRegion) {
		this.rectRegion = rectRegion;
	}

	public int getParentWidth() {
		return parentWidth;
	}

	public void setParentWidth(int parentWidth) {
		this.parentWidth = parentWidth;
	}

	public int getParentHeight() {
		return parentHeight;
	}

	public void setParentHeight(int parentHeight) {
		this.parentHeight = parentHeight;
	}

	public boolean isProcess() {
		return isProcess;
	}

	public void setProcess(boolean isProcess) {
		this.isProcess = isProcess;
	}

	// Hàm isSequential Kiểm tra hai Rect có liền kề nhau hay không
	public boolean isSequential(LineItem lineItem) {
		Rect rect1 = this.getRectRegion();
		Rect rect2 = lineItem.getRectRegion();
		if (null == rect1 || null == rect2) {
			return false;
		}
		// Kiểm tra sự chồng nhau theo chiều ngang
		if ((rect1.x - rect2.x - rect2.width) >= OVERLAP_HORIZONTAL_MAX) {
			return false;
		}
		if ((rect2.x - rect1.x - rect1.width) >= OVERLAP_HORIZONTAL_MAX) {
			return false;
		}
		// Kiểm tra sự liền kề của 2 Rect
		if (rect1.y > rect2.y) {
			int distance = rect1.y - rect2.y - rect2.height;
			if (distance >= OVERLAP_MAX && distance <= VERTICAL_DISTANCE_MAX) {
				return true;
			} else {
				return false;
			}
		} else {
			int distance = rect2.y - rect1.y - rect1.height;
			if (distance >= OVERLAP_MAX && distance <= VERTICAL_DISTANCE_MAX) {
				return true;
			} else {
				return false;
			}
		}
	}

	// Hàm isSequentialHeight Kiểm tra sự liền kề của các Rect cùng độ cao

	public boolean isSequentialHeight(LineItem lineItem) {
		Rect rect1 = this.getRectRegion();
		Rect rect2 = lineItem.getRectRegion();
		if (null == rect1 || null == rect2) {
			return false;
		}
		// Kiểm tra sự chồng nhau theo chiều ngang
		if ((rect1.x - rect2.x - rect2.width) >= OVERLAP_HORIZONTAL_MAX) {
			return false;
		}
		if ((rect2.x - rect1.x - rect1.width) >= OVERLAP_HORIZONTAL_MAX) {
			return false;
		}
		// Kiểm tra độ cao của 2 Rect
		int distanceHeight = Math.abs(rect1.height - rect2.height);
		if (distanceHeight > SUB_HEIGHT_MAX) {
			return false;
		}
		// Kiểm tra sự liền kề
		if (rect1.y > rect2.y) {
			int distance = rect1.y - rect2.y - rect2.height;
			if (distance >= OVERLAP_MAX && distance <= VERTICAL_DISTANCE_MAX) {
				return true;
			} else {
				return false;
			}
		} else {
			int distance = rect2.y - rect1.y - rect1.height;
			if (distance >= OVERLAP_MAX && distance <= VERTICAL_DISTANCE_MAX) {
				return true;
			} else {
				return false;
			}
		}
	}
}
