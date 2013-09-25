package sweng.epfl.editquestions;

import java.util.ArrayList;

import org.apache.http.client.methods.HttpPost;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import epfl.sweng.R;
import epfl.sweng.SubmitQuizzActivity;
import epfl.sweng.showquestions.ShowQuestionsActivity;

/**
 * The user can now enter a question that will be saved on a server.
 * 
 * @author born4new
 * @author Merok
 * 
 */
public class EditQuestionActivity extends Activity {
	private final static String SERVER_URL = "https://sweng-quiz.appspot.com/quizquestions";

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

	public void sendEditedQuestion(View view) {
		LinearLayout layout = (LinearLayout) findViewById(R.id.layoutEditQuestion);
		Toast.makeText(this, "Quizz submitted to the server.",
				Toast.LENGTH_SHORT).show();

		// this is done in order to get the content of the EditText elements in the XML
		// WARNING : the order MUST BE : Question1 => Answer1 => ... => indexOfAnswer => tags
		ArrayList<String> listElem = new ArrayList<String>();
		for (int i = 0; i < layout.getChildCount(); i++){
			if (layout.getChildAt(i) instanceof EditText) {
				EditText currentEditText = (EditText) layout.getChildAt(i);
				String currentArgument = currentEditText.getText().toString();
				listElem.add(currentArgument);
			}	
		}
		

		HttpPost post = new HttpPost(SERVER_URL + "/quizquestions/");
		

	}

}
