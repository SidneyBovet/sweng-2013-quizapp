/**
 * 
 */
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
 * @author Sidney
 * 
 * @see OnItemClickListener
 *
 */
public class SelectionListener implements OnItemClickListener {
	
	private Button buttonNext;
	private Question concernedQuestion;
	private boolean alreadyAnswered;
	
	/**
	 * Creates a listener to react to user input within a {@link ShowQuestionsActivity}.
	 * @param bNext The "Next Question" button of UI.
	 * @param lwQuestions The list of displayed answers.
	 */
	public SelectionListener(
			Button bNext,
			Question question) {
		this.buttonNext = bNext;
		this.concernedQuestion = question;
		this.alreadyAnswered = false;
	}
	
	@Override
	public void onItemClick(
			AdapterView<?> parent,
			View view,
			int position,
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
		if (!alreadyAnswered) {
			alreadyAnswered = true;
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
				int solution = concernedQuestion.getSolutionIndex();
				if (id == solution) {
					buttonNext.setEnabled(true);
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

}
