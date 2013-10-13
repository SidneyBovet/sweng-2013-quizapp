package epfl.sweng.servercomm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
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

import epfl.sweng.backend.UserCredentialsStorage;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * AsyncTask that performs the networking part of authentication with EPFL's
 * Tequila server. Arguments to passed to its <code>execute()</code> method are:
 * <br/>1. Username<br/>2. Password<br/> Any call different from those will
 * throw an IllegalArgumentException.
 * 
 * @author Sidney
 * 
 */
public class AuthenticationProcess extends AsyncTask<String, Void, String> {
	private static final int HTTP_STATUS_FOUND = 302;
	private ProgressDialog dialog;
	private Context context;
	private final String[] urls = {
		"https://sweng-quiz.appspot.com/login",
		"https://tequila.epfl.ch/cgi-bin/tequila/login"};

	public AuthenticationProcess(Context ctx) {
		this.context = ctx;
		this.dialog = new ProgressDialog(ctx);
		dialog.setMessage("Authenticating...");
		dialog.setCancelable(false);
	}

	/**
	 * Starts a new {@link AsyncTask} to perform authentication at Tequila and
	 * SwEng's quiz server.
	 * 
	 * @param usrName
	 *            User's name in EPFL's Tequila system.
	 * @param password
	 *            User's password.
	 * 
	 * @throws AuthenticationException
	 *             in case authentication failed.
	 */
	public void startAuthenticationProcess(String usrName, String password)
		throws AuthenticationException {
		// this function could throw XYZ exception in case auth failed
		this.execute(usrName, password);
	}

	@Override
	protected void onPreExecute() {
		dialog.show();
	}

	@Override
	protected String doInBackground(String... args) {
		if (args.length != 2) {
			System.err.println("Illegal arguments given to "
					+ "AutehnticationProcess, should be (usrname, pwd)");
			throw new IllegalArgumentException("AuthenticationProcess "
					+ "recieved " + args.length + "argument(s), should've "
					+ "been 2.");
		}
		
		String token = getToken();
		validateToken(token, args[0], args[1]);
		String sessionId = retrieveSessionId(token);

		return sessionId;
	}

	@Override
	protected void onPostExecute(String result) {
		if (result.equals("") || null == result) {
			// XXX normal error flow shouldn't bring here: exception handled
			// before
			// TODO Call Aymeric's log architecture and David's errorHandle
			// function
		} else {
			UserCredentialsStorage.getSingletonInstanceOfStorage(context)
					.takeAuthentication(result);
			// TODO why not UserCredentialsStorage.getInstance().setSessionId()?
		}
		dialog.dismiss();
		Toast.makeText(
				context,
				"Authentication activity finished, session id = " + result,
				Toast.LENGTH_LONG).show();
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
	private String retrieveSessionId(String token) {
		JSONObject postBody = new JSONObject();
		try {
			postBody.put("token", token);
		} catch (JSONException e) {
			// XXX shouldn't be reached; exception should've been thrown earlier
			// XXX error putting String token into JSON -> not recoverable
			// TODO Call Aymeric's log architecture and David's errorHandle
			// function
			e.printStackTrace();
		}

		JSONObject jsonResponse = new JSONObject();

		try {
			HttpPost postRequest = new HttpPost(urls[0]);
			postRequest.setEntity(new StringEntity(postBody.toString()));
			postRequest.setHeader("Content-type", "application/json");
			ResponseHandler<String> handler = new BasicResponseHandler();
			String response = SwengHttpClientFactory.getInstance().execute(
					postRequest, handler);
			jsonResponse = new JSONObject(response);
		} catch (UnsupportedEncodingException e) {
			// XXX local encoding not supported -> not recoverable + nothing to
			// do
			// TODO Call Aymeric's log architecture and David's errorHandle
			// function
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// XXX error in the HTTP protocol -> not recoverable
			// TODO Call Aymeric's log architecture and David's errorHandle
			// function
			e.printStackTrace();
		} catch (IOException e) {
			// XXX general error with server -> not recoverable
			// TODO Call Aymeric's log architecture and David's errorHandle
			// function
			e.printStackTrace();
		} catch (JSONException e) {
			// XXX error while parsing JSONObject
			// (server answered with corrupted JSON) -> not recoverable
			// TODO Call Aymeric's log architecture and David's errorHandle
			// function
			e.printStackTrace();
		}
		try {
			return jsonResponse.getString("session");
		} catch (JSONException e) {
			// XXX error while looking for "session" in JSON
			// (Tequila didn't confirm token to SwEng sever) -> not recoverable
			// TODO Call Aymeric's log architecture and David's errorHandle
			// function
			e.printStackTrace();
		}
		// XXX this part of the code should never be reached
		return "";
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
	private void validateToken(String token, String username, String password) {
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
			// XXX local encoding not supported -> not recoverable + nothing to
			// do
			// TODO Call Aymeric's log architecture and David's errorHandle
			// function
			e.printStackTrace();
		}

		HttpPost tokenValidationRequest = new HttpPost(urls[1]);
		tokenValidationRequest.setEntity(tokenValidationContentEncoded);

		try {
			HttpResponse response = SwengHttpClientFactory.getInstance()
					.execute(tokenValidationRequest);
			if (response.getStatusLine().getStatusCode() != HTTP_STATUS_FOUND) {
				// XXX Tequila rejected usrname/pwd -> not recoverable + tell to
				// retry
				// TODO Call Aymeric's log architecture and David's errorHandle
				// function
			}
		} catch (ClientProtocolException e) {
			// XXX error in the HTTP protocol -> not recoverable + tell to retry
			// later
			// TODO Call Aymeric's log architecture and David's errorHandle
			// function
			e.printStackTrace();
		} catch (IOException e) {
			// XXX general error with server -> not recoverable + tell to retry
			// later
			// TODO Call Aymeric's log architecture and David's errorHandle
			// function
			e.printStackTrace();
		}
	}

	/**
	 * Uses an HTTP Get to retrieve a token from EPFL's Tequila server.
	 * 
	 * @return The retrieved token to be validated.
	 */
	private String getToken() {
		ResponseHandler<String> responseHandler = new BasicResponseHandler();

		HttpGet get = new HttpGet(urls[0]);
		String token = "";
		try {
			String response = SwengHttpClientFactory.getInstance().execute(get,
					responseHandler);
			JSONObject responseJSON = new JSONObject(response);
			token = responseJSON.getString("token");
		} catch (ClientProtocolException e) {
			// XXX error in the HTTP protocol -> not recoverable + tell to retry
			// later
			// TODO Call Aymeric's log architecture and David's errorHandle
			// function
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// XXX general error with server -> not recoverable + tell to retry
			// later
			// TODO Call Aymeric's log architecture and David's errorHandle
			// function
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			// XXX error while parsing JSONObject
			// (no "token" field or corrupted JSON) -> not recoverable + tell to
			// retry later
			// TODO Call Aymeric's log architecture and David's errorHandle
			// function
			e.printStackTrace();
			return null;
		}
		return token;
	}
}
