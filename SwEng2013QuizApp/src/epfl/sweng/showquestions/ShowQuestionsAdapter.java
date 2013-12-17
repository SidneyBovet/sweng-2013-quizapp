package epfl.sweng.showquestions;

import java.util.List;

import epfl.sweng.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * Adapter used to display the elements of the <code>ListView</code> on the
 * {@link ShowQuestionsActivity} from the actual data.
 * 
 * @author Melody
 * 
 */

public class ShowQuestionsAdapter extends BaseAdapter implements ListAdapter {

	private ShowQuestionsActivity mShowQuestionsActivity;
	private LayoutInflater mInflater;
	
	// fields related to Answers data
	private List<String> mAnswerList;
	private int mCorrectIndex;
	private int mWrongIndex;
	
	/**
	 * Constructor
	 * 
	 * @param showQuestionsActivity The associated {@link ShowQuestionsActivity}
	 * @param answerList List of answers from the current question to display
	 */
	
	public ShowQuestionsAdapter(ShowQuestionsActivity showQuestionsActivity,
			List<String> answerList) {
		super();
		mShowQuestionsActivity = showQuestionsActivity;
		this.mInflater = LayoutInflater.from(this.mShowQuestionsActivity);
		this.mAnswerList = answerList;
		this.mCorrectIndex = -1;
		this.mWrongIndex = -1;
	}
	
	/**
	 * Sets the index at which the Adapter should put a correct 'V' mark.
	 * 
	 * @param correctIndex index at which there should be a correct mark.
	 */
	
	public void setCorrectIndex(int correctIndex) {
		mCorrectIndex = correctIndex;
	}
	
	/**
	 * Sets the index at which the Adapter should put a incorrect 'X' mark.
	 * 
	 * @param correctIndex index at which there should be a incorrect mark.
	 */
	
	public void setWrongIndex(int wrongIndex) {
		mWrongIndex = wrongIndex;
	}
	
	@Override
	public int getCount() {
		return mAnswerList.size();
	}

	@Override
	public Object getItem(int position) {
		return mAnswerList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Creates and returns the <code>position</code>-th layout by adding a
	 * correct answer mark 'V' or the incorrect answer mark 'X' if it is the
	 * appropriate position.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView answerField = (TextView) this.mInflater.inflate(
				android.R.layout.simple_list_item_1, parent, false);
		
		answerField.setText(mAnswerList.get(position));
		if (position == mCorrectIndex) {
			answerField.append(" " + parent.getContext().
							getString(R.string.question_correct_answer));
		} else if (position == mWrongIndex)  {
			answerField.append(" " + parent.getContext().
					getString(R.string.question_wrong_answer));
		}
		return answerField;
	}
}
