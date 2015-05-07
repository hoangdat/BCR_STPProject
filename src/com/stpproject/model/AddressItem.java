package com.stpproject.model;

public class AddressItem {
	private int id_detail;
	private int id_card;
	private String addressName;
	private RectItem rectItem;

	public int getId_card() {
		return id_card;
	}

	public void setId_card(int id_card) {
		this.id_card = id_card;
	}

	public String getAddressName() {
		return addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	public int getId_detail() {
		return id_detail;
	}

	public void setId_detail(int id_detail) {
		this.id_detail = id_detail;
	}

	public RectItem getRectItem() {
		return rectItem;
	}

	public void setRectItem(RectItem rectItem) {
		this.rectItem = rectItem;
	}

}
