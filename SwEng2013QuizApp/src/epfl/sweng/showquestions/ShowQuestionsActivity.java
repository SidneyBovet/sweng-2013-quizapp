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
import epfl.sweng.backend.QuestionAgent;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.patterns.ConnectivityState;
import epfl.sweng.patterns.QuestionAgentFactory;
import epfl.sweng.patterns.QuestionsProxy;
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
	private QuestionAgent mAgent;
	
	@Override
	protected void onStart() {
		super.onStart();
		// get Intent that started this Activity
		Intent startingIntent = getIntent();
		// get the value of the user parcelable
		
		QuizQuery quizQuery = startingIntent.getParcelableExtra("QuizQuery");
		if (null == quizQuery) {
			// XXX Sidney from est ok?
			quizQuery = new QuizQuery("ShowQuestionActivity");
		}
		mAgent = QuestionAgentFactory.getAgent(this, quizQuery);
		mQuestion = fetchQuestion();
		setContentView(R.layout.activity_display_question);
		setDisplayView();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		mAgent.close();
	}

	public QuizQuestion fetchQuestion() {
		AsyncFetchQuestion asyncFetchQuestion = new AsyncFetchQuestion();
		asyncFetchQuestion.execute();
		QuizQuestion randomQuestion = null;
		try {
			randomQuestion = asyncFetchQuestion.get();
		} catch (InterruptedException e) {
			Log.wtf(this.getClass().getName(),
					"AsyncFetchQuestion was interrupted");
		} catch (ExecutionException e) {
			Log.e(this.getClass().getName(), "Process crashed");
			return null;
		}
		return randomQuestion;
	}
	/**
	 * Goes back to the state when the current activity was started.
	 * <p>
	 * Used when the next button is clicked.
	 * 
	 * @param view
	 *            Element that was clicked, which is the send button.
	 */

	public void displayAgainRandomQuestion(View view) {
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
		Button buttonNext = (Button) findViewById(R.id.buttonNext);
		buttonNext.setEnabled(false);
		
		if (mQuestion != null) {
			// setting statement
			TextView textViewQuestion = (TextView) findViewById(R.id.displayQuestion);
			textViewQuestion.setText("Question: " + mQuestion.getStatement());
			
			// setting tags
			TextView textViewTag = (TextView) findViewById(R.id.displayTags);
			textViewTag.setText(mQuestion.getTagsToString());
			
			// setting answer list
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1,
					mQuestion.getAnswers());
			ListView displayAnswers = (ListView) findViewById(R.id.displayAnswers);
			displayAnswers.setAdapter(adapter);
			
			// put answer list under listening
			AnswerSelectionListener listener = new AnswerSelectionListener(
					buttonNext, mQuestion);
			displayAnswers.setOnItemClickListener(listener);
		}
	}
	
	private final class AsyncFetchQuestion extends AsyncTask<Void, Void, QuizQuestion> {

		private ConnectivityState mStateBeforeRetrieving;
		
		@Override
		protected QuizQuestion doInBackground(Void... params) {
			mStateBeforeRetrieving = UserPreferences.getInstance(ShowQuestionsActivity.this)
					.getConnectivityState();
			return mAgent.getNextQuestion();
		}

		@Override
		protected void onPostExecute(QuizQuestion question) {
			super.onPostExecute(question);
			if (null == question) {
				TextView textViewQuestion = (TextView) findViewById(R.id.displayQuestion);
				if (QuestionsProxy.getInstance().getInboxSize() == 0
						&& mStateBeforeRetrieving == ConnectivityState.OFFLINE) {
					textViewQuestion.setText(R.string.error_cache_empty);
				} else {
					textViewQuestion.setText(R.string.error_fetching_question);
				}
				Toast.makeText(
						ShowQuestionsActivity.this,
						getResources().getString(
								R.string.error_fetching_question),
								Toast.LENGTH_LONG).show();
			}
			TestCoordinator.check(TTChecks.QUESTION_SHOWN);
		}
	}
}
