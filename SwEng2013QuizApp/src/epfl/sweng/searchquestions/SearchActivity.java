package epfl.sweng.searchquestions;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import epfl.sweng.R;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.patterns.QuestionsProxy;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * Activity that takes care of the Tags searching.
 * 
 * @author Merok
 * 
 */
public class SearchActivity extends Activity {
	private Button mSubmitQuery;
	private EditText mQueryField;
	private String mQueryFieldText;

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
	 * Sets all the view in this activity, by disabling the button, filling the
	 * <code>TextView</code>, setting the status of the mSubmitQuery Button
	 * using a listener on Query Filed EditText.
	 */
	public void setDisplayView() {

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

		mSubmitQuery.setEnabled((mQueryFieldText.length() != 0)
				&& QuizQuery.isQueryValid(mQueryFieldText));
	}

	private boolean checkQuery() {
		// verify : no characters other than alphanumeric characters,
		// ' ',(,),*,+
		boolean expectedChara = mQueryFieldText
				.matches("(?:[a-zA-Z0-9])+(?:\\+|\\*|\\s|\\(|\\))*");
		// verifiy that the syntax is correct: (i.e banana++* is not accepted)
		boolean correctSyntax = hasGoodSyntax();
		// verifiy that the nested parenthesis are correct: (i.e (banana)) is
		// not accepted)
		boolean correctNested = isWellNested();
	}

	private boolean hasGoodSyntax() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isWellNested() {
		String onlyParenthesiString = mQueryFieldText.replaceAll("[^\\(\\)]",
				"");

		if (mQueryFieldText.length() % 2 != 0) {
			return false;
		}

		String[] array = onlyParenthesiString.split("");
		int nestedCounter = 0;
		for (String c : array) {
			nestedCounter += c.equals("(") ? 1 : -1;
			if (nestedCounter < 0) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Send the Query to the server and process accordingly.
	 */
	public void sendQuery(View view) {
		new AsyncSearchForQuestions().execute(mQueryFieldText);
		resetQuerySearchField();
	}

	/**
	 * Resets the layout by emptying the queryField on the Activity, and by
	 * disabling the sendQuery Button.
	 */
	private void resetQuerySearchField() {
		mQueryFieldText = "";
		mQueryField.setText("");
	}

	/**
	 * Sends the query to the server and get a list of questions to display.
	 * 
	 * @author born4new
	 * 
	 */
	private final class AsyncSearchForQuestions extends
			AsyncTask<String, Void, List<QuizQuestion>> {

		@Override
		protected List<QuizQuestion> doInBackground(String... queries) {
			if (null != queries && queries.length != 1) {
				throw new IllegalArgumentException();
			}

			JSONObject jsonQuery = new JSONObject();
			try {
				jsonQuery.put("query", queries[0]);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return QuestionsProxy.getInstance()
					.retrieveQuizQuestions(jsonQuery);
		}

		@Override
		protected void onPostExecute(List<QuizQuestion> questions) {
			super.onPostExecute(questions);
			// if (result != HttpStatus.SC_CREATED) {
			// Toast.makeText(
			// SearchActivity.this,
			// getResources().getString(
			// R.string.error_uploading_question),
			// Toast.LENGTH_LONG).show();
			// }
			// TestCoordinator.check(TTChecks.NEW_QUESTION_SUBMITTED);
		}
	}
}
