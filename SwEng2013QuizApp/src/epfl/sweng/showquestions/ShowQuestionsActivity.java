package epfl.sweng.showquestions;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import epfl.sweng.R;
import epfl.sweng.backend.Question;

/***
 * Activity used to display a random question to the user.
 * @author born4new
 *
 */
public class ShowQuestionsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_question);

		Question firstQuestion = getRandomQuestion();
		
		TextView textView = (TextView) findViewById(R.id.displayQuestion);
	    textView.setText(firstQuestion.toString());
		
	}
	
	/**
	 * Processes a request in an {@link AsyncTask} and returns the parsed {@link Question}
	 * @return The parsed {@link Question} object.
	 */
	private Question getRandomQuestion() {

		DownloadJSONFromServer asyncTaskRandomQuestionGetter = new DownloadJSONFromServer();
		asyncTaskRandomQuestionGetter.execute();
		
		Question question;
		try {
			question = Question.createQuestionFromJSON(asyncTaskRandomQuestionGetter.get());
		} catch (JSONException e) {
			e.printStackTrace();
			question = null;
		} catch (InterruptedException e) {
			e.printStackTrace();
			question = null;
		} catch (ExecutionException e) {
			e.printStackTrace();
			question = null;
		}
		
		return question;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_question, menu);
		return true;
	}

}
