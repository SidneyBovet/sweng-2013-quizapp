package epfl.sweng.servercomm;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import epfl.sweng.backend.Question;
import epfl.sweng.exceptions.ServerSubmitFailedException;

/**
 * The <code>ServerInteractions</code> class contains methods for performing
 * network operations, such as {@link #getRandomQuestion()} and
 * {@link #submitQuestion(List)}.
 * 
 * @author Sidney
 * 
 */
public class ServerInteractions {
	
	/**
	 * Gets a question from the SwEng server. The request is an
	 * {@link AsyncTask} process.
	 * 
	 * @return A random {@link Question}.
	 */

	public static Question getRandomQuestion() {
		JSONDownloader asyncTaskRandomQuestionGetter = new JSONDownloader();
		asyncTaskRandomQuestionGetter.execute(HttpFactory.getSwengFetchQuestion());

		Question question = null;
		try {
			question = Question
					.createQuestionFromJSON(asyncTaskRandomQuestionGetter.get());
		} catch (JSONException e) {
			Log.e(ServerInteractions.class.getName(), "getRandomQuestion(): "
					+ "Unable to parse \'JSONObject\'.", e);
		} catch (InterruptedException e) {
			Log.e(ServerInteractions.class.getName(), "getRandomQuestion(): "
					+ "The \'DownloadJSONFromServer\' task has been "
					+ "interrupted.", e);
		} catch (ExecutionException e) {
			Log.e(ServerInteractions.class.getName(), "getRandomQuestion(): "
					+ "An error has occurred during the "
					+ "\'DownloadJSONFromServer\' execution.", e);
		}

		return question;
	}

	/**
	 * Sends a new question to the SwEng server. The request is an
	 * {@link AsyncTask} process.
	 * 
	 * @param The
	 *            {@link Question} to send.
	 * @throws ServerSubmitFailedException
	 *             Wrapper exception to tell the user that something has gone
	 *             wrong.
	 */
	public static int submitQuestion(List<String> listInputGUI)
		throws ServerSubmitFailedException {

		Question questionToSubmit = Question
				.createQuestionFromList(listInputGUI);
		JSONObject jsonToSubmit = questionToSubmit.toJSON();

		// We launch the AsyncTask that will do the submit in the background.
		QuizEditExecution quizEditExecute = new QuizEditExecution();
		quizEditExecute.execute(jsonToSubmit);

		int serverResponse = -1;
		try {
			// XXX RED ALERT! THIS SEEMS TO BE A MISTAKE!
			// XXX SHOULD BE REFACTORED IN "ONPOSTEXECUTE".
			serverResponse = quizEditExecute.get();
		} catch (InterruptedException e) {
			Log.e(ServerInteractions.class.getName(), "submitQuestion(): The "
					+ "The \'QuizEditExecution\' task has been interrupted.", e);
			// Here we don't care about the type of exception thrown:
			// If there is one, we will inform the user that ST went wrong.
			// Note that the exceptions thrown are unlikely to happen since
			// they are thread-related only.
		} catch (ExecutionException e) {
			Log.e(ServerInteractions.class.getName(), "submitQuestion(): An "
					+ "error has occurred during the \'QuizEditExecution\' "
					+ "execution.", e);
			// Same as above.
		}

		if (serverResponse == -1) {
			throw new ServerSubmitFailedException("Server error.");
		}

		return serverResponse;
	}
}
