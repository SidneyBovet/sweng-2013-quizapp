package epfl.sweng.test.activities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import epfl.sweng.authentication.UserPreferences;
import epfl.sweng.patterns.QuestionsProxy;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.testing.TestCoordinator.TTChecks;

//import java.io.IOException;

/** A test that illustrates the use of MockHttpClients */
// XXX : Tests work separetely, find out why it doesn't work together.
public class ShowQuestionsActivityOfflineTest extends GUITest<ShowQuestionsActivity> {

	protected static final String RANDOM_QUESTION_BUTTON_LABEL = "Show a random question";

	public ShowQuestionsActivityOfflineTest() {
		super(ShowQuestionsActivity.class);
	}

	@Override
	public void setUp() {
		super.setUp();
		UserPreferences.getInstance(getInstrumentation().getContext()).
			createEntry("CONNECTION_STATE", "OFFLINE");

		List<String> answers = new ArrayList<String>();
		answers.add("100% accurate");
		answers.add("Fully voodoo and could generate non-pseudorandom numbers");

		Set<String> tags = new HashSet<String>();
		tags.add("robotium");
		tags.add("testing");
		
		QuizQuestion question = new QuizQuestion(
				"How reliable Robotium testing is?", answers, 1, tags);
		QuestionsProxy.getInstance().addInbox(question);
	}

	public void testOnlyQuestionInInboxIsDisplayed() {
		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
		assertTrue(
				"Question must be displayed",
				getSolo()
						.searchText(
								"How reliable Robotium testing is?"));
		assertTrue("Incorrect answer must be displayed",
				getSolo().searchText("100% accurate"));
		assertTrue("Correct answer must be displayed",
				getSolo().searchText("Fully voodoo and could generate non-" +
						"pseudorandom numbers"));
	}
}
