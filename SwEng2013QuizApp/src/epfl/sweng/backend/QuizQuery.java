package epfl.sweng.backend;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import epfl.sweng.interpreter.QueryLexer;
import epfl.sweng.interpreter.QueryParser;

/**
 * Used to filter data from the SwEng server.
 * 
 * @author born4new
 * @author Merok
 * 
 */
public class QuizQuery implements Parcelable {

	private String mQuery;
	private String mFrom;
	
	public QuizQuery(String query, String from) {
		this.mQuery = query;
		this.mFrom = from;
	}
	
	
	/**
	 * Verify that that the querry has a syntax that follow the correct Grammar
	 * (i.e ")(banana++ fruit)" is not accepted). See Query.g for the grammar.
	 * 
	 * @param query
	 *            to be verified.
	 * @return true if it has a good Syntax, false otherwise.
	 */
	
	public boolean hasGoodSyntax(String query) {
		boolean ok = true;
		ANTLRStringStream in = new ANTLRStringStream(query);
		QueryLexer lexer = new QueryLexer(in);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		QueryParser parser = new QueryParser(tokens);
		
		try {
			parser.eval();
		} catch (RecognitionException e) {
			ok = false;
		}
		return ok;
	}
	
	/**
	 * Returns a {@link JSONObject} representing the current query.
	 * 
	 * @return A JSONObject of the question.
	 */
	
	public JSONObject toJSON() throws JSONException {
		JSONObject jsonQuery = new JSONObject();
		jsonQuery.put("query", mQuery);
		if (mFrom != null) {
			jsonQuery.put("from", mFrom);
		}
		return jsonQuery;
	}
	
	public String getQuery() {
		return mQuery;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mQuery);
		dest.writeString(mFrom);
	}
	
	public static final Parcelable.Creator<QuizQuery> CREATOR = new 
			Parcelable.Creator<QuizQuery>() {
		public QuizQuery createFromParcel(Parcel in) {
			return new QuizQuery(in);
		}
		
		public QuizQuery[] newArray(int size) {
			return new QuizQuery[size];
		}
	};
	
	private QuizQuery(Parcel in) {
		mQuery = in.readString();
		mFrom = in.readString();
	}
}
