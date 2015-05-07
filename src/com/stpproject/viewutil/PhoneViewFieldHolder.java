package com.stpproject.viewutil;

import android.widget.ImageButton;
import android.widget.TextView;

public class PhoneViewFieldHolder {
	TextView txtPhoneNumber;
	ImageButton imgBtnCall;
	
	public PhoneViewFieldHolder(TextView txtPhoneNumber, ImageButton imgBtnCall) {
		this.txtPhoneNumber = txtPhoneNumber;
		this.imgBtnCall = imgBtnCall;
	}
	public TextView getTxtPhoneNumber() {
		return txtPhoneNumber;
	}
	public void setTxtPhoneNumber(TextView txtPhoneNumber) {
		this.txtPhoneNumber = txtPhoneNumber;
	}
	public ImageButton getImgBtnCall() {
		return imgBtnCall;
	}
	public void setImgBtnCall(ImageButton imgBtnCall) {
		this.imgBtnCall = imgBtnCall;
	}
}
