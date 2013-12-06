package epfl.sweng.editquestions;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import epfl.sweng.R;
import epfl.sweng.comm.QuestionProxy;
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
	private RelativeLayout mEditQuestionLayout;
	
	private Button mAddButton;
	private Button mSubmitButton;
	private EditText mQuestionEditText;
	private EditText mTagsEditText;
	
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
	 * Creates a {@link QuizQuestion} with the fields of the graphical user
	 * interface.
	 * <p>
	 * The structure of the inputs list must follow this order :
	 * <ul>
	 * 	<li>Question text</li>
	 * 	<li>Answer #1 text</li>
	 * 	<li>Answer #2 text</li>
	 * 	<li>...</li>
	 * 	<li>Index of the correct answer</li>
	 * 	<li>Tags text</li>
	 * </ul>
	 * @return
	 */
	
	public QuizQuestion createQuestionFromGui() {
		if (mAnswerListAdapter != null) {
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
			return questionToSubmit;
		}
		return null;
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
		QuizQuestion question = createQuestionFromGui();
		if (null != question) {
			new AsyncPostQuestion().execute(createQuestionFromGui());
		}
		
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
		if (mSubmitButton != null) {
			mSubmitButton.setEnabled(isQuestionValid());
		}
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
		
		mEditQuestionLayout = (RelativeLayout) findViewById(R.id.layoutEditQuestion);
		mAnswerListAdapter = new AnswerListAdapter(this);
		mListview = (ListView) findViewById(R.id.submit_question_listview);
		
		mListview.setAdapter(mAnswerListAdapter);
		mQuestionEditText = (EditText) 
				findViewById(R.id.submit_question_text_body_edit);
		
		// TextChanged Listener for questionEditText
		mQuestionEditText.addTextChangedListener(new TextWatcher() {
			
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
		
		mTagsEditText = (EditText) findViewById(R.id.submit_question_tags);
		mTagsEditText.addTextChangedListener(new TextWatcher() {
			
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
		
		mAddButton = (Button) findViewById(R.id.submit_question_add_button);
		mSubmitButton = (Button) findViewById(R.id.submit_question_button);
		updateSubmitButton();
		
	}
	
	/**
	 * Resets the layout by emptying every <code>EditText</code> on the Activty,
	 * and by resetting the <code>ListView</code> adapter.
	 */
	
	private void resetEditQuestionLayout() {
		mQuestionBodyText = "";
		mTagsText = "";
		EditText editTextToFocus = null;
		for (int i = 0; i < mEditQuestionLayout.getChildCount(); i++) {
			if (mEditQuestionLayout.getChildAt(i) instanceof EditText) {
				EditText currentEditText = (EditText) mEditQuestionLayout.getChildAt(i);
				currentEditText.setText("");
				if (i == 0) {
					editTextToFocus = currentEditText;
				}
			}
		}
		mAnswerListAdapter.resetAnswerList();
		editTextToFocus.requestFocus();
	}

	private final class AsyncPostQuestion
		extends AsyncTask<QuizQuestion, Void, Integer> {
		
		@Override
		protected Integer doInBackground(QuizQuestion... questions) {
			if (null != questions && questions.length != 1) {
				throw new IllegalArgumentException();
			}
			
			return QuestionProxy.getInstance().
					sendQuizQuestion(questions[0]);
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (result != HttpStatus.SC_CREATED) {
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
	 * Audit method that verifies if all rep-invariants are fulfilled.
	 * 
	 * @return the number of violated rep-invariants.
	 */
	
	public int auditErrors() {
		int errorCount = 0;
		errorCount += auditEditTexts();
		errorCount += auditButtons();
		errorCount += auditAnswers();
		
		if (mSubmitButton.isEnabled() && auditSubmitButton() > 0) {
			logErrorIncrement("Submit button enabled and auditSubmitButton() > 0");
			errorCount += auditSubmitButton();
		} else if (!mSubmitButton.isEnabled() && auditSubmitButton() == 0) {
			logErrorIncrement("Submit button disabled and auditSubmitButton() == 0");
			errorCount += 1;
		}
		
		return errorCount;
	}
	
	/**
	 * Audit method that verifies if all rep-invariants for EditTexts are
	 * fulfilled.
	 * 
	 * @return the number of violated rep-invariants.
	 */
	
	private int auditEditTexts() {
		int errorCount = 0;
		
		String questionBodyHint = getString(R.string.submit_question_text_body);
		if (mQuestionEditText == null
				|| !mQuestionEditText.getHint().equals(questionBodyHint)
				|| mQuestionEditText.getVisibility() != View.VISIBLE) {
			logErrorIncrement("No text entered in question EditText or not visible");
			errorCount++;
		}
		
		String answerHint = getString(R.string.submit_question_answer_hint);
		for (int i = 0; i < mListview.getCount(); i++) {
			View answerView = mListview.getChildAt(i);
			EditText editAnswer = (EditText) answerView
					.findViewById(R.id.submit_question_answer_text);
			
			if (editAnswer == null || !editAnswer.getHint().equals(answerHint)
					|| editAnswer.getVisibility() != View.VISIBLE) {
				logErrorIncrement("One answer's EditText is empty or not visible");
				errorCount++;
			}
		}
		
		String tagsHint = getString(R.string.submit_question_tags);
		if (mTagsEditText == null || !mTagsEditText.getHint().equals(tagsHint)
				|| mTagsEditText.getVisibility() != View.VISIBLE) {
			logErrorIncrement("Tag EditText is empty or not visible");
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
		
		String addButtonText = getString(R.string.submit_question_add_button);
		if (mAddButton == null || !mAddButton.getText().equals(addButtonText)
				|| mAddButton.getVisibility() != View.VISIBLE) {
			logErrorIncrement("Add button corrupted or not visible");
			++errorCount;
		}
		
		String submitButtonText = getString(R.string.submit_question_button);
		if (mSubmitButton == null
				|| !mSubmitButton.getText().equals(submitButtonText)
				|| mSubmitButton.getVisibility() != View.VISIBLE) {
			logErrorIncrement("submit button corrupted or not visible");
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
				logErrorIncrement("Remove button is corrupted or not visible");
				++errorCount;
			}
			
			if (correctnessButton == null
					|| (!correctnessButton.getText().equals(correctAnswerText) 
							&& !correctnessButton.getText().equals(wrongAnswerText))
					|| correctnessButton.getVisibility() != View.VISIBLE) {
				logErrorIncrement("Correctness button corrupted or not visible");
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
				logErrorIncrement("Correctness button is null?!");
				return 1; // then you've met with a terrible fate
			}
			if (correctnessButton.getText().equals(correctAnswerCheck)) {
				if (!oneAnswerChecked) {
					oneAnswerChecked = true;
				} else {
					logErrorIncrement("More than one answer marked as corect");
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
		
		QuizQuestion questionToAudit = createQuestionFromGui();
		
		// avoid IllegalStateException
		if (questionToAudit != null) {
			errorCount += questionToAudit.auditErrors();
		} else {
			logErrorIncrement("Question to be audited is null?!");
			errorCount += 1;
		}
		
//		// avoid IllegalStateException
//		if (mAnswerListAdapter != null) {
//			errorCount += mAnswerListAdapter.auditErrors();
//		} else {
//			logErrorIncrement("AnswerListAdapter is null?!");
//			errorCount += 1;
//		}
		
		if (errorCount > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	private boolean isQuestionValid() {
		QuizQuestion questionToAudit = createQuestionFromGui();
		int errorCount = 0;
		// avoid IllegalStateException
		if (questionToAudit != null) {
			errorCount += questionToAudit.isQuestionValid();
		} else {
			logErrorIncrement("Question to be audited is null?!");
			errorCount += 1;
		}
		return errorCount == 0;
	}
	
	/**
	 * Just a helper to log audit-related things.
	 * @param message the message to append to this log
	 */
	private void logErrorIncrement(String message) {
		Log.d("auditErrors increment @ " + this.getClass().getName(), message);
	}
}
