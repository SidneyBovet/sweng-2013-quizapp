package epfl.sweng.servercomm;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

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
		DownloadJSONFromServer asyncTaskRandomQuestionGetter = new DownloadJSONFromServer();
		String url = "https://sweng-quiz.appspot.com/quizquestions/random";
		asyncTaskRandomQuestionGetter.execute(url);

		Question question = null;
		try {
			question = Question
					.createQuestionFromJSON(asyncTaskRandomQuestionGetter.get());
		} catch (JSONException e) {
			// TODO Log it! Problem with JSON Parsing
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Log it!
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Log it!
			e.printStackTrace();
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
		JSONObject jsonToSubmit = Question
				.createJSONFromQuestion(questionToSubmit);

		// We launch the AsyncTask that will do the submit in the background.
		QuizEditExecution quizEditExecute = new QuizEditExecution();
		quizEditExecute.execute(jsonToSubmit);

		int serverResponse = -1;
		try {
			serverResponse = quizEditExecute.get();
		} catch (InterruptedException e) {
			// TODO Log it! Internal problem with Thread.
			// Here we don't care about the type of exception thrown:
			// If there is one, we will inform the user that ST went wrong.
			// Note that the exceptions thrown are unlikely to happen since
			// they are thread-related only.
		} catch (ExecutionException e) {
			// TODO Log it!
			// Same as above.
		}

		if (serverResponse == -1) {
			throw new ServerSubmitFailedException("Server error");
		}

		return serverResponse;
	}
}
