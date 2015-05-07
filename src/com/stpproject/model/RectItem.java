package com.stpproject.model;

public class RectItem {
	private int x;
	private int y;
	private int height;
	private int width;

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int wight) {
		this.width = wight;
	}

	public String getStringSql() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(x + " ");
		stringBuilder.append(y + " ");
		stringBuilder.append(height + " ");
		stringBuilder.append(width);
		return stringBuilder.toString();
	}

	public void makeDataRect(String sqlStr) {
		String[] splitStr = sqlStr.split(" ");
		if (splitStr.length == 4) {
			setX(Integer.parseInt(splitStr[0]));
			setY(Integer.parseInt(splitStr[1]));
			setHeight(Integer.parseInt(splitStr[2]));
			setWidth(Integer.parseInt(splitStr[3]));
		}
	}
}
