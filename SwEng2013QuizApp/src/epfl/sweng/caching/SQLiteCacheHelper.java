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

	// Id's + Foreign Keys
	private static final String KEY_ID = "id";
	private static final String KEY_QUESTION_ID = "questionId";
	private static final String KEY_TAG_ID = "tagId";

	// QUESTIONS Table
	private static final String KEY_STATEMENT = "statement";
	private static final String KEY_SOLUTION_ID = "solutionId";
	private static final String KEY_OWNER = "owner";

	// TAGS Table
	private static final String KEY_TAG = "name";

	// ANSWERS Table
	private static final String KEY_ANSWER = "content";

	// Tables creation
	private static final String CREATE_TABLE_QUESTIONS = "CREATE TABLE "
			+ TABLE_QUESTIONS + " (" + KEY_ID + " integer primary key, "
			+ KEY_QUESTION_ID + " integer," + KEY_STATEMENT + " varchar(500), "
			+ KEY_SOLUTION_ID + " integer, " + KEY_OWNER + " varchar(500));";

	private static final String CREATE_TABLE_TAGS = "CREATE TABLE "
			+ TABLE_TAGS + " (" + KEY_ID + " integer primary key,  " + KEY_TAG
			+ " varchar(500));";

	private static final String CREATE_TABLE_QUESTIONS_TAGS = "CREATE TABLE "
			+ TABLE_QUESTIONS_TAGS + "(" + KEY_QUESTION_ID + " integer, "
			+ KEY_TAG_ID + " integer,FOREIGN KEY(" + KEY_QUESTION_ID
			+ ") REFERENCES " + TABLE_QUESTIONS + "(" + KEY_QUESTION_ID
			+ "),FOREIGN KEY(" + KEY_TAG_ID + ") REFERENCES " + TABLE_TAGS
			+ "(" + KEY_ID + "));";

	private static final String CREATE_TABLE_ANSWERS = "CREATE TABLE "
			+ TABLE_ANSWERS + " (" + KEY_ID + " integer primary key," + ""
			+ KEY_ANSWER + " varchar(300), " + KEY_QUESTION_ID
			+ " integer, FOREIGN KEY(" + KEY_QUESTION_ID + ") REFERENCES "
			+ TABLE_QUESTIONS + "(" + KEY_QUESTION_ID + ");";

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
