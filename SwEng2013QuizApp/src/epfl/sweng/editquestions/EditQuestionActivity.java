package epfl.sweng.editquestions;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import android.app.Activity;
import android.os.AsyncTask;
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
import epfl.sweng.authentication.UserPreferences;
import epfl.sweng.patterns.QuestionsProxy;
import epfl.sweng.quizquestions.QuizQuestion;
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

	private AnswerListAdapter mAnswerListAdapter;
	private ListView mListview;
	private RelativeLayout mLayout;

	// fields related to the question
	private String mQuestionBodyText;
	private String mTagsText;
	private UserPreferences mUserPreferences;

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
		QuizQuestion questionToSubmit = QuizQuestion
				.createQuestionFromList(listInputGUI);
		
		//XXX pas d'exception comme dans show question? Joanna
		new AsyncPostQuestion().execute(questionToSubmit);

		resetEditQuestionLayout();
	}

	/**
	 * Tries to update the status of the submit button with the value parameter,
	 * along with the success of the {@link #auditEmptyField()} method.
	 * 
	 * @param value
	 *            The new status value of the submit button.
	 */

	public void updateSubmitButton() {
		Button submitButton = (Button) mLayout
				.findViewById(R.id.submit_question_button);
		if (submitButton != null) {
			submitButton.setEnabled(auditSubmitButton() == 0);
		}
	}

	/**
	 * Audit method that verifies if all rep-invariants are fulfilled.
	 * 
	 * @return the number of violated rep-invariants.
	 */

	public int auditErrors() {
		int errorCount = 0;
		errorCount += auditEditTexts();
		errorCount += auditButtons();
		errorCount += auditAnswers();
		errorCount += auditSubmitButton();
		return errorCount;
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
		mUserPreferences = UserPreferences
				.getInstance(this.getApplicationContext());
		return true;
	}

	/**
	 * Initializes the activity by setting the view.
	 * 
	 * @see #setDisplayView()
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit_question);
		setDisplayView();
		TestCoordinator.check(TTChecks.EDIT_QUESTIONS_SHOWN);
	}

	/**
	 * Sets the view by binding the {@link AnswerListAdapter} with the
	 * <code>ListView</code>, and adding a <code>TextWatcher</code> on the
	 * question and tags fields.
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
				// Proceed only if there has been user changes.
				if (!mQuestionBodyText.equals(s.toString())) {
					mQuestionBodyText = s.toString();
					updateSubmitButton();
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
					updateSubmitButton();
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
		mAnswerListAdapter.resetAnswerList();
		editTextToFocus.requestFocus();
	}

	class AsyncPostQuestion extends AsyncTask<QuizQuestion, Void, Integer> {

		//XXX toujours utili d'une Asynctask? Joanna
		@Override
		protected Integer doInBackground(QuizQuestion... questions) {
			if (null != questions && questions.length != 1) {
				throw new IllegalArgumentException();
			}

			return QuestionsProxy.getInstance().sendQuizzQuestion(questions[0]);
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (result != HttpStatus.SC_CREATED && result != HttpStatus.SC_USE_PROXY) {
				mUserPreferences.createEntry("CONNECTION_STATE", "OFFLINE");
				TestCoordinator.check(TTChecks.OFFLINE_CHECKBOX_ENABLED);
				Toast.makeText(
						EditQuestionActivity.this,
						getResources().getString(
								R.string.error_uploading_question),
						Toast.LENGTH_LONG).show();
			}
			TestCoordinator.check(TTChecks.NEW_QUESTION_SUBMITTED);
		}
	}

	/*
	 * ***************************************************
	 * ********************* Audit ***********************
	 * ***************************************************
	 */

	/**
	 * Checks the following requirements :
	 * <ul>
	 * <li>The question field must not be an empty string</li>
	 * <li>The tags field must not be an empty string.</li>
	 * </ul>
	 * 
	 * @return The number of the previously described errors.
	 */

	private int auditEmptyField() {
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
	 * Audit method that verifies if all rep-invariants for EditTexts are
	 * fulfilled.
	 * 
	 * @return the number of violated rep-invariants.
	 */

	private int auditEditTexts() {
		int errorCount = 0;

		EditText editQuestionBody = (EditText) mLayout
				.findViewById(R.id.submit_question_text_body_edit);
		EditText editTags = (EditText) mLayout
				.findViewById(R.id.submit_question_tags);

		String questionBodyHint = getString(R.string.submit_question_text_body);
		if (editQuestionBody == null
				|| !editQuestionBody.getHint().equals(questionBodyHint)
				|| editQuestionBody.getVisibility() != View.VISIBLE) {
			errorCount++;
		}

		String answerHint = getString(R.string.submit_question_answer_hint);
		for (int i = 0; i < mListview.getCount(); i++) {
			View answerView = mListview.getChildAt(i);
			EditText editAnswer = (EditText) answerView
					.findViewById(R.id.submit_question_answer_text);

			if (editAnswer == null || !editAnswer.getHint().equals(answerHint)
					|| editAnswer.getVisibility() != View.VISIBLE) {
				errorCount++;
			}
		}

		String tagsHint = getString(R.string.submit_question_tags);
		if (editTags == null || !editTags.getHint().equals(tagsHint)
				|| editTags.getVisibility() != View.VISIBLE) {
			errorCount++;
		}
		return errorCount;
	}

	/**
	 * Audit method that verifies if all rep-invariants for Buttons are
	 * fulfilled.
	 * 
	 * @return the number of violated rep-invariants.
	 */

	private int auditButtons() {
		int errorCount = 0;
		Button addButton = (Button) mLayout
				.findViewById(R.id.submit_question_add_button);
		Button submitButton = (Button) mLayout
				.findViewById(R.id.submit_question_button);

		String addButtonText = getString(R.string.submit_question_add_button);
		if (addButton == null || !addButton.getText().equals(addButtonText)
				|| addButton.getVisibility() != View.VISIBLE) {
			++errorCount;
		}

		String submitButtonText = getString(R.string.submit_question_button);
		if (submitButton == null
				|| !submitButton.getText().equals(submitButtonText)
				|| submitButton.getVisibility() != View.VISIBLE) {
			++errorCount;
		}

		String removeButtonText = getString(R.string.submit_question_remove_answer);
		String correctAnswerText = getString(R.string.question_correct_answer);
		String wrongAnswerText = getString(R.string.question_wrong_answer);
		for (int i = 0; i < mListview.getCount(); i++) {
			View answerView = mListview.getChildAt(i);
			Button removeButton = (Button) answerView
					.findViewById(R.id.submit_question_remove_answer_edit);
			Button correctnessButton = (Button) answerView
					.findViewById(R.id.submit_question_correct_switch);

			if (removeButton == null
					|| !removeButton.getText().equals(removeButtonText)
					|| removeButton.getVisibility() != View.VISIBLE) {
				++errorCount;
			}

			if (correctnessButton == null
					|| (!correctnessButton.getText().equals(correctAnswerText) && !correctnessButton
							.getText().equals(wrongAnswerText))
					|| correctnessButton.getVisibility() != View.VISIBLE) {
				++errorCount;
			}
		}
		return errorCount;
	}

	/**
	 * Audit method that verifies if all rep-invariants for answers are
	 * fulfilled.
	 * 
	 * @return the number of violated rep-invariants.
	 */

	private int auditAnswers() {
		boolean oneAnswerChecked = false;
		
		String correctAnswerCheck = getString(R.string.question_correct_answer);
		for (int i = 0; i < mListview.getCount(); i++) {
			View answerView = mListview.getChildAt(i);
			Button correctnessButton = (Button) answerView
					.findViewById(R.id.submit_question_correct_switch);
			
			if (correctnessButton == null) {
				return 2; // then you've met with a terrible fate
			}
			if (correctnessButton.getText().equals(correctAnswerCheck)) {
				if (!oneAnswerChecked) {
					oneAnswerChecked = true;
				} else {
					return 1;
				}
			}
		}

		return 0;
	}

	/**
	 * Audit method that verifies if all rep-invariants for behaviour of buttons
	 * are fulfilled.
	 * 
	 * @return the number of violated rep-invariants.
	 */

	private int auditSubmitButton() {
		int errorCount = 0;

		errorCount += auditEmptyField();

		// avoid IllegalStateException
		if (mAnswerListAdapter != null) {
			errorCount += mAnswerListAdapter.auditErrors();
		}

		return errorCount;
	}
}
