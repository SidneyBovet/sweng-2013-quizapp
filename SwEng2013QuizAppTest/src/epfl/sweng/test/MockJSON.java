package epfl.sweng.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class MockJSON extends JSONObject {
	private int mId;
	private String mQuestion;
	private List<String> mAnswers = new ArrayList<String>();
	private int mSolutionIndex;
	private Set<String> mTags = new TreeSet<String>();
	private String mOwner;
	private JSONObject mockJSON;

	public MockJSON(int id, String question, List<String> answers,
			int solutionIndex, Set<String> tags, String owner) {
		mId = id;
		mQuestion = question;
		mAnswers = answers;
		mSolutionIndex = solutionIndex;
		mTags = tags;
		mOwner = owner;
		JSONArray answJSONArray = new JSONArray();
		for (String answr : mAnswers) {
			answJSONArray.put(answr);
		}
		JSONArray tagsJSONArray = new JSONArray();
		for (String tgs : mTags) {
			tagsJSONArray.put(tgs);
		}
		mockJSON = new JSONObject();

		try {
			mockJSON.put("id", mId);
			mockJSON.put("question", mQuestion);
			mockJSON.put("answers", answJSONArray);
			mockJSON.put("solutionIndex", mSolutionIndex);
			mockJSON.put("tags", tagsJSONArray);
			mockJSON.put("owner", mOwner);
		} catch (JSONException e) {
			Log.e(this.getClass().getName(),
					"MockJSON(): could not parse JSON", e);
		}
	}

	public JSONObject getJson() {
		return mockJSON;

	}
}
