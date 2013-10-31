package epfl.sweng.showquestions;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONException;

import android.app.Activity;
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
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.HttpFactory;
import epfl.sweng.servercomm.SwengHttpClientFactory;
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

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_question);

		setDisplayView();
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

		AsyncRetrieveQuestion asyncFetchQuestion = new AsyncRetrieveQuestion();
		asyncFetchQuestion.execute();
		QuizQuestion randomQuestion = null;
		try {
			randomQuestion = asyncFetchQuestion.get();
		} catch (InterruptedException e) {
			Log.wtf(this.getClass().getName(),
					"AsyncFetchQuestion was interrupted");
		} catch (ExecutionException e) {
			// XXX switch to off line mode
			Log.e(this.getClass().getName(), "Process crashed");
//			finish();
			return;
		} finally {
			if (null == randomQuestion) {
				finish();
				return;
			}
		}

		// setting tags
		TextView textViewQuestion = (TextView) findViewById(R.id.displayQuestion);
		textViewQuestion.setText(randomQuestion.getQuestionContent());

		TextView textViewTag = (TextView) findViewById(R.id.displayTags);
		textViewTag.setText(randomQuestion.getTagsToString());

		// setting answer list
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				randomQuestion.getAnswers());
		ListView displayAnswers = (ListView) findViewById(R.id.displayAnswers);
		displayAnswers.setAdapter(adapter);

		// put answer list under listening
		AnswerSelectionListener listener = new AnswerSelectionListener(
				buttonNext, randomQuestion);
		displayAnswers.setOnItemClickListener(listener);

		TestCoordinator.check(TTChecks.QUESTION_SHOWN);
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

		@Override
		protected QuizQuestion doInBackground(Void... params) {

			// TODO Uncomment when getting back to the proxy.
			// return QuestionsProxy.getInstance().retrieveQuizzQuestion();

			/******************* DELETE THIS WHEN PROXY *******************/
			QuizQuestion question = null;

			String url = HttpFactory.getSwengFetchQuestion();

			HttpGet firstRandom = HttpFactory.getGetRequest(url);
			ResponseHandler<String> firstHandler = new BasicResponseHandler();
			try {
				String jsonQuestion = SwengHttpClientFactory.getInstance()
						.execute(firstRandom, firstHandler);
				question = new QuizQuestion(jsonQuestion);
			} catch (ClientProtocolException e) {
				Log.e(this.getClass().getName(), "doInBackground(): Error in"
						+ "the HTTP protocol.", e);
				// TODO switch to off line mode
			} catch (IOException e) {
				Log.e(this.getClass().getName(), "doInBackground(): An I/O"
						+ "error has occurred.", e);
				// TODO switch to off line mode
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return question;
			/**************************************************************/
		}

		@Override
		protected void onPostExecute(QuizQuestion question) {
			super.onPostExecute(question);
			if (null == question) {
				// XXX switch to off line mode
				Toast.makeText(
						ShowQuestionsActivity.this,
						getResources().getString(
								R.string.error_fetching_question),
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
