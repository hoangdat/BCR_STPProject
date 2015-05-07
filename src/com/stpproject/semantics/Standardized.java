package com.stpproject.semantics;

public class Standardized {
	// address
	private static final String[] BEFORE_ADDRESS = { "Tông", "\'I\'", "fìoor",
			"HỜ Nội", "Nộỉ", "Vlệt", "\'l\'", "Hờ Nội" };
	private static final String[] AFTER_ADDRESS = { "Tầng", "T", "floor",
			"Hà Nội", "Nội", "Việt", "T", "Hà Nội" };
	// company
	private static final String[] BEFORE_COMPANY = { "\"I\"", "\'I\'", "\'l\'" };
	private static final String[] AFTER_COMPANY = { "T", "T", "T" };
	// urlName
	private static final String[] BEFORE_URL = { "comvn", ".vP", "í", "ỉ", "ì",
			"ẻ", "è", "é", "ê", "á", "à", "ả", "â", "ă", "ó", "ò", "ỏ", "ơ",
			"ô", ".00m", ".oom", "ú", "ù", "ủ", "ý", "ỳ", "ỷ", "ế", "ề", "ể",
			"ệ", "ễ", "ẽ", "ẹ", "ĩ", "ị", "ã", "ạ", "ấ", "ầ", "ẩ", "ẫ", "ậ",
			"ắ", "ằ", "ẳ", "ẵ", "ặ", "õ", "ọ", "ố", "ồ", "ổ", "ỗ", "ộ", "ớ",
			"ờ", "ở", "ỡ", "ợ", "ũ", "ụ", "ứ", "ừ", "ử", "ữ", "ự" };
	private static final String[] AFTER_URL = { "com.vn", ".vn", "i", "i", "i",
			"e", "e", "e", "e", "a", "a", "a", "a", "a", "o", "o", "o", "o",
			"o", ".com", ".com", "u", "u", "u", "y", "y", "y", "e", "e", "e",
			"e", "e", "e", "e", "i", "i", "a", "a", "a", "a", "a", "a", "a",
			"a", "a", "a", "a", "a", "o", "o", "o", "o", "o", "o", "o", "o",
			"o", "o", "o", "o", "u", "u", "u", "u", "u", "u", "u" };

	public static String normalAddress(String address) {
		for (int index = 0; index < BEFORE_ADDRESS.length; index++) {
			address = address.replaceAll(BEFORE_ADDRESS[index],
					AFTER_ADDRESS[index]);
		}
		return address;
	}

	public static String normalCompany(String company) {
		for (int index = 0; index < BEFORE_COMPANY.length; index++) {
			company = company.replaceAll(BEFORE_COMPANY[index],
					AFTER_COMPANY[index]);
		}
		return company;
	}

	public static String normalUrl(String urlName) {
		for (int index = 0; index < BEFORE_URL.length; index++) {
			urlName = urlName.replaceAll(BEFORE_URL[index], AFTER_URL[index]);
		}
		return urlName;
	}

}
