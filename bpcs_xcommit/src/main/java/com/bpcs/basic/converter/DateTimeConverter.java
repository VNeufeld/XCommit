package com.bpcs.basic.converter;

public class DateTimeConverter {
	/**
	 * Convert from "2006-08-15T17:23:15Z" to "2006-08-15 17:23:15"
	 * @param dateTime
	 * @return
	 */
	public static String convertFromPaypalTime(String dateTime) {
		String result = dateTime.replace('T',' ');
		result = result.replace('Z',' ');
		result = result.trim();
		return result;
	}

}
