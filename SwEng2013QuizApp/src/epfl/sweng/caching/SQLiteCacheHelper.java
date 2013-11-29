package epfl.sweng.caching;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Used to access the local SQLite DB that we use for data persistence in our
 * app.
 * 
 * @author born4new
 * 
 */
public class SQLiteCacheHelper extends SQLiteOpenHelper {

	// DB Infos
	public static final String DATABASE_NAME = "swengQuizzApp";
	public static final int DATABASE_VERSION = 1;

	// Table Names
	public static final String TABLE_QUESTIONS = "questions";
	public static final String TABLE_TAGS = "tags";
	public static final String TABLE_ANSWERS = "answers";
	public static final String TABLE_QUESTIONS_TAGS = "questionsTags";

	// QUESTIONS Table
	public static final String FIELD_QUESTIONS_PK = "id";
	public static final String FIELD_QUESTIONS_SWENG_ID = "swengId";
	public static final String FIELD_QUESTIONS_STATEMENT = "statement";
	public static final String FIELD_QUESTIONS_SOLUTION_INDEX = "solutionIndex";
	public static final String FIELD_QUESTIONS_OWNER = "owner";
	public static final String FIELD_QUESTIONS_IS_QUEUED = "isQueued";
	public static final int QUESTIONS_NB_FIELDS = 6;

	// QUESTIONS Tags
	public static final String FIELD_TAGS_PK = "id";
	public static final String FIELD_TAGS_NAME = "name";
	public static final int TAGS_NB_FIELDS = 2;

	// QUESTIONS Answers
	public static final String FIELD_ANSWERS_PK = "id";
	public static final String FIELD_ANSWERS_ANSWER_VALUE = "answerValue";
	public static final String FIELD_ANSWERS_QUESTION_FK = "questionId";
	public static final int ANSWERS_NB_FIELDS = 3;

	// QUESTIONS questionTags
	public static final String FIELD_QUESTIONS_TAGS_TAG_FK = "tagId";
	public static final String FIELD_QUESTIONS_TAGS_QUESTION_FK = "questionId";
	public static final int QUESTIONS_TAGS_NB_FIELDS = 2;

	// Tables creation
	private static final String CREATE_TABLE_QUESTIONS = "CREATE TABLE "
			+ TABLE_QUESTIONS + " (" + FIELD_QUESTIONS_PK
			+ " integer primary key, " + FIELD_QUESTIONS_SWENG_ID + " integer,"
			+ FIELD_QUESTIONS_STATEMENT + " varchar(500), "
			+ FIELD_QUESTIONS_SOLUTION_INDEX + " integer, "
			+ FIELD_QUESTIONS_OWNER + " varchar(500), "
			+ FIELD_QUESTIONS_IS_QUEUED + " integer(1));";

	private static final String CREATE_TABLE_TAGS = "CREATE TABLE "
			+ TABLE_TAGS + " (" + FIELD_TAGS_PK + " integer primary key,  "
			+ FIELD_TAGS_NAME + " varchar(500));";

	private static final String CREATE_TABLE_QUESTIONS_TAGS = "CREATE TABLE "
			+ TABLE_QUESTIONS_TAGS + "(" + FIELD_QUESTIONS_TAGS_QUESTION_FK
			+ " integer, " + FIELD_QUESTIONS_TAGS_TAG_FK
			+ " integer, FOREIGN KEY(" + FIELD_QUESTIONS_TAGS_QUESTION_FK
			+ ") REFERENCES " + TABLE_QUESTIONS + "(" + FIELD_QUESTIONS_PK
			+ "),FOREIGN KEY(" + FIELD_QUESTIONS_TAGS_TAG_FK + ") REFERENCES "
			+ TABLE_TAGS + "(" + FIELD_TAGS_PK + "));";

	private static final String CREATE_TABLE_ANSWERS = "CREATE TABLE "
			+ TABLE_ANSWERS + " (" + FIELD_ANSWERS_PK + " integer primary key,"
			+ FIELD_ANSWERS_ANSWER_VALUE + " varchar(300), "
			+ FIELD_ANSWERS_QUESTION_FK + " integer, FOREIGN KEY("
			+ FIELD_ANSWERS_QUESTION_FK + ") REFERENCES " + TABLE_QUESTIONS
			+ "(" + FIELD_QUESTIONS_PK + "));";

	/**
	 * Default constructor. We reinstantiate it so that we can pass the DB name
	 * to it.
	 * 
	 * @param context
	 *            Application context.
	 */
	public SQLiteCacheHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {

		database.execSQL(CREATE_TABLE_QUESTIONS);
		database.execSQL(CREATE_TABLE_TAGS);
		database.execSQL(CREATE_TABLE_QUESTIONS_TAGS);
		database.execSQL(CREATE_TABLE_ANSWERS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SQLiteCacheHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANSWERS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS_TAGS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
		onCreate(db);
	}
}
