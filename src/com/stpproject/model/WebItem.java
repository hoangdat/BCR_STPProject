package com.stpproject.model;

public class WebItem {
	private int id_detail;
	private int id_card;
	private String webName;
	private RectItem rectItem;

	public int getId_detail() {
		return id_detail;
	}

	public void setId_detail(int id_detail) {
		this.id_detail = id_detail;
	}

	public int getId_card() {
		return id_card;
	}

	public void setId_card(int id_card) {
		this.id_card = id_card;
	}

	public String getWebName() {
		return webName;
	}

	public void setWebName(String webName) {
		this.webName = webName;
	}

	public RectItem getRectItem() {
		return rectItem;
	}

	public void setRectItem(RectItem rectItem) {
		this.rectItem = rectItem;
	}

}
