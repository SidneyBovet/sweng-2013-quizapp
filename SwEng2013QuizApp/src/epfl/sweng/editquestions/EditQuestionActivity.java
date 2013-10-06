package epfl.sweng.editquestions;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import epfl.sweng.R;
import epfl.sweng.servercomm.ServerInteractions;
import epfl.sweng.testing.TestingTransactions;
import epfl.sweng.testing.TestingTransactions.TTChecks;

/**
 * Activity that allows the user to submit a new question to an internet server.
 * 
 * @author born4new
 * @author Merok
 * @author MelodyLucid
 * 
 */

public class EditQuestionActivity extends Activity {
	
	private AnswerListAdapter mAnswerListAdapter;
	private ListView mListview;
	private RelativeLayout mLayout;
	
	// fields related to the question
	private String mQuestionBodyText;
	private String mTagsText; 
	
	/**
	 * Adds a new empty answer to the <code>ListView</code>.
	 * <p>
	 * Used when the add button is clicked.
	 * 
	 * @param view
	 */
	
	public void addMoreAnswer(View view) {
		mAnswerListAdapter.add("");
		TestingTransactions.check(TTChecks.QUESTION_EDITED);
	}
	
	/**
	 * Retrieves the question and the answers data, put them in a list, and then
	 * send it over the internet.
	 * <p>
	 * Used when the submit button is clicked.
	 * 
	 * @param view Reference to the widget that was clicked.
	 */
	
	public void sendEditedQuestion(View view) {
		Toast.makeText(this, "Quizz submitted to the server.",
				Toast.LENGTH_SHORT).show();
		
		// WARNING : you MUST follow this structure
		// elements : Question1 => Answer1 => ... => indexOfAnswer => tags
		List<String> listInputGUI = new ArrayList<String>();
		
		listInputGUI.add(mQuestionBodyText);
		
		List<String> listAnswers = mAnswerListAdapter.getAnswerList();
		for (int i = 0; i < mAnswerListAdapter.getCount(); i++) {
			listInputGUI.add(listAnswers.get(i));
		}
		
		int indexGoodAnswer = mAnswerListAdapter.getCorrectIndex();
		String indexGoodAnswerString = Integer.toString(indexGoodAnswer);
		
		listInputGUI.add(indexGoodAnswerString);
		listInputGUI.add(mTagsText);
		
		ServerInteractions.submitQuestion(listInputGUI);
		resetEditQuestionLayout();
		
		TestingTransactions.check(TTChecks.NEW_QUESTION_SUBMITTED);
	}
	
	/**
	 * Tries to update the status of the submit button with the value parameter,
	 * along with the success of the {@link #audit()} method.
	 * 
	 * @param value The new status value of the submit button.
	 */
	
	public void updateSubmitButton(boolean value) {
		Button submitButton = (Button) mLayout.findViewById(R.id.submit_question_button);
		if (submitButton != null) {
			submitButton.setEnabled(value && audit() == 0);
		}
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
	
	public int audit() {
		int errors = 0;
		
		if (mQuestionBodyText.equals("")) {
			errors++;
		}
		if (mTagsText.equals("")) {
			errors++;
		}
		
		return errors;
	}
	
	/**
	 * Initializes the contents of the Activity's standard options menu.
	 * <p>
	 * This was not implemented.
	 */
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.submit_question, menu);
		
		return true;
	}
	
	/**
	 * Initializes the activity :
	 * 
	 * <ul>
	 * 	<li>Binds the {@link AnswerListAdapter} with the <code>ListView</code>
	 * </li>
	 * 	<li>Adds <code>TextWatcher</code> on the question and tags fields.
	 * </li>
	 * </ul>
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit_question);
		
		mQuestionBodyText = "";
		mTagsText = "";
		
		mLayout = (RelativeLayout) findViewById(R.id.layoutEditQuestion);
		mAnswerListAdapter = new AnswerListAdapter(this);
		mListview = (ListView) findViewById(R.id.submit_question_listview);
		
		mListview.setAdapter(mAnswerListAdapter);
		EditText questionEditText = (EditText) findViewById(R.id.submit_question_text_body_edit);
		
		// TextChanged Listener for questionEditText
		questionEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void afterTextChanged(Editable s) {
				// Proceed only if there has been changes.
				if (!mQuestionBodyText.equals(s.toString())) {
					mQuestionBodyText = s.toString();
					updateSubmitButton(mAnswerListAdapter.audit() == 0);
					TestingTransactions.check(TTChecks.QUESTION_EDITED);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start,
					int count, int after) {
				// Nothing to do here
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int count,
					int after) {
				// Nothing to do here
			}
		});
		
		EditText tagsEditText = (EditText) findViewById(R.id.submit_question_tags);
		tagsEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void afterTextChanged(Editable s) {
				if (!mTagsText.equals(s.toString())) {
					mTagsText = s.toString();
					updateSubmitButton(mAnswerListAdapter.audit() == 0);
					TestingTransactions.check(TTChecks.QUESTION_EDITED);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start,
					int count, int after) {
				// Nothing to do here
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int count,
					int after) {
				// Nothing to do here
			}
		});
		
		TestingTransactions.check(TTChecks.EDIT_QUESTIONS_SHOWN);
	}
	
	/**
	 * Resets the layout by emptying every <code>EditText</code> on the Activty,
	 * and by resetting the <code>ListView</code> adapter.
	 */
	
	private void resetEditQuestionLayout() {
		mQuestionBodyText = "";
		mTagsText = "";
		EditText editTextToFocus = null;
		for (int i = 0; i < mLayout.getChildCount(); i++) {
			if (mLayout.getChildAt(i) instanceof EditText) {
				EditText currentEditText = (EditText) mLayout.getChildAt(i);
				currentEditText.setText("");
				if (i == 0) {
					editTextToFocus = currentEditText;
				}
			}
		}
		mAnswerListAdapter.reset();
		editTextToFocus.requestFocus();
		TestingTransactions.check(TTChecks.EDIT_QUESTIONS_SHOWN);
	}
}