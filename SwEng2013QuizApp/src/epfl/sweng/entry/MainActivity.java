package epfl.sweng.entry;

import org.apache.http.HttpStatus;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import epfl.sweng.R;
import epfl.sweng.authentication.AuthenticationActivity;
import epfl.sweng.caching.CacheContentProvider;
import epfl.sweng.comm.ConnectivityState;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.preferences.UserPreferences;
import epfl.sweng.searchquestions.SearchActivity;
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

		setDisplayView();

		// Notify the change of connectivity state to the proxy
		AsyncProxyConnectivityNotifier asyncProxyNotifier
			= new AsyncProxyConnectivityNotifier();
		asyncProxyNotifier.execute(clickedCheckBox.isChecked());

		if (auditErrors() != 0) {
			System.out.println("SISI");
			throw new AssertionError();
		}
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
			Button logButton = (Button) findViewById(R.id.autentication_log_button);
			logButton
					.setText(mUserPreferences.isAuthenticated()
							? R.string.auth_login_button_state_log_out
							: R.string.auth_login_button_state_log_in);
			setDisplayView();
			TestCoordinator.check(TTChecks.LOGGED_OUT);
		}
	}

	public void searchActivity(View view) {
		Intent searchActivityIntent = new Intent(this, SearchActivity.class);
		startActivity(searchActivityIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	        	Log.v(this.getClass().getName(), "No settings options for now.");
	            return true;
	        case R.id.erase_database:
	        	Log.v(this.getClass().getName(), "Wiping whole database!");
	        	CacheContentProvider prov = new CacheContentProvider(true);
	        	prov.eraseDatabase();
	        	prov.close();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
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
		mUserPreferences = UserPreferences.getInstance();
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
		Button logButton = (Button) findViewById(R.id.autentication_log_button);
		logButton
				.setText(mUserPreferences.isAuthenticated()
						? R.string.auth_login_button_state_log_out
						: R.string.auth_login_button_state_log_in);
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
		((Button) findViewById(R.id.display_activity_button))
				.setEnabled(mUserPreferences.isAuthenticated());
		((Button) findViewById(R.id.submit_activity_button))
				.setEnabled(mUserPreferences.isAuthenticated());
		((Button) findViewById(R.id.search_activity_button))
		.setEnabled(mUserPreferences.isAuthenticated());
		int visibility = mUserPreferences.isAuthenticated() ? View.VISIBLE
				: View.INVISIBLE;
		CheckBox isOffline = (CheckBox) findViewById(R.id.switch_offline_mode_checkbox);
		isOffline.setVisibility(visibility);
		isOffline.setChecked(!mUserPreferences.isConnected());
	}

	private int auditCheckbox() {
		int numErrors = 0;
		CheckBox onfflineCheckbox = (CheckBox) this
				.findViewById(R.id.switch_offline_mode_checkbox);
		if ((!onfflineCheckbox.isChecked() && !mUserPreferences.isConnected())
				|| (onfflineCheckbox.isChecked() && !!mUserPreferences
						.isConnected())) {
			numErrors++;
		}
		return numErrors;
	}

	private final class AsyncProxyConnectivityNotifier extends
			AsyncTask<Boolean, Void, Integer> {
		
		@Override
		protected Integer doInBackground(Boolean... state) {
			if (null == state || state.length != 1) {
				throw new IllegalArgumentException("Should be only one state.");
			}
			
			// Change the connection state entry in the UserPreferences
			// state true = offline box checked
			if (null != state[0] && state[0].booleanValue()) {
				return mUserPreferences.setConnectivityState(ConnectivityState.ONLINE);
			} else {
				return mUserPreferences.setConnectivityState(ConnectivityState.OFFLINE);
			}

		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			
			CheckBox isOffline = (CheckBox) findViewById(R.id.switch_offline_mode_checkbox);
			isOffline.setChecked(!mUserPreferences.isConnected());
			switch (result) {
				
				case HttpStatus.SC_CREATED:
					TestCoordinator.check(TTChecks.OFFLINE_CHECKBOX_DISABLED);
					break;
				case HttpStatus.SC_OK:
					TestCoordinator.check(TTChecks.OFFLINE_CHECKBOX_ENABLED);
					break;
				
				default: // Http code error
					Toast.makeText(
							MainActivity.this,
							getResources().getString(
									R.string.error_uploading_question),
							Toast.LENGTH_LONG).show();
					
					if (mUserPreferences.isConnected()) {
						TestCoordinator.check(TTChecks.OFFLINE_CHECKBOX_DISABLED);
					} else {
						TestCoordinator.check(TTChecks.OFFLINE_CHECKBOX_ENABLED);
					}
					break;
			}
		}
	}
}
