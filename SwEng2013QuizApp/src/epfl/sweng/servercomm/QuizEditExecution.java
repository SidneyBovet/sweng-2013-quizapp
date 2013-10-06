package epfl.sweng.servercomm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;

import android.os.AsyncTask;
import epfl.sweng.testing.TestingTransactions;
import epfl.sweng.testing.TestingTransactions.TTChecks;
/**
 * Asyncrounous task that executes smthing in background.
 * 
 * @author Merok
 *
 */
// TODO : change to QuizEditTask
public class QuizEditExecution extends
		AsyncTask<ArrayList<String>, Void, String> {
	private final static String SERVER_URL = "https://sweng-quiz.appspot.com";

	@Override
	protected String doInBackground(ArrayList<String>... listElem) {

		HttpPost post = new HttpPost(SERVER_URL + "/quizquestions/");
		
		generatePostentity(post, listElem[0]);

		return null;
	}

	private void generatePostentity(HttpPost post, ArrayList<String> listElem) {
		// Get the values and format them
		int solutionIndex = 0; // THIS A DUMMY VALUE.
		// We need to split the last element of the ArrayList in order to have
		// the the tags separated
		String questionText = "\"" + listElem.remove(0) + "\"";
		String tagsInOneLine = listElem.remove(listElem.size() - 1);
		String formattedTags = tagsInOneLine.replaceAll("\\W", "\", \"");
		formattedTags = "\"" + formattedTags + "\"";
		// now we need to get the questions and transform them for the JSon
		// Format
		String answerInOneLine = listElem.get(0);
		for (int i = 1; i < listElem.size(); i++) {
			answerInOneLine = answerInOneLine + "\", \"" + listElem.get(i);
		}
		answerInOneLine = "\"" + answerInOneLine + "\"";

		String questionJsonFormat = " \"question\": " + questionText + ",";
		String answersJsonFormat = " \"answers\": [ " + answerInOneLine + " ],";
		String solutionIndexJsonFormat = " \"solutionIndex\": " + solutionIndex
				+ ",";
		String tagsJsonFormat = " \"tags\": [ " + formattedTags + " ]";
		
		//send the quiz
		ResponseHandler<String> handler = new BasicResponseHandler();
		try {
			post.setEntity(new StringEntity("{" + questionJsonFormat
					+ answersJsonFormat + solutionIndexJsonFormat
					+ tagsJsonFormat + " }"));
			post.setHeader("Content-type", "application/json");
			SwengHttpClientFactory.getInstance().execute(post,
					handler);
			TestingTransactions.check(TTChecks.NEW_QUESTION_SUBMITTED);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
