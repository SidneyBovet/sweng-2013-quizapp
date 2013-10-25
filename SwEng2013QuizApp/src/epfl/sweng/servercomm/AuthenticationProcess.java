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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import epfl.sweng.authentication.UserPreferences;
import epfl.sweng.exceptions.authentication.InvalidTokenException;
import epfl.sweng.exceptions.authentication.NoSessionIDException;
import epfl.sweng.exceptions.authentication.TequilaNoTokenException;

/**
 * AsyncTask that performs the networking part of authentication with EPFL's
 * Tequila server. Arguments to passed to its <code>execute()</code> method are:
 * <br/>1. Username
 * <br/>2. Password
 * 
 * @author Sidney
 * @author born4new
 * 
 */
public class AuthenticationProcess extends AsyncTask<String, Void, String> {
	// TODO Group this one with the one in EditQuestionsActivity in strings.xml
	// (need context object for this)
	private static final int HTTP_STATUS_FOUND = 302;

	private ProgressDialog mDialog;
	private Context mParentActivity;

	private String mErrorMessage;
	
	public AuthenticationProcess(Context parentActivity) {
		this.mParentActivity = parentActivity;
		this.mDialog = new ProgressDialog(parentActivity);
		mDialog.setMessage("Authenticating...");
		mDialog.setCancelable(false);
	}

	@Override
	protected void onPreExecute() {
		mDialog.show();
	}

	/**
	 * Starts a new {@link AsyncTask} to perform authentication at Tequila and
	 * SwEng's quiz server.
	 * 
	 * @param username
	 *            User's name in EPFL's Tequila system.
	 * @param password
	 *            User's password.
	 */
	
	@Override
	protected String doInBackground(String... args) {

		if (args.length != 2) {
			Log.e(this.getClass().getName(), "doInBackground(): Illegal number "
					+ "of arguments, should be (username, password).");
			throw new IllegalArgumentException("AuthenticationProcess received "
					+ args.length + "argument(s), expected 2.");
		}

		String sessionId = null;
		String token = null;
		try {
			token = getToken();
			validateToken(token, args[0], args[1]);
			sessionId = retrieveSessionId(token);
		} catch (TequilaNoTokenException e) {
			mErrorMessage = e.getMessage();
			Log.e(this.getClass().getName(), "doInBackground(): No Token could "
					+ "be retrieved.", e);
		} catch (InvalidTokenException e) {
			mErrorMessage = e.getMessage();
			Log.e(this.getClass().getName(), "doInBackground(): Token could "
					+ "not be validated.", e);
		} catch (NoSessionIDException e) {
			mErrorMessage = e.getMessage();
			Log.e(this.getClass().getName(), "doInBackground(): No SessionID "
					+ "could be retrieved.", e);
		}

		return sessionId;
	}

	@Override
	protected void onPostExecute(String result) {
		if (result != null && !result.equals("")) {
			UserPreferences.getInstance(mParentActivity).
				createEntry("SESSION_ID", result);
		} else {
			Toast.makeText(mParentActivity, mErrorMessage, Toast.LENGTH_LONG).show();
		}

		mDialog.dismiss();
	}

	/**
	 * Uses an HTTP Get to retrieve a token from EPFL's Tequila server.
	 * 
	 * @return The retrieved token to be validated.
	 */
	
	private String getToken() throws TequilaNoTokenException {

		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		HttpGet get = new HttpGet(HttpFactory.getSwengLogin());
		String token = "";
		try {
			String response = SwengHttpClientFactory.getInstance().execute(get,
					responseHandler);
			JSONObject responseJSON = new JSONObject(response);
			token = responseJSON.getString("token");
		} catch (ClientProtocolException e) {
			Log.e(this.getClass().getName(), "getToken(): Error in the HTTP "
					+ "protocol.", e);
			throw new TequilaNoTokenException("Error in the HTTP Protocol : "
					+ e.getMessage());
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "getToken(): An I/O error has "
					+ "occurred.", e);
			throw new TequilaNoTokenException("An I/O error has occurred : "
					+ e.getMessage());
		} catch (JSONException e) {
			Log.e(this.getClass().getName(), "getToken(): JSONObject "
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
	
	private void validateToken(String token, String username, String password)
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
			Log.e(this.getClass().getName(), "validateToken(): Entity does not "
					+ "support the local encoding", e);
			throw new InvalidTokenException("Entity does not support the "
					+ "local encoding : " + e.getMessage());
		}

		HttpPost tokenValidationRequest = new HttpPost(HttpFactory.getTequilaLogin());
		tokenValidationRequest.setEntity(tokenValidationContentEncoded);

		try {
			HttpResponse response = SwengHttpClientFactory.getInstance()
					.execute(tokenValidationRequest);
			if (response.getStatusLine().getStatusCode() != HTTP_STATUS_FOUND) {
				Log.e(this.getClass().getName(), "validateToken(): Tequila "
						+ "rejected username/password");
				throw new InvalidTokenException(
						"Tequila rejected username/password");
			}
		} catch (ClientProtocolException e) {
			Log.e(this.getClass().getName(), "validateToken(): Error in the "
					+ "HTTP protocol.", e);
			throw new InvalidTokenException("Error in the HTTP protocol : "
					+ e.getMessage());
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "validateToken(): An I/O error "
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
	
	private String retrieveSessionId(String token) throws NoSessionIDException {

		JSONObject postBody = new JSONObject();
		try {
			postBody.put("token", token);
		} catch (JSONException e) {
			Log.e(this.getClass().getName(), "retrieveSessionID(): JSONObject "
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
			Log.e(this.getClass().getName(), "retrieveSessionID(): Entity does "
					+ "not support the local encoding.", e);
			throw new NoSessionIDException("Entity does not support the local "
					+ "encoding : " + e.getMessage());
		} catch (ClientProtocolException e) {
			Log.e(this.getClass().getName(), "retrieveSessionID(): Error in "
					+ "the HTTP protocol.", e);
			throw new NoSessionIDException("Error in the HTTP protocol : "
					+ e.getMessage());
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "retrieveSessionID(): An I/O "
					+ "error has occurred.", e);
			throw new NoSessionIDException("An I/O error has occurred : "
					+ e.getMessage());
		} catch (JSONException e) {
			Log.e(this.getClass().getName(), "retrieveSessionID(): Error while "
					+ "parsing JSONObject \'jsonResponse\'.", e);
			throw new NoSessionIDException("Error while parsing JSONObject "
					+ "\'jsonResponse\'. : " + e.getMessage());
		}
		try {
			return jsonResponse.getString("session");
		} catch (JSONException e) {
			Log.e(this.getClass().getName(), "retrieveSessionID(): JSONObject "
					+ "\'jsonResponse\' couldn't get the \'session\' string.",
					e);
			throw new NoSessionIDException("JSONObject \'jsonResponse\' "
					+ "couldn't get the \'session\' string : " + e.getMessage());
		}
	}
}
