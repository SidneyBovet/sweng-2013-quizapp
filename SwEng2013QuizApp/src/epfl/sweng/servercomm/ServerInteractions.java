/**
 * 
 */
package epfl.sweng.servercomm;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import epfl.sweng.backend.Question;

/**
 * @author Sidney
 *
 */
public class ServerInteractions {
	
	/**
	 * Processes a request in an {@link AsyncTask}.
	 * 
	 * @return The parsed {@link Question}.
	 */
	public static Question getRandomQuestion() {
		DownloadJSONFromServer asyncTaskRandomQuestionGetter =
				new DownloadJSONFromServer();
		String url = "https://sweng-quiz.appspot.com/quizquestions/random"; 
		asyncTaskRandomQuestionGetter.execute(url);
		
		Question question = null;
		try {
			question = Question
					.createQuestionFromJSON(asyncTaskRandomQuestionGetter.get());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return question;
	}
	
	/**
	 * Sends a new question to the SwEng server
	 * @param The new {@link Question}
	 */
	public static void sendQuestion(Question newQuestion) {
		
	}
}
