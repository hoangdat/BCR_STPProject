package com.stpproject.viewutil;

import android.widget.ImageButton;
import android.widget.TextView;

public class EmailViewFieldHolder {
	TextView txtEmail;
	ImageButton imgBtnEmail;
	
	public EmailViewFieldHolder(TextView txtEmail, ImageButton imgBtnEmail) {
		this.txtEmail = txtEmail;
		this.imgBtnEmail = imgBtnEmail;
	}
	public TextView getTxtEmail() {
		return txtEmail;
	}
	public void setTxtEmail(TextView txtEmail) {
		this.txtEmail = txtEmail;
	}
	public ImageButton getImgBtnEmail() {
		return imgBtnEmail;
	}
	public void setImgBtnEmail(ImageButton imgBtnEmail) {
		this.imgBtnEmail = imgBtnEmail;
	}
}
