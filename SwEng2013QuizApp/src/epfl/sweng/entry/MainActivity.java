package epfl.sweng.entry;

import org.apache.http.HttpStatus;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import epfl.sweng.R;
import epfl.sweng.authentication.AuthenticationActivity;
import epfl.sweng.authentication.UserPreferences;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.patterns.QuestionsProxy;
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

	private UserPreferences mUserPreferences;

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
	 * 
	 * @param v
	 *            The View clicked
	 */
	public void onCheckboxSwitchModeClicked(View v) {
		CheckBox clickedCheckBox = (CheckBox) v;

		// Change the connection state entry in the UserPreferences
		if (clickedCheckBox.isChecked()) {
			mUserPreferences.createEntry("CONNECTION_STATE", "OFFLINE");
			TestCoordinator.check(TTChecks.OFFLINE_CHECKBOX_ENABLED);
			setDisplayView();
		} else {
			mUserPreferences.createEntry("CONNECTION_STATE", "ONLINE");
			TestCoordinator.check(TTChecks.OFFLINE_CHECKBOX_DISABLED);
			setDisplayView();
			
			// See https://github.com/sweng-epfl/sweng-2013-team-swing/issues/67
			// new AsyncSendCachedQuestion().execute(QuestionsProxy.getInstance());
		}

		// // XXX is it implemented the correct way (throw AseertionError)?
		// TODO issues #63
		// if (auditErrors() != 0) {
		// throw new AssertionError();
		// }
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
		if (!mUserPreferences.isAuthenticated()) {
			// Case LoginUsingTequila
			Toast.makeText(this, "Please Log in", Toast.LENGTH_SHORT).show();
			Intent submitAuthenticationActivityIntent = new Intent(this,
					AuthenticationActivity.class);
			startActivity(submitAuthenticationActivityIntent);
		} else {
			// Case Log out
			mUserPreferences.destroyAuthentication();
			Button logButton = (Button) findViewById(R.id.autenticationLogButton);
			logButton
					.setText(mUserPreferences.isAuthenticated() ? R.string.autenticationLoginButtonStateLogOut
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
		// create the UserPreferences which use a SharedPreference
		mUserPreferences = UserPreferences.getInstance(this
				.getApplicationContext());
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
				.setText(mUserPreferences.isAuthenticated() ? R.string.autenticationLoginButtonStateLogOut
						: R.string.autenticationLoginButtonStateLogIn);
		setDisplayView();
	}

	@Override
	protected void onStart() {
		super.onStart();
		TestCoordinator.check(TTChecks.MAIN_ACTIVITY_SHOWN);
	}

	/**
	 * Sets the view of the activity, by enabling or disabling the buttons and
	 * the checkbox according to the authentication state.
	 */
	private void setDisplayView() {
		((Button) findViewById(R.id.displayRandomQuestionButton))
				.setEnabled(mUserPreferences.isAuthenticated());
		((Button) findViewById(R.id.submitQuestionButton))
				.setEnabled(mUserPreferences.isAuthenticated());
		int visibility = mUserPreferences.isAuthenticated() ? View.VISIBLE
				: View.INVISIBLE;
		CheckBox isOffline = (CheckBox) findViewById(R.id.switchOnlineModeCheckbox);
		isOffline.setVisibility(visibility);
		isOffline.setChecked(!mUserPreferences.isConnected());	
	}

	private int auditCheckbox() {
		int numErrors = 0;
		CheckBox onfflineCheckbox = (CheckBox) this
				.findViewById(R.id.switchOnlineModeCheckbox);
		if ((onfflineCheckbox.isChecked() && !mUserPreferences.isConnected())
				|| (!onfflineCheckbox.isChecked() && !!mUserPreferences
						.isConnected())) {
			numErrors++;
		}
		return numErrors;
	}

	class AsyncSendCachedQuestion extends AsyncTask<QuestionsProxy, Void, Integer> {

		@Override
		protected Integer doInBackground(QuestionsProxy... proxy) {
			if (null != proxy && proxy.length != 1) {
				throw new IllegalArgumentException();
			}

			return proxy[0].sendCachedQuestions();
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (result != HttpStatus.SC_CREATED) {
				mUserPreferences.createEntry("CONNECTION_STATE", "OFFLINE");
				TestCoordinator.check(TTChecks.OFFLINE_CHECKBOX_ENABLED);
				Toast.makeText(
						MainActivity.this,
						getResources().getString(
								R.string.error_uploading_question),
						Toast.LENGTH_LONG).show();
			}
			TestCoordinator.check(TTChecks.NEW_QUESTION_SUBMITTED);
		}
	}
}
