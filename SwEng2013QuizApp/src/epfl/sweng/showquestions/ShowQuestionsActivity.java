package epfl.sweng.showquestions;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import epfl.sweng.R;
import epfl.sweng.patterns.ConnectivityState;
import epfl.sweng.patterns.QuestionsProxy;
import epfl.sweng.preferences.UserPreferences;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/***
 * Activity used to display a random question to the user.
 * 
 * @author born4new
 * @author JoTearoom
 * 
 */

public class ShowQuestionsActivity extends Activity {
	QuizQuestion mQuestion = null;
	DisplayState mState = DisplayState.RANDOM;
	ArrayList<QuizQuestion> mQuestions;
	
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    // get Intent that started this Activity
	    Intent startingIntent = getIntent();
	    // get the value of the user string
	    mQuestions = startingIntent.getParcelableArrayListExtra("Questions");
	    if (mQuestions != null) {
	    	mState = DisplayState.QUERY;
	    } else {
	    	mState = DisplayState.RANDOM;
	    }
		setContentView(R.layout.activity_display_question);
		setDisplayView();
	}

	public QuizQuestion getRandomQuestion(){
		AsyncRetrieveQuestion asyncFetchQuestion = new AsyncRetrieveQuestion();
		asyncFetchQuestion.execute();
		QuizQuestion randomQuestion = null;
		try {
			randomQuestion = asyncFetchQuestion.get();
		} catch (InterruptedException e) {
			Log.wtf(this.getClass().getName(),
					"AsyncFetchQuestion was interrupted");
		} catch (ExecutionException e) {
			// TestCoordinator.check(TTChecks.QUESTION_SHOWN);
			Log.e(this.getClass().getName(), "Process crashed");
			return null;
		} finally {
			if (null == randomQuestion) {
				// TestCoordinator.check(TTChecks.QUESTION_SHOWN);
				return null;
			}
		}
		return randomQuestion;
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
		
		if (mState == DisplayState.QUERY){
			if(mQuestions.size() > 0){
				mQuestion = mQuestions.remove(0);
			}else{
				mState = DisplayState.RANDOM;
				mQuestion = getRandomQuestion();
			}
		} else {
			mQuestion = getRandomQuestion();
		}

		// setting tags
		TextView textViewQuestion = (TextView) findViewById(R.id.displayQuestion);
		textViewQuestion.setText("Question: " + randomQuestion.getStatement());

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

	/**
	 * Goes back to the state when the current activity was started.
	 * 
	 * @param view
	 *            Element that was clicked, which is the send button.
	 */

	public void displayAgainRandomQuestion(View view) {
		setDisplayView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_question, menu);
		return true;
	}

	class AsyncRetrieveQuestion extends AsyncTask<Void, Void, QuizQuestion> {

		private boolean mWasDisconnectedBeforeRetrieving;
		
		@Override
		protected QuizQuestion doInBackground(Void... params) {

			mWasDisconnectedBeforeRetrieving = UserPreferences.getInstance(
					ShowQuestionsActivity.this).getConnectivityState().
					equals(ConnectivityState.OFFLINE);
			return QuestionsProxy.getInstance(ShowQuestionsActivity.this).
					retrieveRandomQuizQuestion();
		}

		@Override
		protected void onPostExecute(QuizQuestion question) {
			super.onPostExecute(question);
			if (null == question) {
				TextView textViewQuestion = (TextView) findViewById(R.id.displayQuestion);
				if (QuestionsProxy.getInstance(ShowQuestionsActivity.this).
						getInboxSize()==0 &&
						mWasDisconnectedBeforeRetrieving) {
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
