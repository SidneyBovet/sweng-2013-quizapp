package epfl.sweng.showquestions;

import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
		
		setDisplayView();
	}

	private void setDisplayView() {
		// setting button look
		Button buttonNext = (Button) findViewById(R.id.buttonNext);
		buttonNext.setEnabled(false);
		
		// fetching question
		Question randomQuestion = Question.getRandomQuestion();
		
		// setting tags
		TextView textViewQuestion = (TextView) findViewById(R.id.displayQuestion);
		textViewQuestion.setText(randomQuestion.getQuestionContent());
		
		TextView textViewTag = (TextView) findViewById(R.id.displayTags);
		try {
			textViewTag.setText(randomQuestion.getTagsToString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// setting answer list
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, 
		        android.R.layout.simple_list_item_1,
		        randomQuestion.getAnswers());

		ListView displayAnswers = (ListView) findViewById(R.id.displayAnswers);
		displayAnswers.setAdapter(adapter);
		SelectionListener listener =
				new SelectionListener(buttonNext, randomQuestion);
		displayAnswers.setOnItemClickListener(listener);
	}

/**
 * Go back to the state when the current activity was started.
 * @param view that was clicked (here the button send) 
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

}
