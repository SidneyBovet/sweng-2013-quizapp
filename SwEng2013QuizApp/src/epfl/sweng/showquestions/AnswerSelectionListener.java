package epfl.sweng.showquestions;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * The <code>SelectionListener</code> allows to implement a custom
 * implementation for the button.
 * 
 * @author Sidney
 * 
 * @see OnItemClickListener
 *
 */
public class AnswerSelectionListener implements OnItemClickListener {
	
	private Button mButtonNext;
	private QuizQuestion mConcernedQuestion;
	private boolean mRightAnswerSelected;
	
	/**
	 * Creates a listener that will react to user input within a
	 * {@link ShowQuestionsActivity}.
	 * 
	 * @param bNext The next question button
	 * @param lwQuestions The list of displayed answers.
	 */
	
	public AnswerSelectionListener(Button bNext, QuizQuestion question) {
		this.mButtonNext = bNext;
		this.mConcernedQuestion = question;
		this.mRightAnswerSelected = false;
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
			int solution = mConcernedQuestion.getSolutionIndex();
			
			if (id == solution) {
				mRightAnswerSelected = true;
				mButtonNext.setEnabled(true);
				((ShowQuestionsAdapter) parent.getAdapter()).setWrongIndex(-1);
				((ShowQuestionsAdapter) parent.getAdapter()).setCorrectIndex((int) id);
			} else {
				((ShowQuestionsAdapter) parent.getAdapter()).setWrongIndex((int) id);
			}
			notifyAnswerStatements(parent);
		}
		
		// Notifying the testing interface
		TestCoordinator.check(TTChecks.ANSWER_SELECTED);
	}

	/**
	 * Notifies the selection of answer, so only one selection of answer can be
	 * shown.
	 * 
	 * @param The parent {@link AdapterView} that called this listener
	 */
	private void notifyAnswerStatements(AdapterView<?> parent) {
		((ShowQuestionsAdapter) parent.getAdapter()).notifyDataSetChanged();
	}

}
