package epfl.sweng.authentication;

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

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import epfl.sweng.R;
import epfl.sweng.exceptions.authentication.InvalidTokenException;
import epfl.sweng.exceptions.authentication.NoSessionIDException;
import epfl.sweng.exceptions.authentication.TequilaNoTokenException;
import epfl.sweng.servercomm.HttpFactory;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/***
 * Activity that takes care of the authentication on the Tequila EPFL server.
 * 
 * @author Merok
 * 
 */
public class AuthenticationActivity extends Activity {
	
	private static final int FOUND_CODE = 302;
	private EditText mUserNameEditText;
	private EditText mPasswordEditText;
	private Button mLoginbutton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authentication);
		setDisplayView();
		TestCoordinator.check(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
	}

	/**
	 * Sets all the view in this activity, by disabling the button, filling the
	 * <code>TextView</code>, setting the status of the loginButton using a 
	 * listener on the login and password EditText.
	 */
	
	private void setDisplayView() {
		mLoginbutton = (Button) findViewById(R.id.login_button);
		mLoginbutton.setEnabled(false);
		mUserNameEditText = (EditText) findViewById(R.id.login_user);
		mPasswordEditText = (EditText) findViewById(R.id.login_password);
		mPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);

		mUserNameEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				checkLoginButtonStatus();
			}
		});

		mPasswordEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				checkLoginButtonStatus();

			}
		});
	}

	/**
	 * Tries to update the status of the submit button according to the content
	 * of the password and login EditText.
	 */
	
	private void checkLoginButtonStatus() {
		String usrName = mUserNameEditText.getText().toString();
		String usrPassword = mPasswordEditText.getText().toString();
		mLoginbutton.setEnabled(!(usrName.length() == 0 || usrPassword
				.length() == 0));
	}

	/**
	 * Resets the layout by emptying every EditText on the Activity, and by
	 * disabling the loginButton.
	 */
	
	public void resetGUIWhenAuthenticationFails() {
		mUserNameEditText.setText("");
		mPasswordEditText.setText("");
		TestCoordinator.check(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.authentication, menu);
		return true;
	}

	/**
	 * Authenticates the user on the Tequila server.
	 * <p>
	 * Used when the login button is clicked.
	 */
	
	public void buttonAuthenticate(View view) {
		String username = ((TextView) findViewById(R.id.login_user)).getText()
				.toString();
		String password = ((TextView) findViewById(R.id.login_password))
				.getText().toString();

		if (UserPreferences.getInstance(this).isAuthenticated()) {
			Toast.makeText(this, "Already logged in!", Toast.LENGTH_LONG).show();
			Log.wtf(this.getClass().getName(),
					"User pressed 'Log in' while authenticated!?");
			finish();
		}
		
		new AsyncAuthentication().execute(username, password);
//		AuthenticationProcess ap = new AuthenticationProcess(AuthenticationActivity.this);
//		ap.execute(username, password);
//		
//		try {
//			if (ap.get() == null) {
//				resetGUIWhenAuthenticationFails();
//			} else {
//				finish();
//			}
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			e.printStackTrace();
//		}
	}
	
class AsyncAuthentication extends AsyncTask<String, Void, String> {
		
		@Override
		protected String doInBackground(String... userInfos) {
			if (null != userInfos && userInfos.length != 2) {
				throw new IllegalArgumentException();
			}
			String username = userInfos[0];
			String password = userInfos[1];
			
			String sessionId = null;
			String token = null;
			try {
				token = getToken();
				validateToken(token, username, password);
				sessionId = retrieveSessionId(token);
			} catch (TequilaNoTokenException e) {
				Log.e(this.getClass().getName(), "doInBackground(): No Token could "
						+ "be retrieved.", e);
			} catch (InvalidTokenException e) {
				Log.e(this.getClass().getName(), "doInBackground(): Token could "
						+ "not be validated.", e);
			} catch (NoSessionIDException e) {
				Log.e(this.getClass().getName(), "doInBackground(): No SessionID "
						+ "could be retrieved.", e);
			}
			
			return sessionId;
		}
		
		@Override
		protected void onPostExecute(String sessionId) {
			super.onPostExecute(sessionId);
			
			if (null == sessionId || sessionId.equals("")) {
				// XXX switch to off line mode
				// TODO meh. (change fuck this shit)
				Toast.makeText(AuthenticationActivity.this, "Fuck this shit",
						Toast.LENGTH_LONG).show();
				resetGUIWhenAuthenticationFails();
			} else {
				UserPreferences.getInstance(AuthenticationActivity.this).
					createEntry("SESSION_ID", sessionId);
				finish();
			}
		}
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
			Log.e(getClass().getName(), "getToken(): Error in the HTTP "
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
			if (response.getStatusLine().getStatusCode() != FOUND_CODE) {
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
