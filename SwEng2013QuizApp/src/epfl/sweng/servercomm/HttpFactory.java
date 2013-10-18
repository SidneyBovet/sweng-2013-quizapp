package epfl.sweng.servercomm;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import epfl.sweng.authentication.UserCredentialsStorage;

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
		HttpGet request = new HttpGet(url);
		request.setHeader("Authorization", "Tequila "
				+ UserCredentialsStorage.getInstance().getSessionId());
		return request;
	}

	public static HttpPost getPostRequest(String url) {
		HttpPost request = new HttpPost(url);
		request.setHeader("Authorization", "Tequila "
				+ UserCredentialsStorage.getInstance().getSessionId());
		return request;
	}
}
