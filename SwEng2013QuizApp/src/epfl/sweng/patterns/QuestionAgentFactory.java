package epfl.sweng.patterns;

import android.content.Context;
import epfl.sweng.backend.QuestionAgent;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.caching.CachedQuestionAgent;
import epfl.sweng.preferences.UserPreferences;
import epfl.sweng.showquestions.ShowQuestionsAgent;

public class QuestionAgentFactory {
	
	private static boolean isFreeToCreate = true;
	private static QuestionAgent instanceSet = null;
	
	/**
	 * Creates an agent according to the connectivity state of the application.
	 * This agent will then proceed to retrieve questions for a client activity.
	 * 
	 * @param ctx The context of the activity using the agent
	 * @param query The query that will be passed to the agent
	 * @return The created agent
	 */
	public static QuestionAgent getAgent(Context ctx, QuizQuery query) {
		if (!isFreeToCreate) {
			return instanceSet;
		} else if (UserPreferences.getInstance(ctx).isConnected()) {
			return new ShowQuestionsAgent(query);
		} else {
			return new CachedQuestionAgent(query, ctx);
		}
	}
	
	public static void setInstance(QuestionAgent agent) {
		isFreeToCreate = false;
		instanceSet = agent;
	}
	
	public static void releaseInstance() {
		isFreeToCreate = true;
		instanceSet = null;
	}
}
