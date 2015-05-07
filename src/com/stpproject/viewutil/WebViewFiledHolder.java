package com.stpproject.viewutil;

import android.widget.ImageButton;
import android.widget.TextView;

public class WebViewFiledHolder {
	TextView txtWebsite;
	ImageButton imgBtnWebsite;
	
	public WebViewFiledHolder(TextView txtWebsite, ImageButton imgBtnWebsite) {
		this.txtWebsite = txtWebsite;
		this.imgBtnWebsite = imgBtnWebsite;
	}
	public TextView getTxtWebsite() {
		return txtWebsite;
	}
	public void setTxtWebsite(TextView txtWebsite) {
		this.txtWebsite = txtWebsite;
	}
	public ImageButton getImgBtnWebsite() {
		return imgBtnWebsite;
	}
	public void setImgBtnWebsite(ImageButton imgBtnWebsite) {
		this.imgBtnWebsite = imgBtnWebsite;
	}
}
