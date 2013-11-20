package epfl.sweng.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.patterns.JsonToQuestionsAdapter;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.test.minimalmock.MockHttpClient;

import junit.framework.TestCase;

public class JsonToQuestionAdapterTest extends TestCase{
	/*private MockHttpClient httpClient;
	
	//TODO find a way to post a JSONObject in the mockHttpClient
	public void testRetrieveQuizQuestions() {
        httpClient.pushCannedResponse(
                "POST (?:https?://[^/]+|[^/]+)?/+quizquestions/search\\b",
                HttpStatus.SC_OK,
                "{\"question\": \"What is the answer to life, the universe, and everything?\","
                + " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
                + " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }",
                "application/json");
	}
	
	public void testRetrieveQuizQuestionsExampleGivenInHW() {
		QuizQuery query = new QuizQuery("(banana + garlic) fruit");
		List<QuizQuestion> questions = JsonToQuestionsAdapter.retrieveQuizQuestions(query);
		assertEquals("How many calories are in a banana?", questions.get(0).getQuestionStatement());
	}
	*/
	
	public void testfillQuizQuestion() {
		QuizQuestion question1 = new QuizQuestion("q", new ArrayList<String>(
				Arrays.asList("a1", "a2", "a3")), 1, new TreeSet<String>(
						Arrays.asList("t1", "t2")), 1, "o");
		QuizQuestion question2 = new QuizQuestion("r", new ArrayList<String>(
				Arrays.asList("b1", "b2", "b3")), 0, new TreeSet<String>(
						Arrays.asList("u1", "u2")), 2, "p");
		List<QuizQuestion> questions = new ArrayList<QuizQuestion>();
		try {
			JSONObject json = createListQuestion();
			questions = JsonToQuestionsAdapter.fillQuizQuestionListFromQuery(json);
			assertEquals(question1, questions.get(0));
			assertEquals(question2, questions.get(1));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private JSONObject createListQuestion(){
		JSONObject json1 = new MockJSON(1, "q", new ArrayList<String>(
				Arrays.asList("a1", "a2", "a3")), 1, new TreeSet<String>(
						Arrays.asList("t1", "t2")), "o");
		JSONObject json2 = new MockJSON(2, "r", new ArrayList<String>(
				Arrays.asList("b1", "b2", "b3")), 0, new TreeSet<String>(
						Arrays.asList("u1", "u2")), "p");
		JSONArray jsa = new JSONArray();
		jsa.put(json1);
		jsa.put(json2);
		JSONObject json = new JSONObject();
		try {
			json.put("questions", jsa);
			json.put("next", null);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
}
