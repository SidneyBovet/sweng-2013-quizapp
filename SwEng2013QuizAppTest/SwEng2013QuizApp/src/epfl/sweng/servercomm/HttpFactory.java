package epfl.sweng.servercomm;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import android.util.Log;
import epfl.sweng.preferences.UserPreferences;

/**
 * Wrapper used to centralize our http urls.
 * @author born4new
 *
 */
public class HttpFactory {
	private static final String SWENG_BASE_ADDRESS =
			"https://sweng-quiz.appspot.com";
	private static final String SWENG_FETCH_QUESTION =
			"https://sweng-quiz.appspot.com/quizquestions/random";
	private static final String SWENG_LOGIN =
			"https://sweng-quiz.appspot.com/login";
	private static final String TEQUILA_LOGIN =
			"https://tequila.epfl.ch/cgi-bin/tequila/login";
	private static final String SWENG_QUERY_QUESTION =
			"https://sweng-quiz.appspot.com/search";

	/**
	 * @return the sweng server base address
	 */
	public static String getSwengBaseAddress() {
		return SWENG_BASE_ADDRESS;
	}

	/**
	 * @return the sweng server question fetching address
	 */
	public static String getSwengFetchQuestion() {
		return SWENG_FETCH_QUESTION;
	}

	public static String getSwengQueryQuestions() {
		return SWENG_QUERY_QUESTION;
	}
	
	/**
	 * @return the sweng server login address
	 */
	public static String getSwengLogin() {
		return SWENG_LOGIN;
	}

	/**
	 * @return the tequila server login address
	 */
	public static String getTequilaLogin() {
		return TEQUILA_LOGIN;
	}

	public static HttpGet getGetRequest(String url) {
        Log.i("HTTP FACTORY", "Creating GET Request " + url);
		HttpGet request = new HttpGet(url);
		
		UserPreferences storageInstance = UserPreferences.getInstance();
		if (null != storageInstance) {
			request.setHeader("Authorization", "Tequila "+ storageInstance.getSessionId());	
		}
		
		return request;
	}

	public static HttpPost getPostRequest(String url) {
        Log.i("HTTP FACTORY", "Creating POST Request " + url);
		HttpPost request = new HttpPost(url);
		
		UserPreferences storageInstance = UserPreferences.getInstance();
		if (null != storageInstance) {
			request.setHeader("Authorization", "Tequila "+
					storageInstance.getSessionId());			
		}
		
		return request;
	}
}
