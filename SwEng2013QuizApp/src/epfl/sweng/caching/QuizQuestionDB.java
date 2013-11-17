package epfl.sweng.caching;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import epfl.sweng.quizquestions.QuizQuestion;

//See: http://a-renouard.developpez.com/tutoriels/android/sqlite/ for inspiration
public class QuizQuestionDB {
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "persistent";
    private static final String CACHE_TABLE_NAME = "questionCache";
 
	private static final String COL_ID = "ID";
	private static final int NUM_COL_ID = 0;
	private static final String COL_CONTENT = "CONTENT";
	private static final int NUM_COL_CONTENT = 1;
	private static final String COL_ANSWER = "ANSWER";
	private static final int NUM_COL_ANSWER = 2;
	private static final String COL_TAG = "TAG";
	private static final int NUM_COL_TAG = 3;
	private static final String COL_OWNER = "OWNER";
	private static final int NUM_COL_OWNER  = 4;
	private static final String COL_IDSOL = "IDSOL";
	private static final int NUM_COL_IDSOL  = 5;
	
	private static final String[] result_columns = new String[] {
		COL_ID, COL_CONTENT, COL_ANSWER, COL_TAG, COL_OWNER, COL_IDSOL
	};
	
	private SQLiteDatabase bdd;
	 
	private CacheOpenHelper cache;
	
	public QuizQuestionDB(Context context){
		//On crée la BDD et sa table
		cache  = new CacheOpenHelper(context);
	}
 
	public void open(){
		//on ouvre la BDD en écriture
		bdd = cache.getWritableDatabase();
	}
 
	public void close(){
		//on ferme l'accès à la BDD
		bdd.close();
	}
 
	public SQLiteDatabase getBDD(){
		return bdd;
	}
	
	public long insertQuizQuestion(QuizQuestion question){
		//Création d'un ContentValues (fonctionne comme une HashMap)
		ContentValues values = new ContentValues();
		//on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
		values.put(COL_ID, question.getId());
		values.put(COL_CONTENT, question.getQuestionStatement());
		values.put(COL_ANSWER, question.getAnswers().get(0));
		//values.put(COL_TAG, question.getTags());
		values.put(COL_OWNER, question.getOwner());
		values.put(COL_IDSOL, question.getSolutionIndex());
		//on insère l'objet dans la BDD via le ContentValues
		return bdd.insert(CACHE_TABLE_NAME, null, values);
	}
	
	public int updateQuizQuestion(QuizQuestion question){
		//La mise à jour d'un livre dans la BDD fonctionne plus ou moins comme une insertion
		//il faut simplement préciser quel livre on doit mettre à jour grâce à l'ID
		ContentValues values = new ContentValues();
		//on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
		values.put(COL_CONTENT, question.getQuestionStatement());
		values.put(COL_ANSWER, question.getAnswers().get(0));
		//values.put(COL_TAG, question.getTags());
		values.put(COL_OWNER, question.getOwner());
		values.put(COL_IDSOL, question.getSolutionIndex());
		return bdd.update(CACHE_TABLE_NAME, values, COL_ID + " = " + question.getId(), null);
	}
	
	public int removeQuizQuestionWithID(int id){
		//Suppression d'un livre de la BDD grâce à l'ID
		return bdd.delete(CACHE_TABLE_NAME, COL_ID + " = " +id, null);
	}
 
	//XXX to quizquestion cursorToQuizQuestion(Cursor c) to build
	public String getQuizQuestionWithContent (String content){
		//Récupère dans un Cursor les valeurs correspondant à un livre contenu dans la BDD (ici on sélectionne le livre grâce à son titre)
		Cursor c = bdd.query(CACHE_TABLE_NAME, result_columns, COL_CONTENT + " LIKE \"" + content +"\"", null, null, null, null);
		return c.getString(NUM_COL_OWNER);
	}
	
}