package com.stpproject.viewutil;

import com.stpproject.model.RectItem;

import android.widget.EditText;
import android.widget.ImageButton;

public class WebEditHolder {
	EditText edtWebsite;
	ImageButton imgBtnDeleteWeb;
	private RectItem rectItem;
	public EditText getEdtWebsite() {
		return edtWebsite;
	}
	public void setEdtWebsite(EditText edtWebsite) {
		this.edtWebsite = edtWebsite;
	}
	public ImageButton getImgBtnDeleteWeb() {
		return imgBtnDeleteWeb;
	}
	public void setImgBtnDeleteWeb(ImageButton imgBtnDeleteWeb) {
		this.imgBtnDeleteWeb = imgBtnDeleteWeb;
	}
	public WebEditHolder(EditText edtWebsite, ImageButton imgBtnDeleteWeb) {
		super();
		this.edtWebsite = edtWebsite;
		this.imgBtnDeleteWeb = imgBtnDeleteWeb;
	}
	public RectItem getRectItem() {
		return rectItem;
	}
	public void setRectItem(RectItem rectItem) {
		this.rectItem = rectItem;
	}
}
