package com.stpproject.viewutil;

import com.stpproject.model.RectItem;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

public class PhoneEditHolder {
	EditText edtPhoneNumber;
	ImageButton imgBtnDelPhoneNumber;
	Spinner spinner;

	private RectItem rectItem;

	public Spinner getSpiner() {
		return spinner;
	}

	public void setSipner(Spinner spinner) {
		this.spinner = spinner;
	}

	public EditText getEdtPhoneNumber() {
		return edtPhoneNumber;
	}

	public void setEdtPhoneNumber(EditText edtPhoneNumber) {
		this.edtPhoneNumber = edtPhoneNumber;
	}

	public ImageButton getImgBtnDelPhoneNumber() {
		return imgBtnDelPhoneNumber;
	}

	public void setImgBtnDelPhoneNumber(ImageButton imgBtnDelPhoneNumber) {
		this.imgBtnDelPhoneNumber = imgBtnDelPhoneNumber;
	}

	public PhoneEditHolder(EditText edtPhoneNumber,
			ImageButton imgBtnDelPhoneNumber, Spinner spinner) {
		this.edtPhoneNumber = edtPhoneNumber;
		this.imgBtnDelPhoneNumber = imgBtnDelPhoneNumber;
		this.spinner = spinner;
	}

	public RectItem getRectItem() {
		return rectItem;
	}

	public void setRectItem(RectItem rectItem) {
		this.rectItem = rectItem;
	}
}
