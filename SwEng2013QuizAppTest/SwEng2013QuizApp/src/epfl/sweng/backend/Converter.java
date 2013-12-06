package epfl.sweng.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;
import epfl.sweng.quizquestions.QuizQuestion;

/**
 * Utility class that allows us to convert JSON in various formats. It is
 * supposed to be a general-converter.
 * 
 * @author born4new
 */

public abstract class Converter {

	
	/**
	 * Converts a {@link JSONArray} into a list of String.
	 * 
	 * @param array The {@link JSONArray} to convert.
	 * @return A <code>List</code> of the <code>JSONObjects</code> string field.
	 */
	
	public static List<String> jsonArrayToStringList(JSONArray array) {
		List<String> list = new ArrayList<String>();
		if (array != null) {
			for (int i = 0; i < array.length() && array.opt(i) != null; i++) {
				list.add(array.optString(i));
			}
		}
		return list;
	}
	
	/**
	 * Converts a {@link JSONArray} to a list of {@link QuizQuestion}s.
	 * <p>
	 * The {@link JSONObject}s string field must follow the structure of a
	 * {@link QuizQuestion}, otherwise the method returns <code>null</code>.
	 * 
	 * @param array The {@link JSONArray} to convert.
	 * @return A <code>List</code> of the translated <code>JSONObjects</code>
	 *         string field into {@link QuizQuestion} instances.
	 */
	
	public static List<QuizQuestion> jsonArrayToQuizQuestionList(JSONArray array) {
		List<QuizQuestion> list = new ArrayList<QuizQuestion>();
		if (array != null) {
			try {
				for (int i = 0; i < array.length() && array.opt(i) != null; i++) {
					list.add(new QuizQuestion(array.optString(i)));
				}
			} catch (JSONException e) {
				Log.e(Converter.class.getName(), "jsonArrayToQuizQuestionList(): "
						 + "wrong structure of JSON inputs.", e);
				return null;
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
