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
import android.widget.Toast;
import epfl.sweng.backend.UserCredentialsStorage;
import epfl.sweng.exceptions.authentication.InvalidatedTokenException;
import epfl.sweng.exceptions.authentication.NoSessionIDException;
import epfl.sweng.exceptions.authentication.TequilaNoTokenException;

/**
 * AsyncTask that performs the networking part of authentication with EPFL's
 * Tequila server. Arguments to passed to its <code>execute()</code> method are: <br/>
 * 1. Username<br/>
 * 2. Password<br/>
 * 
 * @author Sidney
 * @author born4new
 * 
 */
public class AuthenticationProcess extends AsyncTask<String, Void, String> {
	// TODO Group this one with the one in EditQuestionsActivity in strings.xml
	private static final int HTTP_STATUS_FOUND = 302;

	private ProgressDialog dialog;
	private Context context;

	private String errorMessage;

	// TODO Put them in Strings.xml?
	private final String[] urls = { 
		"https://sweng-quiz.appspot.com/login",
		"https://tequila.epfl.ch/cgi-bin/tequila/login" };

	public AuthenticationProcess(Context ctx) {
		this.context = ctx;
		this.dialog = new ProgressDialog(ctx);
		// TODO Strings.xml
		dialog.setMessage("Authenticating...");
		dialog.setCancelable(false);
	}

	@Override
	protected void onPreExecute() {
		dialog.show();
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
			// TODO Log it!
			System.err.println("Illegal arguments given to "
					+ "AuthenticationProcess, should be (usrname, pwd)");
			// XXX We should NOT Throw exceptions that we did not declared
			// in the method signature.
			// throw new IllegalArgumentException("AuthenticationProcess "
			// + "received " + args.length + "argument(s), expected 2.");
		}

		String sessionId = null;
		String token = null;
		try {
			token = getToken();
			validateToken(token, args[0], args[1]);
			sessionId = retrieveSessionId(token);
		} catch (TequilaNoTokenException e) {
			// TODO Log it!
			errorMessage = e.getMessage();
		} catch (InvalidatedTokenException e) {
			// TODO Log it!
			errorMessage = e.getMessage();
		} catch (NoSessionIDException e) {
			// TODO Log it!
			errorMessage = e.getMessage();
		}

		return sessionId;
	}

	@Override
	protected void onPostExecute(String result) {
		if (!result.equals("") && result != null) {
			UserCredentialsStorage.getSingletonInstanceOfStorage(context)
					.takeAuthentication(result);
			// TODO why not UserCredentialsStorage.getInstance().setSessionId()?
			Toast.makeText(context,
					"Authentication activity finished, session id = " + result,
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
		}

		dialog.dismiss();
	}

	/**
	 * Uses an HTTP Get to retrieve a token from EPFL's Tequila server.
	 * 
	 * @return The retrieved token to be validated.
	 */
	private String getToken() throws TequilaNoTokenException {

		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		HttpGet get = new HttpGet(urls[0]);
		String token = "";
		try {
			String response = SwengHttpClientFactory.getInstance().execute(get,
					responseHandler);
			JSONObject responseJSON = new JSONObject(response);
			token = responseJSON.getString("token");
		} catch (ClientProtocolException e) {
			// TODO Log it!
			throw new TequilaNoTokenException("Error in the HTTP Protocol: "
					+ e.getMessage());
		} catch (IOException e) {
			// TODO Log it!
			throw new TequilaNoTokenException("Internal Error : "
					+ e.getMessage());
		} catch (JSONException e) {
			// TODO Log it!
			throw new TequilaNoTokenException("JSON Parsing error : "
					+ e.getMessage());
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
		throws InvalidatedTokenException {

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
			// TODO Log it!
			throw new InvalidatedTokenException(
					"Local encoding not supported : " + e.getMessage());
		}

		HttpPost tokenValidationRequest = new HttpPost(urls[1]);
		tokenValidationRequest.setEntity(tokenValidationContentEncoded);

		try {
			HttpResponse response = SwengHttpClientFactory.getInstance()
					.execute(tokenValidationRequest);
			if (response.getStatusLine().getStatusCode() != HTTP_STATUS_FOUND) {
				// TODO Log it!
				throw new InvalidatedTokenException(
						"Tequila rejected username/" + "password");
			}
		} catch (ClientProtocolException e) {
			// TODO Log it!
			throw new InvalidatedTokenException("Error in the HTTP protocol"
					+ e.getMessage());
		} catch (IOException e) {
			// TODO Log it!
			throw new InvalidatedTokenException("Internal Error : "
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
			// TODO Log it!
			throw new NoSessionIDException(
					"Error putting String token into JSON" + e.getMessage());
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
			// TODO Log it!
			throw new NoSessionIDException("Local encoding not supported : "
					+ e.getMessage());
		} catch (ClientProtocolException e) {
			// TODO Log it!
			throw new NoSessionIDException("Error in the HTTP protocol : "
					+ e.getMessage());
		} catch (IOException e) {
			// TODO Log it!
			throw new NoSessionIDException("Internal error : " + e.getMessage());
		} catch (JSONException e) {
			// TODO Log it!
			throw new NoSessionIDException("Error while parsing JSONObject : "
					+ e.getMessage());
		}
		try {
			return jsonResponse.getString("session");
		} catch (JSONException e) {
			// TODO Log it!
			throw new NoSessionIDException("Tequila didn't confirm token to "
					+ "SwEng server" + e.getMessage());
		}
	}
}
