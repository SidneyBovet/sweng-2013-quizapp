//package epfl.sweng.caching;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import epfl.sweng.quizquestions.QuizQuestion;
//
////See: http://a-renouard.developpez.com/tutoriels/android/sqlite/ for inspiration
//public class QuizQuestionDB {
//	private static final int DATABASE_VERSION = 2;
//	private static final String DATABASE_NAME = "persistent";
//    private static final String CACHE_TABLE_NAME = "questionCache";
// 
//	private static final String COL_ID = "ID";
//	private static final int NUM_COL_ID = 0;
//	private static final String COL_CONTENT = "CONTENT";
//	private static final int NUM_COL_CONTENT = 1;
//	private static final String COL_ANSWER = "ANSWER";
//	private static final int NUM_COL_ANSWER = 2;
//	private static final String COL_TAG = "TAG";
//	private static final int NUM_COL_TAG = 3;
//	private static final String COL_OWNER = "OWNER";
//	private static final int NUM_COL_OWNER  = 4;
//	private static final String COL_IDSOL = "IDSOL";
//	private static final int NUM_COL_IDSOL  = 5;
//	
//	private static final String[] result_columns = new String[] {
//		COL_ID, COL_CONTENT, COL_ANSWER, COL_TAG, COL_OWNER, COL_IDSOL
//	};
//	
//	private SQLiteDatabase bdd;
//	 
//	private SQLiteCacheHelper cache;
//	
//	public QuizQuestionDB(Context context){
//		//On cr��e la BDD et sa table
//		cache  = new SQLiteCacheHelper(context);
//	}
// 
//	public void open(){
//		//on ouvre la BDD en ��criture
//		bdd = cache.getWritableDatabase();
//	}
// 
//	public void close(){
//		//on ferme l'acc��s �� la BDD
//		bdd.close();
//	}
// 
//	public SQLiteDatabase getBDD(){
//		return bdd;
//	}
//	
//	public long insertQuizQuestion(QuizQuestion question){
//		//Cr��ation d'un ContentValues (fonctionne comme une HashMap)
//		ContentValues values = new ContentValues();
//		//on lui ajoute une valeur associ��e �� une cl�� (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
//		values.put(COL_ID, question.getId());
//		values.put(COL_CONTENT, question.getQuestionStatement());
//		values.put(COL_ANSWER, question.getAnswers().get(0));
//		//values.put(COL_TAG, question.getTags());
//		values.put(COL_OWNER, question.getOwner());
//		values.put(COL_IDSOL, question.getSolutionIndex());
//		//on ins��re l'objet dans la BDD via le ContentValues
//		return bdd.insert(CACHE_TABLE_NAME, null, values);
//	}
//	
//	public int updateQuizQuestion(QuizQuestion question){
//		//La mise �� jour d'un livre dans la BDD fonctionne plus ou moins comme une insertion
//		//il faut simplement pr��ciser quel livre on doit mettre �� jour gr��ce �� l'ID
//		ContentValues values = new ContentValues();
//		//on lui ajoute une valeur associ��e �� une cl�� (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
//		values.put(COL_CONTENT, question.getQuestionStatement());
//		values.put(COL_ANSWER, question.getAnswers().get(0));
//		//values.put(COL_TAG, question.getTags());
//		values.put(COL_OWNER, question.getOwner());
//		values.put(COL_IDSOL, question.getSolutionIndex());
//		return bdd.update(CACHE_TABLE_NAME, values, COL_ID + " = " + question.getId(), null);
//	}
//	
//	public int removeQuizQuestionWithID(int id){
//		//Suppression d'un livre de la BDD gr��ce �� l'ID
//		return bdd.delete(CACHE_TABLE_NAME, COL_ID + " = " +id, null);
//	}
// 
//	//XXX to quizquestion cursorToQuizQuestion(Cursor c) to build
//	/* see: http://www.vogella.com/articles/AndroidSQLite/article.html
//	 * To get the number of elements of the resulting query use the getCount() method.
//		To move between individual data rows, you can use the moveToFirst() 
//		and moveToNext() methods. The isAfterLast() method allows to check if the end of the query result has been reached.
//	 */
//	public String getQuizQuestionWithContent (String content){
//		//R��cup��re dans un Cursor les valeurs correspondant �� un livre contenu dans la BDD (ici on s��lectionne le livre gr��ce �� son titre)
//		Cursor c = bdd.query(CACHE_TABLE_NAME, result_columns, COL_CONTENT + " LIKE \"" + content +"\"", null, null, null, null);
//		return c.getString(NUM_COL_OWNER);
//	
//	}
//	
//}