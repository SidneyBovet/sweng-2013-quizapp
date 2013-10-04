package epfl.sweng.entry;

import sweng.epfl.editquestions.EditQuestionActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;
import epfl.sweng.R;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.testing.TestingTransactions;
import epfl.sweng.testing.TestingTransactions.TTChecks;

/***
 * Main class of the program.
 * @author born4new
 *
 */
public class MainActivity extends Activity {

//	@Override
//	protected void onResume() {
//		super.onResume();
//		TestingTransactions.check(TTChecks.MAIN_ACTIVITY_SHOWN);
//	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Transaction testing.
		TestingTransactions.check(TTChecks.MAIN_ACTIVITY_SHOWN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void displayRandomQuestion(View view) {
		Toast.makeText(this, "Please answer to the following question.", Toast.LENGTH_SHORT).show();
		Intent showQuestionsActivityIntent = new Intent(this, ShowQuestionsActivity.class);
	    startActivity(showQuestionsActivityIntent);
	}
	
	public void submitQuestion(View view) {
		Toast.makeText(this, "Please enter your question.", Toast.LENGTH_SHORT).show();
		Intent submitQuestionActivityIntent = new Intent(this, EditQuestionActivity.class);
	    startActivity(submitQuestionActivityIntent);
	}

}
