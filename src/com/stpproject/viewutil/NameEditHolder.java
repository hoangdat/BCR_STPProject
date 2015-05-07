package com.stpproject.viewutil;

import android.widget.EditText;

import com.stpproject.model.RectItem;

public class NameEditHolder {
	EditText edtNameORCompany;
	private RectItem rectItem;
	public EditText getEdtNameORCompany() {
		return edtNameORCompany;
	}
	public void setEdtNameORCompany(EditText edtNameORCompany) {
		this.edtNameORCompany = edtNameORCompany;
	}
	public RectItem getRectItem() {
		return rectItem;
	}
	public void setRectItem(RectItem rectItem) {
		this.rectItem = rectItem;
	}
	public NameEditHolder(EditText edtNameORCompany) {
		this.edtNameORCompany = edtNameORCompany;
	}
	
}
