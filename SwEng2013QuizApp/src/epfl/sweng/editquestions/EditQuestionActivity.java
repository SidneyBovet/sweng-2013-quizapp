package epfl.sweng.editquestions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

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
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.HttpFactory;
import epfl.sweng.servercomm.SwengHttpClientFactory;
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

	public void updateSubmitButton(boolean value) {
		Button submitButton = (Button) mLayout
				.findViewById(R.id.submit_question_button);
		if (submitButton != null) {
			boolean newVal = value && auditEmptyField() == 0;
			if (submitButton.isEnabled() != newVal) {
				submitButton.setEnabled(newVal);
			}
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
		
		@Override
		protected Integer doInBackground(QuizQuestion... questions) {
			if (null != questions && questions.length != 1) {
				throw new IllegalArgumentException();
			}
			
			// TODO Uncomment this when using the proxy.
			// return QuestionsProxy.getInstance().sendQuizzQuestion(questions[0]);
			
			/******************* DELETE THIS WHEN PROXY *******************/ 
			QuizQuestion question = questions[0];
			
			int responseStatus = -1;
			HttpPost post = HttpFactory.getPostRequest(
					HttpFactory.getSwengBaseAddress() + "/quizquestions/");
			
			// Send the quiz
			try {
				post.setEntity(new StringEntity(question.toJSON().toString()));
				post.setHeader("Content-type", "application/json");
				
				HttpResponse mResponse = SwengHttpClientFactory.getInstance().execute(post);
				responseStatus = mResponse.getStatusLine().getStatusCode();
			} catch (UnsupportedEncodingException e) {
				// XXX switch to off line mode
				Log.e(this.getClass().getName(), "doInBackground(): Entity does "
						+ "not support the local encoding.", e);
			} catch (ClientProtocolException e) {
				// XXX switch to off line mode
				Log.e(this.getClass().getName(), "doInBackground(): Error in the "
						+ "HTTP protocol.", e);
			} catch (IOException e) {
				// XXX switch to off line mode
				Log.e(this.getClass().getName(), "doInBackground(): An I/O error "
						+ "has occurred.", e);
			}
			
			return responseStatus;
			/**************************************************************/
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			
			if (result != HttpStatus.SC_CREATED) {
				// XXX switch to off line mode
				Toast.makeText(EditQuestionActivity.this, R.string.
						error_uploading_question, Toast.LENGTH_LONG).show();
			}
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
		errorCount += auditSubmitButton();
		return errorCount;
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
	 * Audit method that verifies if all rep-invariants for EditTexts
	 * are fulfilled. 
	 * 
	 * @return the number of violated rep-invariants.
	 */
	
	private int auditEditTexts() {
		int errorCount = 0;
		
		EditText editQuestionBody = (EditText) mLayout.
				findViewById(R.id.submit_question_text_body_edit);
		EditText editTags = (EditText) mLayout.
				findViewById(R.id.submit_question_tags);
		
		String questionBodyHint = getString(R.string.submit_question_text_body);
		if (editQuestionBody == null
				|| !editQuestionBody.getHint().equals(questionBodyHint)
				|| editQuestionBody.getVisibility() != View.VISIBLE) {
			errorCount++;
		}
		
		String answerHint = getString(R.string.submit_question_answer_hint);
		for (int i = 0; i < mListview.getCount(); i++) {
			View answerView = mListview.getChildAt(i);
			EditText editAnswer = (EditText) answerView.
					findViewById(R.id.submit_question_answer_text);
			
			if (editAnswer == null
					|| !editAnswer.getHint().equals(answerHint)
					|| editAnswer.getVisibility() != View.VISIBLE) {
				errorCount++;
			}
		}
		
		String tagsHint = getString(R.string.submit_question_tags);
		if (editTags == null
				|| !editTags.getHint().equals(tagsHint)
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
				.findViewById(R.id.submit_question_add_text);
		Button submitButton = (Button) mLayout
				.findViewById(R.id.submit_question_button);
		
		if (addButton == null || !addButton.getText().equals(R.id.submit_question_add_button) 
				|| !addButton.isShown()) {
			++errorCount;
		}
		
		if (submitButton == null || !submitButton.getText().equals(R.id.submit_question_button)
				|| !submitButton.isShown()) {
			++errorCount;
		}
		
		//XXX correct view got?
		//XXX isShown => getVisibility() == View.VISIBLE
		//XXX getText().equals(R.id...) => getString(R.string...)
		for (int i = 0; i < mListview.getCount(); i++) {
			View answerView = mListview.getChildAt(i);
			Button removeButton = (Button) answerView
					.findViewById(R.string.submit_question_remove_answer);
			Button correctnessButton = (Button) answerView	
					.findViewById(R.string.submit_Edited_Question_Button);
			if (removeButton == null || !removeButton.getText().equals("-")
					|| !removeButton.isShown()) {
				++errorCount;
			}
			
			if (correctnessButton == null || 
					!(correctnessButton.getText().equals(R.string.question_wrong_answer) ||
							correctnessButton.getText().equals(R.string.question_correct_answer))
					|| !correctnessButton.isShown()) {
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
			Button correctnessButton = (Button) answerView.
					findViewById(R.id.submit_question_correct_switch);
			
			
			
			// TODO what the hell?! == true + = true? maaah
			
			
			
			if (correctnessButton == null) {
				return 2;	// then you've met with a terrible fate
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
		errorCount += mAnswerListAdapter.audit();
		
		return errorCount;
	}
}
