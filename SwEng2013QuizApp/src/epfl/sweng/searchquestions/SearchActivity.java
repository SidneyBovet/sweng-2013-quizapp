package epfl.sweng.searchquestions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.textservice.SentenceSuggestionsInfo;
import android.widget.Button;
import android.widget.EditText;
import epfl.sweng.R;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.patterns.JsonToQuestionsAdapter;
import epfl.sweng.patterns.QuestionsProxy;
import epfl.sweng.quizquestions.QuizQuestion;
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
		mQuery = new QuizQuery(mQueryFieldText);
		// && QuizQuery.isQueryValid(mQueryFieldText));
		mSubmitQuery.setEnabled(mQueryFieldText.length() != 0
				&& mQueryFieldText.length() < 500
				&& mQuery.hasGoodSyntax(mQueryFieldText));
	}

	/**
	 * Send the Query to the server and process accordingly.
	 */
	public void sendQuery(View view) {
		QuestionsProxy.getInstance(this);
		AsyncSearchForQuestions asyncFetchSearchQuestion = new AsyncSearchForQuestions();
		asyncFetchSearchQuestion.execute(mQuery);
		List<QuizQuestion> searchQuestions = null;
		try {
			searchQuestions = asyncFetchSearchQuestion.get();
		} catch (InterruptedException e) {
			Log.wtf(this.getClass().getName(),
					"AsyncFetchQuestion was interrupted");
		} catch (ExecutionException e) {
			// TestCoordinator.check(TTChecks.QUESTION_SHOWN);
			Log.e(this.getClass().getName(), "Process crashed");
			return;
		} finally {
			if (null == searchQuestions) {
				// TestCoordinator.check(TTChecks.QUESTION_SHOWN);
			}
		}
		resetQuerySearchField();
		sendToShowQuestionActivity(searchQuestions);
	}

	private void sendToShowQuestionActivity(List<QuizQuestion> questions) {
		Intent displayActivityIntent = new Intent(this,
				ShowQuestionsActivity.class);
		displayActivityIntent.putParcelableArrayListExtra("Questions",
				(ArrayList<QuizQuestion>) questions);
		startActivity(displayActivityIntent);
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
			AsyncTask<QuizQuery, Void, List<QuizQuestion>> {

		@Override
		protected List<QuizQuestion> doInBackground(QuizQuery... queries) {
			if (null == queries || queries.length != 1) {
				throw new IllegalArgumentException();
			}

			return JsonToQuestionsAdapter.retrieveQuizQuestions(queries[0]);
		}

		@Override
		protected void onPostExecute(List<QuizQuestion> questions) {
			super.onPostExecute(questions);

			// TODO Additional manipulations, such as TTCHECKS, etc
		}
	}
}
