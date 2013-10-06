package epfl.sweng.editquestions;

import java.util.ArrayList;
import java.util.List;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import epfl.sweng.R;
import epfl.sweng.testing.TestingTransactions;
import epfl.sweng.testing.TestingTransactions.TTChecks;

/**
 * Adapter used to display the elements of the <code>ListView</code> on the
 * {@link EditQuestionActivity} from the actual data.
 * 
 * @author MelodyLucid
 * 
 */

class AnswerListAdapter extends BaseAdapter {
	
	private EditQuestionActivity mEditQuestionActivity;
	private LayoutInflater mInflater;
	
	// fields related to Answers data
	private ArrayList<String> mAnswerList;
	private int mAnswerCount;
	private int mCorrectAnswerIndex;
	
	/**
	 * Constructor
	 * 
	 * @param editQuestionActivity The associated {@link EditQuestionActivity}
	 */
	
	public AnswerListAdapter(EditQuestionActivity editQuestionActivity) {
		super();
		mEditQuestionActivity = editQuestionActivity;
		this.mInflater = LayoutInflater.from(this.mEditQuestionActivity);

		resetAnswerList();
	}
	
	/**
	 * Adds a new specific answer to the list.
	 * 
	 * @param newAnswer a new answer.
	 */
	
	public void add(String newAnswer) {
		mAnswerCount++;
		mAnswerList.add(newAnswer);
		notifyDataSetChanged();
	}
	
	/**
	 * Retrieves the entire list of answers, stored so far in the data.
	 * 
	 * @return List of answer strings.
	 */
	public List<String> getAnswerList() {
		return mAnswerList;
	}
	
	/**
	 * Retrieves the index of the correct answer in the list.
	 * 
	 * @return The index of the correct answer.
	 */
	public int getCorrectIndex() {
		return mCorrectAnswerIndex;
	}
	
	/**
	 * Resets the fields and the data of the <code>ListView</code>
	 */
	public void resetAnswerList() {
		mAnswerCount = 2;
		mAnswerList = new ArrayList<String>();
		mAnswerList.add("");
		mAnswerList.add("");
		mCorrectAnswerIndex = 0;
		notifyDataSetChanged();
	}
	
	/**
	 * Notifies on a higher level that the data of the answers has changed.
	 * <p>
	 * Blocks the submit button on the {@link EditQuestionActivity} if the data
	 * set violates the requirements.
	 */
	
	@Override
	public void notifyDataSetChanged() {
		mEditQuestionActivity.updateSubmitButton(audit() == 0);
		super.notifyDataSetChanged();
	}
	
	/**
	 * Retrieves the current count of answers.
	 */
	
	@Override
	public int getCount() {
		return mAnswerCount;
	}
	
	/**
	 * Creates the <code>position</code>-th layout of the <code>ListView</code>
	 * :
	 * 
	 * <ul>
	 *   <li>Binds a <code>TextWatcher</code> on the answer field that updates
	 * the data of the answer text body</li>
	 *   <li>Binds an <code>OnClickListener</code> on the switch correct button
	 * that updates the index of the correct answer.</li>
	 *   <li>Binds an <code>OnClickListener</code> on the remove button that
	 * removes an element from the data, if it's possible.</li>
	 * </ul>
	 * 
	 * then returns it.
	 */
	
	@Override
	public Object getItem(int index) {
		return mAnswerList.get(index);
	}
	
	/**
	 * Retrieves the id of the current element at the position <code>index
	 * </code>, which is the <code>index</code> itself.
	 */
	
	@Override
	public long getItemId(int index) {
		return index;
	}
	
	/**
	 * Retrieves a layout that will take place at the <code>position</code>
	 */
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout relatLayout = (RelativeLayout) this.mInflater.inflate(R.
				layout.submit_question_answer_row, parent, false);
		
		EditText answerField = (EditText) relatLayout.findViewById(R.id.
				submit_question_answer_text);
		answerField.setText(mAnswerList.get(position));
		
		final int currentRowIndex = position;
		answerField.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void afterTextChanged(Editable s) {
				if (!mAnswerList.get(currentRowIndex).equals(s.toString())) {
					mAnswerList.remove(currentRowIndex);
					mAnswerList.add(currentRowIndex, s.toString());
					mEditQuestionActivity.updateSubmitButton(audit() == 0);
					TestingTransactions.check(TTChecks.QUESTION_EDITED);
				} else {
					return;
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
		
		Button switchCorrectButton = (Button) relatLayout.findViewById(R.
				id.submit_question_correct_switch);
		if (position == mCorrectAnswerIndex) {
			switchCorrectButton.setText(R.string.question_correct_answer);
		} else {
			switchCorrectButton.setText(R.string.question_wrong_answer);
		}
		
		switchCorrectButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (currentRowIndex == mCorrectAnswerIndex) {
					return;
				}
				mCorrectAnswerIndex = currentRowIndex;
				notifyDataSetChanged();
				TestingTransactions.check(TTChecks.QUESTION_EDITED);
			}
		});
		
		Button removeButton = (Button) relatLayout.findViewById(R.id.
				submit_question_remove_answer_edit);
		
		removeButton.setEnabled(getCount() > 2);
		
		removeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (mAnswerCount <= 2) {
					return;
				}
				mAnswerList.remove(currentRowIndex);
				mAnswerCount--;
				if (currentRowIndex <= mCorrectAnswerIndex && currentRowIndex > 0) {
					mCorrectAnswerIndex--;
				}
				notifyDataSetChanged();
				TestingTransactions.check(TTChecks.QUESTION_EDITED);
			}
		});
		
		return relatLayout;
	}
	
	/**
	 * 
	 * Checks the following requirements :
	 * <ul>
	 * <li>The current count of answers must precisely be the size of the
	 * <code>ArrayList</code>.</li>
	 * 	<li>None of the answers must be an empty string.</li>
	 * 	<li>The index of the correct answer must exist in the <code>ArrayList
	 * </code>.</li>
	 * 	<li>There must be at least 2 answers</li>
	 * </ul>
	 * 
	 * @return	The number of the previously described errors.
	 */
	
	public int audit() {
		int errors = 0;

		if (mAnswerCount != mAnswerList.size()) {
			errors++;
		}
		for (int i = 0; i < mAnswerCount; i++) {
			if (mAnswerList.get(i).matches("\\s*")) {
				errors++;
			}
		}
		if (mCorrectAnswerIndex >= mAnswerCount || mCorrectAnswerIndex < 0
			|| mAnswerList.get(mCorrectAnswerIndex) == null) {
			errors++;
		}
		if (mAnswerCount < 2) {
			errors++;
		}

		return errors;
	}
}
