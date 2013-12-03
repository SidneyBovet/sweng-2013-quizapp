package epfl.sweng.agents;

import epfl.sweng.backend.QuizQuery;
import epfl.sweng.preferences.UserPreferences;

public abstract class QuestionAgentFactory {
	
	private static boolean isFreeToCreate = true;
	private static QuestionAgent instanceSet = null;
	
	/**
	 * Creates an agent according to the connectivity state of the application.
	 * This agent will then proceed to retrieve questions for a client activity.
	 * 
	 * @param query The query that will be passed to the agent
	 * @return The created agent
	 */
	public static QuestionAgent getAgent(QuizQuery query) {
		if (!isFreeToCreate) {
			return instanceSet;
		} else if (UserPreferences.getInstance().isConnected()) {
			return new OnlineQuestionsAgent(query);
		} else {
			return new CachedQuestionAgent(query);
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
