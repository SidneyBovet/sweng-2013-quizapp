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
import epfl.sweng.exceptions.ServerSubmitFailedException;
import epfl.sweng.servercomm.ServerInteractions;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * Activity that allows the user to submit a new question to the SwEng server.
 * 
 * @author born4new
 * @author Merok
 * @author MelodyLucid
 * 
 */

public class EditQuestionActivity extends Activity {

	// XXX Where do you think we should store that?
	private static final int HTTP_SUCCESS = 201;

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
		TestCoordinator.check(TTChecks.QUESTION_EDITED);
	}

	/**
	 * Retrieves the question and the answers data, put them in a list, and then
	 * sends it to the SwEng server.
	 * <p>
	 * Used when the submit button is clicked.
	 * 
	 * @param view
	 *            Reference to the widget that was clicked.
	 */

	public void sendEditedQuestion(View view) {

		// WARNING : you MUST follow this structure
		// elements : Question1 => Answer1 => ... => indexOfAnswer => tags
		List<String> listInputGUI = new ArrayList<String>();

		listInputGUI.add(mQuestionBodyText);

		List<String> listAnswers = mAnswerListAdapter.getAnswerList();
		listInputGUI.addAll(listAnswers);

		int indexGoodAnswer = mAnswerListAdapter.getCorrectIndex();
		String indexGoodAnswerString = Integer.toString(indexGoodAnswer);

		listInputGUI.add(indexGoodAnswerString);
		listInputGUI.add(mTagsText);

		int httpResponse = -1;
		try {
			httpResponse = ServerInteractions.submitQuestion(listInputGUI);
			if (httpResponse == HTTP_SUCCESS) {
				// TODO In general, we should put error messages in strings.xml
				// and especially make a hierachy if possible.
				Toast.makeText(this, "Quizz submitted to the server.",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this,
						"The server returned an error " + httpResponse,
						Toast.LENGTH_SHORT).show();
			}
		} catch (ServerSubmitFailedException e) {
			// TODO Log it? (Since we did it on the two layers before,
			// I'm wondering if we should do it here) Problem with the server
			Toast.makeText(this, "Server error.", Toast.LENGTH_SHORT).show();
		}

		resetEditQuestionLayout();
	}

	/**
	 * Tries to update the status of the submit button with the value parameter,
	 * along with the success of the {@link #audit()} method.
	 * 
	 * @param value
	 *            The new status value of the submit button.
	 */

	public void updateSubmitButton(boolean value) {
		Button submitButton = (Button) mLayout
				.findViewById(R.id.submit_question_button);
		if (submitButton != null) {
			boolean newVal = value && audit() == 0;
			if (submitButton.isEnabled() != newVal) {
				submitButton.setEnabled(newVal);
			}
		}
	}

	/**
	 * Checks the following requirements :
	 * <ul>
	 * <li>The question field must not be an empty string</li>
	 * <li>The tags field must not be an empty string.</li>
	 * </ul>
	 * 
	 * @return The number of the previously described errors.
	 */

	public int audit() {
		int errors = 0;

		if (mQuestionBodyText.matches("\\s*")) {
			errors++;
		}
		if (mTagsText.matches("\\s*")) {
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
	 * Initializes the activity by binding the {@link AnswerListAdapter} with
	 * the <code>ListView</code>, and adding a <code>TextWatcher</code> on the
	 * question and tags fields.
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit_question);
		setDisplayView();
		TestCoordinator.check(TTChecks.EDIT_QUESTIONS_SHOWN);
	}

	/**
	 * Resets the layout by emptying every <code>EditText</code> on the Activty,
	 * and by resetting the <code>ListView</code> adapter.
	 */
	private void setDisplayView() {

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
					TestCoordinator.check(TTChecks.QUESTION_EDITED);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
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
					TestCoordinator.check(TTChecks.QUESTION_EDITED);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// Nothing to do here
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int count,
					int after) {
				// Nothing to do here
			}
		});

	}

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
		mAnswerListAdapter.resetAnswerList();
		editTextToFocus.requestFocus();
	}
}
