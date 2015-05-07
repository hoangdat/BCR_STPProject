package com.stpproject.viewutil;

import android.widget.EditText;
import android.widget.ImageButton;

import com.stpproject.model.RectItem;

public class CompanyEditHolder {
	EditText edtCompanyNameEdit;
	ImageButton imgBtnMinus;
	private RectItem rectItem;
	public EditText getEdtCompanyNameEdit() {
		return edtCompanyNameEdit;
	}
	public void setEdtCompanyNameEdit(EditText edtCompanyNameEdit) {
		this.edtCompanyNameEdit = edtCompanyNameEdit;
	}
	public ImageButton getImgBtnMinus() {
		return imgBtnMinus;
	}
	public void setImgBtnMinus(ImageButton imgBtnMinus) {
		this.imgBtnMinus = imgBtnMinus;
	}
	public RectItem getRectItem() {
		return rectItem;
	}
	public void setRectItem(RectItem rectItem) {
		this.rectItem = rectItem;
	}
	public CompanyEditHolder(EditText edtCompanyNameEdit,
			ImageButton imgBtnMinus) {
		this.edtCompanyNameEdit = edtCompanyNameEdit;
		this.imgBtnMinus = imgBtnMinus;
	}
}
