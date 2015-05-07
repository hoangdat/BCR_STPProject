package com.stpproject.semantics;

import java.util.ArrayList;

public class CheckNameTitle {
	private static final float RATIO_MIN = 2.0f;
	private static final int SPACE_MIN = 1;
	private static final int SPACE_MAX = 3;
	private static final int CHARACTERS_MIN = 8;
	private static final int CHARACTERS_MAX = 26;
	private static final int SPACE_ADDRESS_MIN = 5;
	private static final String[] COMPANY_CONDITION = { "Co.,", "Ltd",
			"CÔNG TY", "Công ty", "COMPANY", "ompany", "CORP", "CO.,", "LTD",
			"icrosoft", "ICROSOFT", "LLC", "BRANCH", "branch", "TNHH" };
	private static final String[] NOT_NAME_CONDITION = { "-", "(", ")", "<",
			"\"", "\'", "<", ">", "&", "!", "#", "$", "%", "*", "^", "`", "~",
			"|", "\\" };

	public static boolean isNameCard(ArrayList<LineItem> listLine, int position) {
		LineItem lineItem = listLine.get(position);
		// Kiem tra lien ke
		for (int index = 0; index < position; index++) {
			if (lineItem.isSequential(listLine.get(index))) {
				return false;
			}
		}
		// Kiem tra ti le chieu rong va cao
		float rationItem = lineItem.getRectRegion().width
				/ lineItem.getRectRegion().height;
		if (rationItem <= RATIO_MIN) {
			return false;
		}
		// Kiem tra so dau cach
		String str = lineItem.getStringResult();
		int numberSpace = 0;
		for (int index = 0; index < str.length(); index++) {
			if (str.charAt(index) == ' ') {
				numberSpace++;
			}
		}
		if (numberSpace < SPACE_MIN || numberSpace > SPACE_MAX) {
			return false;
		}
		// Kiem tra so ki tu
		if (str.length() < CHARACTERS_MIN || str.length() > CHARACTERS_MAX) {
			return false;
		}
		// Kiem tra ki tu dac biet
		for (int index = 0; index < NOT_NAME_CONDITION.length; index++) {
			String conditionStr = NOT_NAME_CONDITION[index];
			if (checkAppear(conditionStr, str)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNameAddress(String str) {
		int numberSpace = 0;
		int numberPhayTru = 0;
		int numberNumber = 0;
		for (int index = 0; index < str.length(); index++) {
			if (str.charAt(index) == ' ' || str.charAt(index) == '\n') {
				numberSpace++;
			}
			if (str.charAt(index) == ',' || str.charAt(index) == '-') {
				numberPhayTru++;
			}
			if (isNumber(str.charAt(index))) {
				numberNumber++;
			}
		}
		if (numberSpace < SPACE_ADDRESS_MIN) {
			return false;
		}
		if (numberPhayTru == 0 && numberNumber < 3) {
			return false;
		}
		return true;
	}

	public static String deleteTitleAddress(String addr) {
		StringBuilder newAddr = new StringBuilder();
		int begin = 0;
		int end = 12;
		if (end > addr.length() - 1) {
			end = addr.length() - 1;
		}
		for (int index = 0; index < end; index++) {
			if (addr.charAt(index) == ':') {
				begin = index + 1;
				break;
			}
		}
		newAddr.append(addr.substring(begin, addr.length()));
		while (newAddr.charAt(0) == ' ') {
			newAddr.deleteCharAt(0);
		}
		return newAddr.toString();
	}

	public static boolean isNameCompany(String str) {
		for (int index = 0; index < COMPANY_CONDITION.length; index++) {
			String conditionStr = COMPANY_CONDITION[index];
			if (checkAppear(conditionStr, str)) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkAppear(String subStr, String parStr) {
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

	public static boolean isNumber(char c) {
		return (c >= '0' && c <= '9');
	}

	public static int checkTypePhone(String str) {
		String sub = str.substring(0, 2);
		String sub84 = str.substring(0, 3);
		if (sub.equals("09") || sub.equals("01")) {
			return 1;// mobiphone
		}
		if (sub84.equals("849") || sub84.equals("841")) {
			return 1; // mobiphone
		}
		return 0; // Work phone
	}
}
