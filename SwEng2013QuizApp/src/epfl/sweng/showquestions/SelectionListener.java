/**
 * 
 */
package epfl.sweng.showquestions;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import epfl.sweng.backend.Question;

/**
 * @author Sidney
 * 
 * @see OnItemClickListener
 *
 */
public class SelectionListener implements OnItemClickListener {
	
	Activity parentActivity;
	Button buttonNext;
	Question concernedQuestion;
	
	/**
	 * Creates a listener to react to user input within a {@link ShowQuestionsActivity}.
	 * @param bNext The "Next Question" button of UI.
	 * @param lwQuestions The list of displayed answers.
	 */
	public SelectionListener(
			Button bNext,
			Question question,
			Activity parent) {
		this.buttonNext = bNext;
		this.concernedQuestion = question;
		this.parentActivity = parent;
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
		 * /!\	(I have no idea what this means, using *id*)
		 */
		
		ListView displayAnswers;
		try {
			displayAnswers = (ListView) parent;
		} catch (RuntimeException e) {
			System.err.println("Exception while casting parent.");
			displayAnswers = null;
		}
		if (displayAnswers != null) {
			Toast.makeText(
					parentActivity,
					"position="+position+" id="+id+". Yeeepey!!",
					Toast.LENGTH_SHORT).show();
		}
	}

}
