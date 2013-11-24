package epfl.sweng.searchquestions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import epfl.sweng.R;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * Activity that takes care of the Tags searching.
 * 
 * @author Merok
 * 
 */
public class SearchActivity extends Activity {
	
	private QuizQuery mQuery;
	private Button mSubmitQuery;
	private EditText mQueryField;
	private String mQueryFieldText;
	
	private final static int QUERY_MAX_LENGTH = 500;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setDisplayView();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		TestCoordinator.check(TTChecks.SEARCH_ACTIVITY_SHOWN);
	}
	
	/**
	 * Creates a query that will be sent and processed by the server.
	 * <p>
	 * Used when the search button is clicked.
	 */
	
	public void createQuery(View view) {
		QuizQuery quizQuery = new QuizQuery(mQueryFieldText, null);
		resetQuerySearchField();
		sendToShowQuestionsActivity(quizQuery);
	}
	
	/**
	 * Sets all the views in this activity, by disabling the <code>Search
	 * Button</code>, and setting a listener on the <code>Query Field</code>.
	 */
	
	private void setDisplayView() {
	
		mSubmitQuery = (Button) findViewById(R.id.submit_query_button);
		mQueryField = (EditText) findViewById(R.id.search_question_query);
		mSubmitQuery.setEnabled(false);
		mQueryFieldText = "";
	
		mQueryField.addTextChangedListener(new TextWatcher() {
	
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// Nothing to do
	
			}
	
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// Nothing to do
	
			}
	
			@Override
			public void afterTextChanged(Editable s) {
				// Proceed only if there has been user changes.
	
				if (!mQueryFieldText.equals(s.toString())) {
					mQueryFieldText = s.toString();
					updateSearchButton();
					TestCoordinator.check(TTChecks.QUERY_EDITED);
				}
			}
		});
	}

	/**
	 * Tries to update the status of the submit query button according to the
	 * content of the queryField EditText.
	 */
	
	private void updateSearchButton() {
		mQuery = new QuizQuery(mQueryFieldText, null);
		// && QuizQuery.isQueryValid(mQueryFieldText));
		mSubmitQuery.setEnabled(mQueryFieldText.length() != 0
				&& mQueryFieldText.length() < QUERY_MAX_LENGTH
				&& mQuery.hasGoodSyntax(mQueryFieldText));
		mSubmitQuery.setEnabled(mQueryFieldText.length() != 0);
	}
	
	private void sendToShowQuestionsActivity(QuizQuery quizQuery) {
		Intent displayActivityIntent = new Intent(this, ShowQuestionsActivity.class);	
		displayActivityIntent.putExtra("QuizQuery", quizQuery);
		startActivity(displayActivityIntent);
	}
	
	/**
	 * Resets the layout by emptying the queryField on the Activity, and by
	 * disabling the <code>Search Button</code>.
	 */
	
	private void resetQuerySearchField() {
		mQueryFieldText = "";
		mQueryField.setText("");
		updateSearchButton();
	}
}
