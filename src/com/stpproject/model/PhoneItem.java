package com.stpproject.model;

public class PhoneItem {
	private int id_detail;
	private int id_card;
	private String phoneName;
	private RectItem rectItem;
	private int type; // 0 - Work Phone, 1 - Mobile , 2 - Fax

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

	public String getPhoneName() {
		return phoneName;
	}

	public void setPhoneName(String phoneName) {
		this.phoneName = phoneName;
	}

	public RectItem getRectItem() {
		return rectItem;
	}

	public void setRectItem(RectItem rectItem) {
		this.rectItem = rectItem;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getStringSql() {
		if (this.phoneName == null) {
			return null;
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.phoneName);
		if (this.type == 0) {
			stringBuilder.append("W");
		} else if (this.type == 1) {
			stringBuilder.append("M");
		} else {
			stringBuilder.append("F");
		}
		return stringBuilder.toString();
	}

	public void makePhoneData(String sqlStr) {
		this.setPhoneName(sqlStr.substring(0, sqlStr.length() - 1));
		if (sqlStr.charAt(sqlStr.length() - 1) == 'W') {
			this.setType(0);
		} else if (sqlStr.charAt(sqlStr.length() - 1) == 'M') {
			this.setType(1);
		} else {
			this.setType(2);
		}
	}

}
