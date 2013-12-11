package epfl.sweng.showquestions;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import epfl.sweng.R;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.preferences.UserPreferences;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/***
 * Activity used to display a question to the user.
 * 
 * @author born4new
 * @author JoTearoom
 * 
 */

public class ShowQuestionsActivity extends Activity {

	private QuizQuestion mQuestion = null;
	private ShowQuestionsAgent mAgent = null;

	@Override
	protected void onStart() {
		super.onStart();
		// get Intent that started this Activity
		Intent startingIntent = getIntent();
		// get the value of the user parcelable

		QuizQuery quizQuery = startingIntent.getParcelableExtra("QuizQuery");
		if (null == quizQuery) {
			quizQuery = new QuizQuery();
		}
		mAgent = new ShowQuestionsAgent(quizQuery);
		mQuestion = fetchQuestion();
		setContentView(R.layout.activity_display_question);
		setDisplayView();
	}

	/**
	 * Fetches a new question from the {@link ShowQuestionsAgent}, and displays
	 * it.
	 * <p>
	 * Used when the next button is clicked.
	 * 
	 * @param view
	 *            Element that was clicked, which is the send button.
	 */

	public void displayNextQuestion(View view) {
		mQuestion = fetchQuestion();
		setDisplayView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_question, menu);
		return true;
	}

	/**
	 * Sets all the view in this activity, by disabling the button, filling the
	 * <code>TextView</code>, initializing the <code>ListView</code> bounded
	 * with its adapter and putting it under a listener.
	 */

	private void setDisplayView() {
		// setting button look
		Button buttonNext = (Button) findViewById(R.id.show_questions_button_next);
		buttonNext.setEnabled(false);

		if (mQuestion != null) {
			// setting statement
			TextView textViewQuestion = (TextView) findViewById(R.id.show_questions_display_question);
			textViewQuestion.setText("Question: " + mQuestion.getStatement());

			// setting tags
			TextView textViewTag = (TextView) findViewById(R.id.show_questions_display_tags);
			textViewTag.setText(mQuestion.getTagsToString());

			// setting answer list
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, mQuestion.getAnswers());
			ListView displayAnswers = (ListView) findViewById(R.id.show_questions_display_answers);
			displayAnswers.setAdapter(adapter);

			// put answer list under listening
			AnswerSelectionListener listener = new AnswerSelectionListener(
					buttonNext, mQuestion);
			displayAnswers.setOnItemClickListener(listener);
		} else {
			// clear answer list
			ListView displayAnswers = (ListView) findViewById(R.id.show_questions_display_answers);
			displayAnswers.setAdapter(null);
			
			// clear tags
			TextView textViewTag = (TextView) findViewById(R.id.show_questions_display_tags);
			textViewTag.setText(null);
		}
	}

	private QuizQuestion fetchQuestion() {
		AsyncFetchQuestion asyncFetchQuestion = new AsyncFetchQuestion();
		asyncFetchQuestion.execute();
		QuizQuestion randomQuestion = null;
		try {
			randomQuestion = asyncFetchQuestion.get();
		} catch (InterruptedException e) {
			Log.wtf(this.getClass().getName(),
					"AsyncFetchQuestion was interrupted", e);
		} catch (ExecutionException e) {
			Log.e(this.getClass().getName(), "Process crashed", e);
			return null;
		}
		return randomQuestion;
	}

	private final class AsyncFetchQuestion extends
			AsyncTask<Void, Void, QuizQuestion> {

		@Override
		protected QuizQuestion doInBackground(Void... params) {
			return mAgent.getNextQuestion();
		}

		@Override
		protected void onPostExecute(QuizQuestion question) {
			super.onPostExecute(question);
			if (null == question) {
				// if server has send nothing from the given query we go back to
				// SearchQueryActivity
				TextView textViewQuestion = (TextView) findViewById(R.id.show_questions_display_question);
				textViewQuestion.setText(R.string.error_fetching_question);
				String toastErrorMessage = "";
				if (UserPreferences.getInstance().isConnected()) {
					toastErrorMessage = getResources().getString(
									R.string.error_fetching_query_question);
				} else {
					toastErrorMessage = getResources().getString(
							R.string.error_fetching_question);
				}
				Toast.makeText(
						ShowQuestionsActivity.this,
						toastErrorMessage,
						Toast.LENGTH_LONG).show();
			}
			TestCoordinator.check(TTChecks.QUESTION_SHOWN);

		}
	}
}
