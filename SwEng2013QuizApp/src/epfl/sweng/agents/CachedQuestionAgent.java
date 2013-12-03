package epfl.sweng.agents;

import android.database.Cursor;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.caching.CacheContentProvider;
import epfl.sweng.quizquestions.QuizQuestion;

/**
 * @author Sidney
 */
public class CachedQuestionAgent extends QuestionAgent {

	private CacheContentProvider mContentProvider;
	private Cursor mCursor;

	/**
	 * 
	 * @param query
	 *            The {@link QuizQuery} defining this stream of questions,
	 *            <code>null</code> if those are random questions.
	 */
	public CachedQuestionAgent(QuizQuery query) {
		super(query);
		mContentProvider = new CacheContentProvider(false);
		mCursor = mContentProvider.getQuestions(query);
	}

	@Override
	public QuizQuestion getNextQuestion() {
		QuizQuestion retrievedQuestion = null;
		
		if (isClosed() || mCursor.getCount() == 0 || mCursor.isAfterLast()) {
			return null;
		}
		
		int questionPK = mCursor.getInt(mCursor.getColumnIndex("id"));
		retrievedQuestion = mContentProvider.getQuestionFromPK(questionPK);
		
		if (!mCursor.moveToNext()) {
			mCursor.moveToFirst();
		}

		return retrievedQuestion;
	}

	@Override
	public void close() {
		mCursor.close();
		mContentProvider.close();
	}

	@Override
	public boolean isClosed() {
		return null == mCursor || null == mContentProvider
				|| mCursor.isClosed() || mContentProvider.isClosed();
	}

}
