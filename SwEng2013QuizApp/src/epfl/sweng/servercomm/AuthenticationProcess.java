package epfl.sweng.servercomm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import epfl.sweng.exceptions.authentication.InvalidTokenException;
import epfl.sweng.exceptions.authentication.NoSessionIDException;
import epfl.sweng.exceptions.authentication.TequilaNoTokenException;

/**
 * This class is used to authenticate the user. It is not a thread.
 * @author born4new
 *
 */
public final class AuthenticationProcess {
	
	private static final int FOUND_CODE = 302;
	
	private AuthenticationProcess() {
		
	}
	
	public static String authenticate(String username, String password) {
		String sessionId = null;
		String token = null;
		
		try {
			token = getToken();
			validateToken(token, username, password);
			sessionId = retrieveSessionId(token);
		} catch (TequilaNoTokenException e) {
			Log.e("AuthenticationProcess", "doInBackground(): No Token could "
					+ "be retrieved.", e);
		} catch (InvalidTokenException e) {
			Log.e("AuthenticationProcess", "doInBackground(): Token could "
					+ "not be validated.", e);
		} catch (NoSessionIDException e) {
			Log.e("AuthenticationProcess", "doInBackground(): No SessionID "
					+ "could be retrieved.", e);
		}
		
		return sessionId;
	}
	
	
	/**
	 * Uses an HTTP Get to retrieve a token from EPFL's Tequila server.
	 * 
	 * @return The retrieved token to be validated.
	 */
	
	private static String getToken() throws TequilaNoTokenException {
	
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		HttpGet get = new HttpGet(HttpFactory.getSwengLogin());
		String token = "";
		try {
			String response = SwengHttpClientFactory.getInstance().execute(get,
					responseHandler);
			JSONObject responseJSON = new JSONObject(response);
			token = responseJSON.getString("token");
		} catch (ClientProtocolException e) {
			Log.e("AuthenticationProcess", "getToken(): Error in the HTTP "
					+ "protocol.", e);
			throw new TequilaNoTokenException("Error in the HTTP Protocol : "
					+ e.getMessage());
		} catch (IOException e) {
			Log.e("AuthenticationProcess", "getToken(): An I/O error has "
					+ "occurred.", e);
			throw new TequilaNoTokenException("An I/O error has occurred : "
					+ e.getMessage());
		} catch (JSONException e) {
			Log.e("AuthenticationProcess", "getToken(): JSONObject "
					+ "\'responseJSON\' could not get the \'token\' string.", e);
			throw new TequilaNoTokenException("JSONObject \'responseJSON\' "
					+ "could not get the \'token\' string. : " + e.getMessage());
		}
		return token;
	}
	
	/**
	 * Proceeds to validate the token given by EPFL's Tequila server by
	 * communicating its associated username/password combination. (uses HTTPS
	 * protocol)
	 * 
	 * @param token
	 *            The token previously given by the Tequila server.
	 * @param username
	 *            The user's name in EPFL's Tequila system
	 * @param password
	 *            The user's password in EPFL's Tequila system.
	 */
	
	private static void validateToken(String token, String username, String password)
		throws InvalidTokenException {
	
		List<NameValuePair> tokenValidationContentList = new ArrayList<NameValuePair>();
		tokenValidationContentList.add(new BasicNameValuePair("requestkey",
				token));
		tokenValidationContentList.add(new BasicNameValuePair("username",
				username));
		tokenValidationContentList.add(new BasicNameValuePair("password",
				password));
	
		UrlEncodedFormEntity tokenValidationContentEncoded = null;
		try {
			tokenValidationContentEncoded = new UrlEncodedFormEntity(
					tokenValidationContentList);
		} catch (UnsupportedEncodingException e) {
			Log.e("AuthenticationProcess", "validateToken(): Entity does not "
					+ "support the local encoding", e);
			throw new InvalidTokenException("Entity does not support the "
					+ "local encoding : " + e.getMessage());
		}
	
		HttpPost tokenValidationRequest = new HttpPost(HttpFactory.getTequilaLogin());
		tokenValidationRequest.setEntity(tokenValidationContentEncoded);
	
		try {
			HttpResponse response = SwengHttpClientFactory.getInstance()
					.execute(tokenValidationRequest);
			if (response.getStatusLine().getStatusCode() != FOUND_CODE) {
				Log.e("AuthenticationProcess", "validateToken(): Tequila "
						+ "rejected username/password");
				throw new InvalidTokenException(
						"Tequila rejected username/password");
			}
		} catch (ClientProtocolException e) {
			Log.e("AuthenticationProcess", "validateToken(): Error in the "
					+ "HTTP protocol.", e);
			throw new InvalidTokenException("Error in the HTTP protocol : "
					+ e.getMessage());
		} catch (IOException e) {
			Log.e("AuthenticationProcess", "validateToken(): An I/O error "
					+ "has occurred.", e);
			throw new InvalidTokenException("An I/O error has occurred : "
					+ e.getMessage());
		}
	}

	/**
	 * Retrieves session id from SwEng's quiz server.
	 * 
	 * @param token
	 *            The token previously validated to EPFL's Tequila server.
	 * @return The session id returned by SwEng's quiz server if everything went
	 *         well, the empty string otherwise.
	 * 
	 * @see validateToken()
	 * @see getToken()
	 */
	private static String retrieveSessionId(String token) throws NoSessionIDException {
	
		JSONObject postBody = new JSONObject();
		try {
			postBody.put("token", token);
		} catch (JSONException e) {
			Log.e("AuthenticationProcess", "retrieveSessionID(): JSONObject "
					+ "\'postBody\' could not write on the string \'token\'.",
					e);
			throw new NoSessionIDException(
					"JSONObject \'postBody\' could not write on the string "
					+ "\'token\'. : " + e.getMessage());
		}
	
		JSONObject jsonResponse = new JSONObject();
	
		try {
			HttpPost postRequest = new HttpPost(HttpFactory.getSwengLogin());
			postRequest.setEntity(new StringEntity(postBody.toString()));
			postRequest.setHeader("Content-type", "application/json");
			ResponseHandler<String> handler = new BasicResponseHandler();
			String response = SwengHttpClientFactory.getInstance().execute(
					postRequest, handler);
			jsonResponse = new JSONObject(response);
		} catch (UnsupportedEncodingException e) {
			Log.e("AuthenticationProcess", "retrieveSessionID(): Entity does "
					+ "not support the local encoding.", e);
			throw new NoSessionIDException("Entity does not support the local "
					+ "encoding : " + e.getMessage());
		} catch (ClientProtocolException e) {
			Log.e("AuthenticationProcess", "retrieveSessionID(): Error in "
					+ "the HTTP protocol.", e);
			throw new NoSessionIDException("Error in the HTTP protocol : "
					+ e.getMessage());
		} catch (IOException e) {
			Log.e("AuthenticationProcess", "retrieveSessionID(): An I/O "
					+ "error has occurred.", e);
			throw new NoSessionIDException("An I/O error has occurred : "
					+ e.getMessage());
		} catch (JSONException e) {
			Log.e("AuthenticationProcess", "retrieveSessionID(): Error while "
					+ "parsing JSONObject \'jsonResponse\'.", e);
			throw new NoSessionIDException("Error while parsing JSONObject "
					+ "\'jsonResponse\'. : " + e.getMessage());
		}
		try {
			return jsonResponse.getString("session");
		} catch (JSONException e) {
			Log.e("AuthenticationProcess", "retrieveSessionID(): JSONObject "
					+ "\'jsonResponse\' couldn't get the \'session\' string.",
					e);
			throw new NoSessionIDException("JSONObject \'jsonResponse\' "
					+ "couldn't get the \'session\' string : " + e.getMessage());
		}
	}
	
}

