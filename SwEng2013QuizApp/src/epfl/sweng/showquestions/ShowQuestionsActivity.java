package epfl.sweng.showquestions;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

		Question randomQuestion = getRandomQuestion();
		
		TextView textViewQuestion = (TextView) findViewById(R.id.displayQuestion);
		textViewQuestion.setText(randomQuestion.getQuestionContent());
		
		TextView textViewTag = (TextView) findViewById(R.id.displayTags);
		try {
			textViewTag.setText(randomQuestion.getTagsToString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
		        android.R.layout.simple_list_item_1, randomQuestion.getAnswerToStringArray());
		ListView textViewAnswers = (ListView) findViewById(R.id.displayAnswers);
		textViewAnswers.setAdapter(adapter);
	}
	
	/**
	 * Processes a request in an {@link AsyncTask} and returns the parsed {@link Question}
	 * @return The parsed {@link Question} object.
	 */
	private Question getRandomQuestion() {

		DownloadJSONFromServer asyncTaskRandomQuestionGetter = new DownloadJSONFromServer();
		asyncTaskRandomQuestionGetter.execute();
		
		Question question = null;
		try {
			question = Question.createQuestionFromJSON(asyncTaskRandomQuestionGetter.get());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return question;
	}
/**
 * Go back to the state when the current activity was started.
 * @param view that was clicked (here the button send) 
 */
	public void displayAgainRandomQuestion(View view) {
		Intent showQuestionsActivityIntent = new Intent(this, ShowQuestionsActivity.class);
	    startActivity(showQuestionsActivityIntent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_question, menu);
		return true;
	}

}
