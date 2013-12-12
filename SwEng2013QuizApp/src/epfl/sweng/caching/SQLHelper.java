package epfl.sweng.caching;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class used by the cache to simplify/format for some database
 * operations.
 * 
 * @author born4new
 * 
 */
public abstract class SQLHelper {
	/**
	 * Given a length, returns a series of ? separated by commas. Example for
	 * len = 4 : "?, ?, ?, ?"
	 * 
	 * @param len
	 * @return String used as placeholders.
	 */
	public static String makePlaceholders(int len) {
		if (len < 1) {
			throw new RuntimeException("No placeholders");
		} else {
			StringBuilder sb = new StringBuilder(len * 2 - 1);
			sb.append("?");
			for (int i = 1; i < len; i++) {
				sb.append(",?");
			}
			return sb.toString();
		}
	}

	/**
	 * Normalizes the query. - We change the '*' by a logical AND. - We change
	 * the '+' by a logical OR. - We change the ' ' by a logical AND.
	 * 
	 * @param query
	 *            Query to be changed.
	 * @return the new SQL-compatible query.
	 */
	public static String filterQuery(String query) {

		// We start with "  (  A * B C + D (  E + F ))  "

		// Only one space max. between words.
		// " ( A * B C + D ( E + F )) "
		query = query.replaceAll("(\\ )+", " ");

		// Removes the spaces after '(' and/or before ')'
		// " (A * B C + D (E + F)) "
		query = query.replaceAll("\\(\\ ", "(");
		query = query.replaceAll("\\ \\)", ")");

		// Removes beginning and end spaces.
		// "(A * B C + D (E + F))"
		query = query.replaceAll("^\\ ", "");
		query = query.replaceAll("\\ $", "");

		// Replaces all the names by ? for the SQL query.
		// "(? * ? ? + ? (? + ?))"
		query = query.replaceAll("\\w+", "?");

		// Makes the " * " or " + " look like "*" or "+"
		// "(?*? ?+? (?+?))"
		query = query.replaceAll("(?:\\ )?\\*(?:\\ )?", "*");
		query = query.replaceAll("(?:\\ )?\\+(?:\\ )?", "+");

		// Replaces all the spaces by ANDs (the order
		// is important, do not move it without a valid reason).
		// "(?*?*?+?*(?+?))"
		query = query.replaceAll("\\ ", "*");

		return query;
	}

	/**
	 * Will get all the words contained in the query given in parameters.
	 * 
	 * @param query
	 *            Query from where we need to extract the data.
	 * @return All the words contained in the query.
	 */
	public static String[] extractParameters(String query) {

		List<String> whereArgsArray = new ArrayList<String>();

		// Finds all alphanumeric tokens in the query
		Pattern pattern = Pattern.compile("\\w+");
		Matcher m = pattern.matcher(query);
		while (m.find()) {
			whereArgsArray.add(m.group());
		}

		// We convert the List to an array of String.
		return (String[]) whereArgsArray.toArray(new String[whereArgsArray
				.size()]);
	}

	public static String setToSQLiteQueryArray(Set<Long> set) {
		//String statement = "";
		StringBuffer buf = new StringBuffer();
		for (Object object : set) {
			//statement = statement + "," + object.toString();
			buf.append(",");
			buf.append(object.toString());
		}
		String statement = buf.toString();
		if (statement.isEmpty()) {
			return "()";
		}
		statement = statement + ")";
		statement = "(" + statement.substring(1);
		return statement;
	}
}
