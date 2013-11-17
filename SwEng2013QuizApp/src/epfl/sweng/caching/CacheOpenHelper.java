package epfl.sweng.caching;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import epfl.sweng.quizquestions.QuizQuestion;

public class CacheOpenHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "persistent";
    private static final String CACHE_TABLE_NAME = "questionCache";
    
    // XXX Is there a way to have nicer tags and answer lists?
	private static final String CACHE_TABLE_CREATE = "CREATE TABLE" +
			CACHE_TABLE_NAME + " (" +
					"id integer primary key," +
					"tags varchar("+
						QuizQuestion.TAGSLIST_MAX_SIZE *
						QuizQuestion.TAGSET_MAX_SIZE + "), " +
					"statement varchar("+
						QuizQuestion.QUESTION_CONTENT_MAX_SIZE + "), " +
					"answers varchar(" +
						QuizQuestion.ANSWER_CONTENT_MAX_SIZE *
						QuizQuestion.ANSWERLIST_MAX_SIZE + "), " +
					"owner varchar(100), " +
					"solutionIndex integer" +
				");";

	
	public CacheOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CACHE_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// XXX What should we do here? - Sidney
		throw new UnsupportedOperationException("Cannot upgrade SQLite version.");
	}
}
