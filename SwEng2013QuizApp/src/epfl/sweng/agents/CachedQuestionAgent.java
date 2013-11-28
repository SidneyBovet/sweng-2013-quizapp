package epfl.sweng.agents;

import java.util.Random;

import android.content.Context;
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
	 * @param query The {@link QuizQuery} defining this stream of questions,
	 * <code>null</code> if those are random questions.
	 * @param context The {@link Context} of the activity using this object.
	 */
	public CachedQuestionAgent(QuizQuery query, Context context) {
		super(query);
		mContentProvider = new CacheContentProvider(context, false);
		mCursor = mContentProvider.getQuestions(query);
	}
	
	@Override
	public QuizQuestion getNextQuestion() {
		QuizQuestion retrievedQuestion = null;

		int position = new Random().nextInt(mCursor.getCount());
		mCursor.moveToPosition(position);
		int questionPK = mCursor.getInt(mCursor.getColumnIndex("id"));
		retrievedQuestion = mContentProvider.getQuestionFromPK(questionPK);
		
		return retrievedQuestion;
	}

	@Override
	public void close() {
		mCursor.close();
		mContentProvider.close();
	}

	@Override
	public boolean isClosed() {
		return mCursor.isClosed() || mContentProvider.isClosed();
	}

}
