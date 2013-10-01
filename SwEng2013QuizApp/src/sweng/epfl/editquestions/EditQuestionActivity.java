package sweng.epfl.editquestions;

import java.util.ArrayList;

import android.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import epfl.sweng.backend.Question;
import epfl.sweng.testing.TestingTransactions;
import epfl.sweng.testing.TestingTransactions.TTChecks;

/**
 * The user can now enter a question that will be saved on a server.
 * 
 * @author born4new
 * @author Merok
 * 
 */
public class EditQuestionActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit_question);
		TestingTransactions.check(TTChecks.EDIT_QUESTIONS_SHOWN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.submit_question, menu);

		return true;
	}

	public void sendEditedQuestion(View view) {
		Toast.makeText(this, "Quizz submitted to the server.",
				Toast.LENGTH_SHORT).show();
		LinearLayout layout = (LinearLayout) findViewById(R.id.layoutEditQuestion);
		ArrayList<String> listInputGUI = createListFromUserGUI(layout);

		Question.submitQuestion(listInputGUI);

		// FUNCTION : CLEAR VIEW OF QUIZ EDITION
		resetEditQuestionLayout(layout);
		TestingTransactions.check(TTChecks.EDIT_QUESTIONS_SHOWN);
	}

	private void resetEditQuestionLayout(LinearLayout mainLayout) {
		EditText editTextToFocus = null;
		for (int i = 0; i < mainLayout.getChildCount(); i++) {
			if (mainLayout.getChildAt(i) instanceof EditText) {
				EditText currentEditText = (EditText) mainLayout.getChildAt(i);
				currentEditText.setText("");
				if (i == 0) {
					editTextToFocus = currentEditText;
				}
			}
		}
		editTextToFocus.requestFocus();
	}

	private ArrayList<String> createListFromUserGUI(LinearLayout layout) {
		// this is done in order to get the content of the EditText elements in
		// the XML
		// WARNING : you MUST follow this structure when you write the EditText
		// elements : Question1 => Answer1 => ... => indexOfAnswer => tags
		ArrayList<String> listInputGUI = new ArrayList<String>();
		int childCountInlayout = layout.getChildCount();
		for (int i = 0; i < childCountInlayout; i++) {
			if (layout.getChildAt(i) instanceof EditText) {
				EditText currentEditText = (EditText) layout.getChildAt(i);
				String currentArgument = currentEditText.getText().toString();
				listInputGUI.add(currentArgument);
			}
		}
		return listInputGUI;
	}

}
