package epfl.sweng.backend;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.generated.QueryLexer;
import epfl.sweng.generated.QueryParser;

import android.os.Parcel;
import android.os.Parcelable;


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

	/**
	 * Constructor for search query
	 * 
	 * @param query
	 *            The search query.
	 * @param from
	 *            Indicator used to pursue the search of a query.
	 */
	public QuizQuery(String query, String from) {
		this.mQuery = query;
		this.mFrom = from;
	}

	/**
	 * Constructor for random query
	 * 
	 * @param from
	 */
	public QuizQuery() {
		mQuery = null;
		mFrom = null;
	}

	/**
	 * Verifies that that the query has a syntax that follows the correct
	 * Grammar (i.e ")(banana++ fruit)" is not accepted). See Query.g for the
	 * grammar.
	 * 
	 * @param query
	 *            Query to be verified.
	 * @return {@code true} if it has a good syntax, {@code false} otherwise.
	 */

	public boolean hasGoodSyntax() {
		boolean ok = true;
		ANTLRStringStream in = new ANTLRStringStream(mQuery);
		QueryLexer lexer = new QueryLexer(in);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		QueryParser parser = new QueryParser(tokens);

		try {
			parser.eval();
		} catch (Throwable e) {
			ok = false;
		}
		
		return ok;
	}

	
	
	@Override
	public String toString() {
		return mQuery;
	}

	/**
	 * Returns a {@link JSONObject} representing the current query.
	 * 
	 * @return A {@link JSONObject} of the query.
	 */

	public JSONObject toJSON() throws JSONException {
		JSONObject jsonQuery = new JSONObject();
		jsonQuery.put("query", mQuery);
		if (mFrom != null) {
			jsonQuery.put("from", mFrom);
		}
		return jsonQuery;
	}

	/**
	 * Returns the string representation of the query.
	 * 
	 * @return The string representation of the query.
	 */

	public String getQuery() {
		return mQuery;
	}

	/**
	 * Returns the string representation of the next hash.
	 * 
	 * @return The string representation of the next hash.
	 */

	public String getFrom() {
		return mFrom;
	}

	public boolean isRandom() {
		return null == mQuery;
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

	public static final Parcelable.Creator<QuizQuery> CREATOR = new Parcelable.Creator<QuizQuery>() {
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
