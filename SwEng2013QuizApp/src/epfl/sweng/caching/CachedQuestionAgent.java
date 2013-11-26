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

	private CacheContentProvider mContentProvider;
	private Cursor mQuestionCursor;
	
	/**
	 * 
	 * @param query The {@link QuizQuery} defining this stream of questions,
	 * <code>null</code> if those are random questions.
	 * @param context The {@link Context} of the activity using this object.
	 */
	public CachedQuestionAgent(QuizQuery query, Context context) {
		mContentProvider = new CacheContentProvider(context, false);
		mQuestionCursor = mContentProvider.getQuestions(query);
	}
	
	@Override
	public QuizQuestion getNextQuestion() {
		QuizQuestion retrievedQuestion = null;
		
		// TODO extract a question from the cursor
		
		return retrievedQuestion;
	}

	@Override
	public void destroy() {
		mQuestionCursor.close();
		mContentProvider.destroy();
	}

}
