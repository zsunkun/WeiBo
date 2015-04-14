package com.example.utils;

import java.util.regex.Pattern;

public class StringUtils {

	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() <= 0;
	}

	public static boolean isNotEmpty(String str) {
		return str != null && str.trim().length() > 0;
	}

	public static boolean isValidPhoneNumber(String mobiles) {
		return Pattern.matches("^1\\d{10}$", mobiles);
	}

}
