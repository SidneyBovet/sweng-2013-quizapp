package epfl.sweng.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.test.MockJSON;

import junit.framework.TestCase;

public class ConvertTestt extends TestCase {
	private JSONArray jsonArray;
	private MockJSON mockJson1;
	private List<String> list = new ArrayList<String>();
	private JSONObject jsonObj;

	@Override
	protected void setUp() throws Exception {
		super.setUp(); 
		list.add("Hello");
		list.add("Lapin");
		mockJson1 = new MockJSON(1, "Salut?", list, 1,
				new TreeSet<String>(list), "bob");
		jsonArray = new JSONArray(list);
		jsonObj = mockJson1.getJson();
	}

	public void testjsonArrayToStringList() {
		assertTrue("method is not working",
				Converter.jsonArrayToStringList(jsonArray).containsAll(list));
	}

	public void testjsonArrayToQuizQuestionList() throws JSONException {
		List<QuizQuestion> listQuizQuestion = new ArrayList<QuizQuestion>();

		String str = jsonObj.toString();
		QuizQuestion question = new QuizQuestion(str);
		listQuizQuestion.add(question);
		JSONArray jsonArr = new JSONArray();
		jsonArr.put(jsonObj);

		List<QuizQuestion> listGenerated = Converter
				.jsonArrayToQuizQuestionList(jsonArr);

		assertTrue("method is not working",
				verifyListsAreEquals(listQuizQuestion, listGenerated));
	}

	private boolean verifyListsAreEquals(List<QuizQuestion> list1,
			List<QuizQuestion> list2) {
		if (list1.size() != list2.size()) {
			return false;
		} else {
			for (int i = 0; i < list1.size(); ++i) {
				if (list1.get(i).equalAs(list2.get(i))) {
					return false;
				}
			}
		}
		return true;
	}
}
