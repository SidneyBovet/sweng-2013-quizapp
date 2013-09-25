package sweng.epfl.editquestions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;
import epfl.sweng.R;
import epfl.sweng.SubmitQuizzActivity;
import epfl.sweng.showquestions.ShowQuestionsActivity;

/**
 * The user can now enter a question that will be saved on a server.
 * @author born4new
 * @author Merok
 *
 */
public class EditQuestionActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit_question);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.submit_question, menu);
		return true;
	}
	
	public void sendEditedQuestion(View view){
		Toast.makeText(this, "Quizz submitted to the server.", Toast.LENGTH_SHORT).show();
		Intent submitQuizzActivityIntent = new Intent(this, SubmitQuizzActivity.class);
		startActivity(submitQuizzActivityIntent);
	}
	

}
