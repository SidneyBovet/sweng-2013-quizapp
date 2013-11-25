package epfl.sweng.caching;

import android.content.Context;
import android.database.Cursor;
import epfl.sweng.backend.QuestionAgent;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.quizquestions.QuizQuestion;

/**
 * @author Sidney
 */
public class CachedQuestionAgent extends QuestionAgent {

	private QuizQuery mQuery;
	private Context mContext;
	private Cursor mQuestionCursor;
	
	/**
	 * 
	 * @param query The {@link QuizQuery} defining this stream of questions,
	 * <code>null</code> if those are random questions.
	 * @param context The {@link Context} of the activity using this object.
	 */
	public CachedQuestionAgent(QuizQuery query, Context context) {
		mQuery = query;
		mContext = context;
		mQuestionCursor = null;
	}
	
	@Override
	public QuizQuestion getNextQuestion() {
		QuizQuestion retrievedQuestion = null;
		CacheContentProvider contentProvider =
				new CacheContentProvider(mContext, false);
		
		if (null == mQuery) {
			
			retrievedQuestion = contentProvider.getRandomQuestion();
		} else {
			if (null == mQuestionCursor) {
				mQuestionCursor = contentProvider.getQuestions(mQuery);
			}
			// TODO extract a question from the cursor
		}
		
		contentProvider.destroy();
		return retrievedQuestion;
	}

}
