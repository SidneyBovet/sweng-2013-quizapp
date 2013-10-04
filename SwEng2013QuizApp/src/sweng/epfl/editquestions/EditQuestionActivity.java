package sweng.epfl.editquestions;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import epfl.sweng.R;
<<<<<<< HEAD
import epfl.sweng.backend.Question;
=======
>>>>>>> b022b3ad97883d8011261ea583d9aad09d349fc5
import epfl.sweng.servercomm.ServerInteractions;
import epfl.sweng.testing.TestingTransactions;
import epfl.sweng.testing.TestingTransactions.TTChecks;

/**
 * The user can now enter a question that will be saved on a server.
 * 
 * @author born4new
 * @author Merok
 * @author Aymeric Genet
 * 
 */
public class EditQuestionActivity extends Activity {
	private AnswerListAdapter mAnswerListAdapter;
	private ListView mListview;
	private RelativeLayout mLayout;

	public void addMoreElements(View view) {
		mAnswerListAdapter.add("");

	}

	public void updateSubmitButton(boolean value) {
		Button submitButton = (Button) mLayout.findViewById(R.id.submit_question_button);
		if (submitButton != null) {
			submitButton.setEnabled(audit() == 0 && value);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit_question);

		mLayout = (RelativeLayout) findViewById(R.id.layoutEditQuestion);
		mAnswerListAdapter = new AnswerListAdapter(this);
		mListview = (ListView) findViewById(R.id.submit_question_listview);

		mListview.setAdapter(mAnswerListAdapter);

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

		// this was done in order to get the content of the EditText elements in
		// the XML
		// 	for (int i = 0; i < childCountInlayout; i++) {
		// 		if (mLayout.getChildAt(i) instanceof EditText) {
		// 		EditText currentEditText = (EditText) mLayout.getChildAt(i);
		// 		String currentArgument = currentEditText.getText().toString();
		// 		listElem.add(currentArgument);
		// 	}
		// }
		// WARNING : you MUST follow this structure when you write the EditText
		// elements : Question1 => Answer1 => ... => indexOfAnswer => tags
		List<String> listElem = new ArrayList<String>();

		EditText questionBodyEditText = (EditText) mLayout.findViewById(R.id.submit_question_text_body);
		String questionBodyString = questionBodyEditText.getText().toString();
		int indexGoddAnswer = mAnswerListAdapter.getCorrectIndex();
		String indexGoodAnswerString = Integer.toString(indexGoddAnswer);
		listElem.add(questionBodyString);

		List<String> listAnswers = mAnswerListAdapter.getAnswerList();

		for (int i = 0; i < mAnswerListAdapter.getCount(); i++) {
			listElem.add(listAnswers.get(i));
		}

		EditText questionTagsEditText = (EditText) mLayout.findViewById(R.id.submit_question_tags);
		String questionTagsString = questionTagsEditText.getText().toString();
		listElem.add(indexGoodAnswerString);
		listElem.add(questionTagsString);

		ServerInteractions.submitQuestion(listElem);

		//FUNCTION : CLEAR VIEW OF QUIZ EDITION
		resetEditQuestionLayout(mLayout);
		TestingTransactions.check(TTChecks.EDIT_QUESTIONS_SHOWN);
	}

	private void resetEditQuestionLayout(RelativeLayout mainLayout) {
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
		mAnswerListAdapter.reset();
		editTextToFocus.requestFocus();
	}

	/**
	 * Checks the following requirements :
	 * <ul>
	 * 	<li>The question field must not be an empty string</li>
	 * 	<li>The tags field must not be an empty string.</li>
	 * </ul>
	 * 
	 * @return The number of the previously described errors.
	 */
	private int audit() {
		int errors = 0;

		// TODO : think about listeners on EditTexts.

		return errors;
	}
}