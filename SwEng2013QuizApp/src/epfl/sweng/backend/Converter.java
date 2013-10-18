package epfl.sweng.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONArray;

/**
 * Utility class that allows us to convert JSON in various formats.
 * It is supposed to be a general-converter.
 * @author born4new
 *
 */

public final class Converter {

	private Converter() {
		
	}
	
	/**
	 * Converts a {@link JSONArray} into a list of String.
	 * 
	 * @param arrayToConvert The {@link JSONArray} to convert.
	 * @return An <code>ArrayList</code> of the <code>JSONObjects</code> string
	 *            field.
	 */
	public static List<String> jsonArrayToStringArray(
			JSONArray arrayToConvert) {
		ArrayList<String> list = new ArrayList<String>();
		if (arrayToConvert != null) {
			for (int i = 0; i < arrayToConvert.length(); i++) {
				if (arrayToConvert.optString(i) != null) {
					list.add(arrayToConvert.optString(i));
				}
			}
		}
		return list;
	}
	
	/**
	 * Converts a {@link JSONArray} into a set of String.
	 * 
	 * @param arrayToConvert The {@link JSONArray} to convert.
	 * @return An <code>HashSet</code> of the <code>JSONObjects</code> string
	 *            field.
	 */
	public static Set<String> jsonArrayToStringSet(
			JSONArray arrayToConvert) {
		TreeSet<String> set = new TreeSet<String>();
		if (arrayToConvert != null) {
			for (int i = 0; i < arrayToConvert.length(); i++) {
				if (arrayToConvert.optString(i) != null) {
					set.add(arrayToConvert.optString(i));
				}
			}
		}
		return set;
	}
}
