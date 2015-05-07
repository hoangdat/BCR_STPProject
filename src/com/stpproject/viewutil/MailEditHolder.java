package com.stpproject.viewutil;

import com.stpproject.model.RectItem;

import android.widget.EditText;
import android.widget.ImageButton;

public class MailEditHolder {
	EditText edtEmail;
	ImageButton imgBtnDeleteEmail;
	private RectItem rectItem;
	public EditText getEdtEmail() {
		return edtEmail;
	}
	public void setEdtEmail(EditText edtEmail) {
		this.edtEmail = edtEmail;
	}
	public ImageButton getImgBtnDeleteEmail() {
		return imgBtnDeleteEmail;
	}
	public void setImgBtnDeleteEmail(ImageButton imgBtnDeleteEmail) {
		this.imgBtnDeleteEmail = imgBtnDeleteEmail;
	}
	public MailEditHolder(EditText edtEmail, ImageButton imgBtnDeleteEmail) {
		this.edtEmail = edtEmail;
		this.imgBtnDeleteEmail = imgBtnDeleteEmail;
	}
	public RectItem getRectItem() {
		return rectItem;
	}
	public void setRectItem(RectItem rectItem) {
		this.rectItem = rectItem;
	}
}
