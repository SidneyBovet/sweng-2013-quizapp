package epfl.sweng.showquestions;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;
import epfl.sweng.R;
import epfl.sweng.backend.Question;
import epfl.sweng.testing.TestingTransactions;
import epfl.sweng.testing.TestingTransactions.TTChecks;

/**
 * The <code>SelectionListener</code> allows to implement a custom
 * implementation for the button.
 * 
 * @author Sidney
 * 
 * @see OnItemClickListener
 *
 */
public class SelectionListener implements OnItemClickListener {
	
	private Button mButtonNext;
	private Question mConcernedQuestion;
	private boolean mRightAnswerSelected;
	
	private int mLastSelectedQuestion;
	
	/**
	 * Creates a listener that will react to user input within a
	 * {@link ShowQuestionsActivity}.
	 * 
	 * @param bNext The next question button
	 * @param lwQuestions The list of displayed answers.
	 */
	
	public SelectionListener(Button bNext, Question question) {
		this.mButtonNext = bNext;
		this.mConcernedQuestion = question;
		this.mRightAnswerSelected = false;
		this.mLastSelectedQuestion = -1;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		/* /!\ 	"position" and "id" seem to be the same (same value) but we've
		 * 		got to be careful since they don't have the same description:
		 * 
		 * 		position	The position of the view in the adapter
		 * 		id			The row id of the item that was clicked
		 * 
		 * (I have no idea what this means, keep using *id* as advised by TA)
		 * 
		 * 									Sidney
		 */
		if (!mRightAnswerSelected) {
			TextView clickedAnswer;
			try {
				clickedAnswer = (TextView) parent.getChildAt(position);
			} catch (RuntimeException e) {
				System.err.println("Exception while casting parent."
						+ "Not containing TextViews?\n"
						+ e.getStackTrace());
				clickedAnswer = null;
			}
			if (clickedAnswer != null) {
				int solution = mConcernedQuestion.getSolutionIndex();
				
				resetAnswerStatements(parent);
				this.mLastSelectedQuestion = (int) id;
				
				if (id == solution) {
					mRightAnswerSelected = true;
					mButtonNext.setEnabled(true);
					clickedAnswer.append(" " + parent.getContext().
							getString(R.string.question_correct_answer));
				} else {
					clickedAnswer.append(" " + parent.getContext().
							getString(R.string.question_wrong_answer));
				}
			}
		}
		
		// Notifying the testing interface
		TestingTransactions.check(TTChecks.ANSWER_SELECTED);
	}

	/**
	 * Resets the selection of answer, so only one selection of answer can be
	 * shown.
	 * 
	 * @param The parent {@link AdapterView} that called this listener
	 */
	private void resetAnswerStatements(AdapterView<?> parent) {
		if (mLastSelectedQuestion != -1) {
			TextView child = (TextView) parent.getChildAt(mLastSelectedQuestion);
			CharSequence childsContent = child.getText().toString();
			CharSequence newContent = childsContent.subSequence(0,
					childsContent.length()-2);
			child.setText(newContent);
		}
	}

}
