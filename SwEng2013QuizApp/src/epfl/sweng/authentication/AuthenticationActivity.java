package epfl.sweng.authentication;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
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
import epfl.sweng.servercomm.AuthenticationProcess;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/***
 * * This class will take care of the authentication to the Tequila EPFL server.
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
	 * TextView, setting the status of the loginButton using a listener on the
	 * login and password EditText.
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
		// XXX Ici je suis parti du principe que le bouton est desactivé si on
		// remplit les fields avec des espaces. A changer ?
		mLoginbutton.setEnabled(!(usrName.trim().length() == 0 || usrPassword
				.trim().length() == 0));
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

	public void buttonAuthenticate(View view) {
		String usrName = ((TextView) findViewById(R.id.login_user)).getText()
				.toString();
		String password = ((TextView) findViewById(R.id.login_password))
				.getText().toString();

		if (UserCredentialsStorage.getInstance(this).isAuthenticated()) {
			Toast.makeText(this, "Already logged in!", Toast.LENGTH_LONG).show();
			Log.wtf(this.getClass().getName(),
					"User pressed 'Log in' while authenticated!?");
			finish();
		}
		
		AuthenticationProcess ap = new AuthenticationProcess(AuthenticationActivity.this);
		ap.execute(usrName, password);
		
		try {
			if (ap.get() == null) {
				resetGUIWhenAuthenticationFails();
			} else {
				finish();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}
