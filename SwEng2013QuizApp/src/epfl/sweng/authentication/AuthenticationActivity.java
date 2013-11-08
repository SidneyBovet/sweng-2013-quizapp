package epfl.sweng.authentication;

import android.app.Activity;
import android.app.ProgressDialog;
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
import epfl.sweng.preferences.UserPreferences;
import epfl.sweng.servercomm.AuthenticationProcess;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/***
 * Activity that takes care of the authentication on the Tequila EPFL server.
 * 
 * @author Merok
 * 
 */
public class AuthenticationActivity extends Activity {

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
		mLoginbutton
				.setEnabled(!(usrName.length() == 0 || usrPassword.length() == 0));
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
			Toast.makeText(this, "Already logged in!", Toast.LENGTH_LONG)
					.show();
			Log.wtf(this.getClass().getName(),
					"User pressed 'Log in' while authenticated!?");
			finish();
			return;
		}

		new AsyncAuthentication().execute(username, password);
	}

	/**
	 * Thread that will proceed user authentication.
	 * 
	 * @author born4new
	 * 
	 */
	class AsyncAuthentication extends AsyncTask<String, Void, String> {

		ProgressDialog mProgressDialog;
		
		public AsyncAuthentication() {
			mProgressDialog = new ProgressDialog(AuthenticationActivity.this);
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			mProgressDialog.setTitle("Authenticating...");
			mProgressDialog.setMessage("Please wait.");
			mProgressDialog.setCancelable(false);
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.show();
		}
		
		@Override
		protected String doInBackground(String... userInfos) {
			if (null != userInfos && userInfos.length != 2) {
				throw new IllegalArgumentException();
			}

			return AuthenticationProcess.authenticate(userInfos[0],
					userInfos[1]);
		}

		@Override
		protected void onPostExecute(String sessionId) {
			super.onPostExecute(sessionId);

			mProgressDialog.dismiss();
			
			if (null == sessionId || sessionId.equals("")) {
				// XXX switch to off line mode
				Toast.makeText(AuthenticationActivity.this,
						getResources().getString(R.string.error_logging_in),
						Toast.LENGTH_LONG).show();
				resetGUIWhenAuthenticationFails();
			} else {
				UserPreferences.getInstance().setSessionId(sessionId);
				finish();
			}
		}
	}
}
