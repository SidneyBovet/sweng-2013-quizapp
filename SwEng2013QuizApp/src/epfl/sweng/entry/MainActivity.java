package epfl.sweng.entry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import epfl.sweng.R;
import epfl.sweng.authentication.AuthenticationActivity;
import epfl.sweng.authentication.UserCredentialsStorage;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/***
 * Main activity of the quiz application, shown at launch.
 * 
 * @author born4new, JoTearoom
 * 
 */
public class MainActivity extends Activity {

	private UserCredentialsStorage mPersistentStorage;
	private boolean isOffline = false;

	/**
	 * Launches the {@link ShowQuestionActivity}.
	 * <p>
	 * Used when the show random question button is clicked.
	 * 
	 * @param view
	 *            Reference to the widget that was clicked.
	 */

	public void displayRandomQuestion(View view) {
		Intent showQuestionsActivityIntent = new Intent(this,
				ShowQuestionsActivity.class);
		startActivity(showQuestionsActivityIntent);
	}

	/**
	 * Launches the {@link EditQuestionActivity}.
	 * <p>
	 * Used when the submit a quiz button is clicked.
	 * 
	 * @param view
	 *            Reference to the widget that was clicked.
	 */

	public void submitQuestion(View view) {
		Intent submitQuestionActivityIntent = new Intent(this,
				EditQuestionActivity.class);
		startActivity(submitQuestionActivityIntent);
	}

	/**
	 * Function called back on checkbox activation. Handles the <i>Offline</i>
	 * state of the whole application.
	 * @param v The View clicked
	 */
	public void onCheckboxSwitchModeClicked(View v) {
		assert v instanceof CheckBox;
		CheckBox clickedCheckBox = (CheckBox) v;

		// XXX why assert doesn't breaks?
		//isOffline = !isOffline;
		
		Toast.makeText(this,
				"Assert: ("+clickedCheckBox.isChecked()+"&&"+isOffline+")||(" +
						!clickedCheckBox.isChecked()+"&&"+!isOffline+")",
				Toast.LENGTH_SHORT).show();

		assert 	(clickedCheckBox.isChecked() && isOffline) ||
				(!clickedCheckBox.isChecked() && !isOffline);
	}
	
	
	/**
	 * Launches the {@link AuthenticationActivity} when not authenticated.
	 * Releases the authentication and refreshes the view when authenticated.
	 * <p>
	 * Used when the login/logout button is clicked.
	 * 
	 * @param view
	 */
	
	public void displayAuthenticationActivity(View view) {
		if (!mPersistentStorage.isAuthenticated()) {
			// Case LoginUsingTequila
			Toast.makeText(this, "Please Log in", Toast.LENGTH_SHORT).show();
			Intent submitAuthenticationActivityIntent = new Intent(this,
					AuthenticationActivity.class); 
			startActivity(submitAuthenticationActivityIntent); 
		} else {
			// Case Log out
			mPersistentStorage.destroyAuthentication();
			// TODO ne devrait pas se faire au chargement initial de
			// l'application
			Button logButton = (Button) findViewById(R.id.autenticationLogButton);
			logButton
					.setText(mPersistentStorage.isAuthenticated() ? R.string.autenticationLoginButtonStateLogOut
							: R.string.autenticationLoginButtonStateLogIn);
			setDisplayView();
			TestCoordinator.check(TTChecks.LOGGED_OUT);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public int auditErrors() {
		int numErrors = 0;
		
		numErrors += auditCheckbox();
		
		return numErrors;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Transaction testing.
		// create the UserCreditentialStorage which use a SharedPreference
		mPersistentStorage = UserCredentialsStorage
				.getInstance(this.getApplicationContext());
	}

	/**
	 * Checks the authentication state and change the text on the log button and
	 * the authenticated boolean in response.
	 * <p>
	 * Called when the app is first launched and when we return to it from
	 * another activity.
	 */
	
	@Override
	protected void onResume() {
		super.onResume();
		Button logButton = (Button) findViewById(R.id.autenticationLogButton);
		logButton
				.setText(mPersistentStorage.isAuthenticated() ? R.string.autenticationLoginButtonStateLogOut
						: R.string.autenticationLoginButtonStateLogIn);
		setDisplayView();
		TestCoordinator.check(TTChecks.MAIN_ACTIVITY_SHOWN);
	}

	/**
	 * Sets the view of the activity, by enabling or disabling the buttons
	 * according to the authentication state.
	 */
	
	private void setDisplayView() {
		((Button) findViewById(R.id.displayRandomQuestionButton))
				.setEnabled(mPersistentStorage.isAuthenticated());
		((Button) findViewById(R.id.submitQuestionButton))
				.setEnabled(mPersistentStorage.isAuthenticated());
	}

	private int auditCheckbox() {
		int numErrors = 0;
		CheckBox onfflineCheckbox =
				(CheckBox) this.findViewById(R.id.switchOnlineModeCheckbox);
		if ((onfflineCheckbox.isChecked() && isOffline) ||
				(!onfflineCheckbox.isChecked() && !isOffline)) {
			numErrors++;
		}
		return numErrors;
	}
}
