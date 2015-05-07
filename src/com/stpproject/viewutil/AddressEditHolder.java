package com.stpproject.viewutil;

import com.stpproject.model.RectItem;

import android.widget.EditText;
import android.widget.ImageButton;

public class AddressEditHolder {
	EditText edtAddress;
	ImageButton imgBtnDeleteAddress;
	private RectItem rectItem;
	public EditText getEdtAddress() {
		return edtAddress;
	}
	public RectItem getRectItem() {
		return rectItem;
	}
	public void setRectItem(RectItem rectItem) {
		this.rectItem = rectItem;
	}
	public void setEdtAddress(EditText edtAddress) {
		this.edtAddress = edtAddress;
	}
	public ImageButton getImgBtnDeleteAddress() {
		return imgBtnDeleteAddress;
	}
	public void setImgBtnDeleteAddress(ImageButton imgBtnDeleteAddress) {
		this.imgBtnDeleteAddress = imgBtnDeleteAddress;
	}
	public AddressEditHolder(EditText edtAddress,
			ImageButton imgBtnDeleteAddress) {
		super();
		this.edtAddress = edtAddress;
		this.imgBtnDeleteAddress = imgBtnDeleteAddress;
	}
}
