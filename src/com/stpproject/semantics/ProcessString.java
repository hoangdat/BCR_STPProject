package com.stpproject.semantics;

import java.util.ArrayList;

public class ProcessString {
	private String str;
	private int str_length;
	private static final int LENGTH_NUMBERPHONE = 8;
	private static final int MAX_NUMBERPHONE = 12;
	private static final String[] EMAIL_CONDITION = { "Emai", "emai", "EMAI" };
	private static final String[] WEB_CONDITION = { "Http", "http", "Web",
			"web", "HTTP", "WEB" };

	public ProcessString(String str) {
		this.str = str;
		this.str_length = str.length();
		if (str.charAt(str_length - 1) == ' ') {
			str_length--;
		}
	}

	// Get list number phone
	public ArrayList<String> getNumberPhone() {
		ArrayList<String> listNumber = new ArrayList<String>();
		int begin = 0;
		int end = 0;
		while (begin < str_length) {
			if (isNumber(str.charAt(begin))) {
				end = getEndString(begin);
				if (null != getNumberPhone(begin, end)) {
					listNumber.add(getNumberPhone(begin, end));
				}
				begin = end;
			} else {
				begin++;
			}
		}
		return listNumber;
	}

	private int getEndString(int begin) {
		for (int index = begin; index < str_length; index++) {
			if ((str.charAt(index) == ' ')
					&& (!isNumber(str.charAt(index + 1)))
					|| (str.charAt(index) == '\n')
					|| (str.charAt(index) == ' ')
					&& (index - begin > MAX_NUMBERPHONE)) {
				return index;
			}
		}
		return str_length;
	}

	private boolean isNumber(char c) {
		return (c >= '0' && c <= '9');
	}

	private String getNumberPhone(int begin, int end) {
		StringBuilder number = new StringBuilder();
		for (int index = begin; index < end; index++) {
			if (isNumber(str.charAt(index))) {
				number.append(str.charAt(index));
			}
		}
		if (number.length() >= LENGTH_NUMBERPHONE) {
			return number.toString();
		} else
			return null;
	}

	// Get emaillist
	public ArrayList<String> getEmailList() {
		ArrayList<String> listEmail = new ArrayList<String>();
		int begin = 0;
		while (begin < str_length) {
			if (str.charAt(begin) == '@') {
				Position position = getBeginEndString(begin);
				listEmail.add(str.substring(position.getX() + 1,
						position.getY()));
				begin = position.getY();
			} else {
				begin++;
			}
		}
		// Loc lan 1
		if (listEmail.size() == 0 && isEmail()) {
			begin = 0;
			while (begin < str_length) {
				if (str.charAt(begin) == '.') {
					Position position = getBeginEndString(begin);
					listEmail.add(str.substring(position.getX() + 1,
							position.getY()));
					begin = position.getY();
				} else {
					begin++;
				}
			}
		}
		// Loc lan 2
		// if (listEmail.size() == 0 && isEmail()) {
		// listEmail.add(str);
		// }
		return listEmail;
	}

	// Get bengin end position
	private Position getBeginEndString(int begin) {
		Position position = new Position();
		position.setX(-1);
		position.setY(str_length);
		// Get end
		for (int index = begin; index < str_length; index++) {
			if ((str.charAt(index) == ' ') || (str.charAt(index) == '\n')) {
				position.setY(index);
				break;
			}
		}
		for (int index = begin; index >= 0; index--) {
			if ((str.charAt(index) == ' ') || (str.charAt(index) == '\n')
					|| (str.charAt(index) == ':') || (str.charAt(index) == '/')) {
				position.setX(index);
				break;
			}
		}
		return position;

	}

	class Position {
		private int x = 0, y = 0;

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

	}

	// Get website list
	public ArrayList<String> getWebsiteList() {
		ArrayList<String> listWebsite = new ArrayList<String>();
		// int from = 0;
		// String subStr = new String("www.");
		// Log.e("Nguyen thong", str.lastIndexOf(subStr, from)+"");
		// while (str.lastIndexOf(subStr, from) != -1) {
		// int begin = str.lastIndexOf(subStr, from);
		// Log.e("Nguyen thong", begin+"");
		// int end = getEndStringWeb(begin);
		// listWebsite.add(str.substring(begin, end));
		// from = end;
		// }
		int begin = 0;
		while (begin < str_length - 3) {
			if ((str.charAt(begin) == 'w' || str.charAt(begin) == 'W')
					&& (str.charAt(begin + 1) == 'w' || str.charAt(begin + 1) == 'W')
					&& (str.charAt(begin + 2) == 'w' || str.charAt(begin + 2) == 'W')
					&& str.charAt(begin + 3) == '.') {
				int end = getEndStringWeb(begin);
				listWebsite.add(str.substring(begin, end));
				begin = end;
			} else {
				begin++;
			}
		}
		// Loc lan 1
		if (listWebsite.size() == 0 && isWebsite()) {
			begin = 0;
			while (begin < str_length) {
				if (str.charAt(begin) == '.') {
					Position position = getBeginEndString(begin);
					listWebsite.add(str.substring(position.getX() + 1,
							position.getY()));
					begin = position.getY();
				} else {
					begin++;
				}
			}
		}
		// Loc lan 2
		// if (listWebsite.size() == 0 && isWebsite()) {
		// listWebsite.add(str);
		// }

		return listWebsite;
	}

	private int getEndStringWeb(int begin) {
		for (int index = begin; index < str_length; index++) {
			if ((str.charAt(index) == ' ') || (str.charAt(index) == '\n')) {
				return index;
			}
		}
		return str_length;
	}

	private boolean isWebsite() {
		for (int index = 0; index < WEB_CONDITION.length; index++) {
			String conditionStr = WEB_CONDITION[index];
			if (checkAppear(conditionStr, str)) {
				return true;
			}
		}
		return false;
	}

	private boolean isEmail() {
		for (int index = 0; index < EMAIL_CONDITION.length; index++) {
			String conditionStr = EMAIL_CONDITION[index];
			if (checkAppear(conditionStr, str)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkAppear(String subStr, String parStr) {
		int sub_length = subStr.length();
		int par_length = parStr.length();
		if (sub_length > par_length) {
			return false;
		}
		for (int index = 0; index < (par_length - sub_length + 1); index++) {
			if (subStr.charAt(0) == parStr.charAt(index)) {
				String compareStr = parStr.substring(index, index + sub_length);
				if (subStr.equals(compareStr)) {
					return true;
				}
			}

		}
		return false;
	}

	public boolean isFax() {
		for (int index = 0; index < str.length(); index++) {
			if (str.charAt(index) == 'F' || str.charAt(index) == 'f') {
				return true;
			}
		}
		return false;
	}
}