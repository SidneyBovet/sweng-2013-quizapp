package epfl.sweng.entry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;
import epfl.sweng.R;
import epfl.sweng.backend.UserCreditentialsStorage;
//import epfl.sweng.backend.UserCreditentialsStorage;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.testing.TestingTransactions;
import epfl.sweng.testing.TestingTransactions.TTChecks;

/***
 * Main activity of the quiz application, shown at launch.
 * 
 * @author born4new
 *
 */
public class MainActivity extends Activity {
	
	/**
	 * Launches the {@link ShowQuestionActivity}.
	 * <p>
	 * Used when the show random question button is clicked.
	 * 
	 * @param view Reference to the widget that was clicked.
	 */
	
	public void displayRandomQuestion(View view) {
		Toast.makeText(this, "Please answer to the following question.", Toast.LENGTH_SHORT).show();
		Intent showQuestionsActivityIntent = new Intent(this, ShowQuestionsActivity.class);
		startActivity(showQuestionsActivityIntent);
	}
	
	/**
	 * Launches the {@link EditQuestionActivity}.
	 * <p>
	 * Used when the submit a quiz button is clicked.
	 * 
	 * @param view Reference to the widget that was clicked.
	 */
	
	public void submitQuestion(View view) {
		Toast.makeText(this, "Please enter your question.", Toast.LENGTH_SHORT).show();
		Intent submitQuestionActivityIntent = new Intent(this, EditQuestionActivity.class);
		startActivity(submitQuestionActivityIntent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//create the UserCreditentialStorage which use a SharedPreference
		UserCreditentialsStorage persistentStorage =
				UserCreditentialsStorage.getSingletonInstanceOfStorage(
				this.getApplicationContext());
		int dummySessionID = 21;
		persistentStorage.takeAuthentification(dummySessionID);
		if (persistentStorage.isAuthentificated(21)) {
			Toast.makeText(this, "Authentificated", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Not Authentificated", 
					Toast.LENGTH_SHORT).show();
		}
		
		// Transaction testing.
		TestingTransactions.check(TTChecks.MAIN_ACTIVITY_SHOWN);
	}
}
