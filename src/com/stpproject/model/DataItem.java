package com.stpproject.model;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class DataItem {
	private int id_card;
	private int contact_position = -1;
	private String nameCard;
	private RectItem rectName;
	private String nameCompany;
	private RectItem rectCompany;
	private String image;
	private Bitmap bitmapIcon;
	private ArrayList<AddressItem> listAddress = new ArrayList<AddressItem>();
	private ArrayList<PhoneItem> listPhone = new ArrayList<PhoneItem>();
	private ArrayList<EmailItem> listEmail = new ArrayList<EmailItem>();
	private ArrayList<WebItem> listWeb = new ArrayList<WebItem>();

	public int getId_card() {
		return id_card;
	}

	public void setId_card(int id_card) {
		this.id_card = id_card;
	}

	public int getContact_position() {
		return contact_position;
	}

	public void setContact_position(int contact_id) {
		this.contact_position = contact_id;
	}

	public ArrayList<AddressItem> getListAddress() {
		return listAddress;
	}

	public void setListAddress(ArrayList<AddressItem> listAddress) {
		this.listAddress = listAddress;
	}

	public ArrayList<PhoneItem> getListPhone() {
		return listPhone;
	}

	public void setListPhone(ArrayList<PhoneItem> listPhone) {
		this.listPhone = listPhone;
	}

	public ArrayList<EmailItem> getListEmail() {
		return listEmail;
	}

	public void setListEmail(ArrayList<EmailItem> listEmail) {
		this.listEmail = listEmail;
	}

	public ArrayList<WebItem> getListWeb() {
		return listWeb;
	}

	public void setListWeb(ArrayList<WebItem> listWeb) {
		this.listWeb = listWeb;
	}

	public String getNameCard() {
		return nameCard;
	}

	public void setNameCard(String nameCard) {
		this.nameCard = nameCard;
	}

	public RectItem getRectName() {
		return rectName;
	}

	public void setRectName(RectItem rectName) {
		this.rectName = rectName;
	}

	public String getNameCompany() {
		return nameCompany;
	}

	public void setNameCompany(String nameCompany) {
		this.nameCompany = nameCompany;
	}

	public RectItem getRectCompany() {
		return rectCompany;
	}

	public void setRectCompany(RectItem rectCompany) {
		this.rectCompany = rectCompany;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Bitmap getBitmapIcon() {
		return bitmapIcon;
	}

	public void setBitmapIcon(Bitmap bitmapIcon) {
		this.bitmapIcon = bitmapIcon;
	}

	public String converseStr() {
		StringBuffer dataString = new StringBuffer();
		dataString.append("");
		if (null != this.nameCard) {
			dataString.append("Name:").append(this.nameCard).append("\n");
		}
		if (null != this.nameCompany) {
			dataString.append("Company:").append(this.nameCompany).append("\n");
		}
		for (int index = 0; index < this.listPhone.size(); index++) {
			PhoneItem phoneItem = this.listPhone.get(index);
			if (phoneItem.getType() == 0) {
				dataString.append("WorkPhone:")
						.append(phoneItem.getPhoneName()).append("\n");
			} else if (phoneItem.getType() == 1) {
				dataString.append("MobilePhone:")
						.append(phoneItem.getPhoneName()).append("\n");
			} else {
				dataString.append("Fax:").append(phoneItem.getPhoneName())
						.append("\n");
			}
		}
		for (int index = 0; index < this.listEmail.size(); index++) {
			EmailItem emailItem = this.listEmail.get(index);
			dataString.append("Email:").append(emailItem.getEmailName())
					.append("\n");
		}
		for (int index = 0; index < this.listWeb.size(); index++) {
			WebItem webItem = this.listWeb.get(index);
			dataString.append("Web:").append(webItem.getWebName()).append("\n");
		}
		for (int index = 0; index < this.listAddress.size(); index++) {
			AddressItem addressItem = this.listAddress.get(index);
			dataString.append("Address:").append(addressItem.getAddressName())
					.append("\n");
		}
		if (dataString.length() > 2) {
			dataString.deleteCharAt(dataString.length() - 1);
		}
		return dataString.toString();
	}

}
