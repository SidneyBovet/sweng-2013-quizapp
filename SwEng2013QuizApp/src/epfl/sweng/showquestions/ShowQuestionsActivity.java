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
import epfl.sweng.servercomm.ServerInteractions;
import epfl.sweng.testing.TestingTransactions;
import epfl.sweng.testing.TestingTransactions.TTChecks;

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

		// fetching question
		Question randomQuestion = ServerInteractions.getRandomQuestion();

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
		
		//put answer list under listening
		SelectionListener listener =
				new SelectionListener(buttonNext, randomQuestion);
		displayAnswers.setOnItemClickListener(listener);
		
		TestingTransactions.check(TTChecks.QUESTION_SHOWN);
	}

	/**
	 * Goes back to the state when the current activity was started.
	 * 
	 * @param view Element that was clicked, which is the send button. 
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
