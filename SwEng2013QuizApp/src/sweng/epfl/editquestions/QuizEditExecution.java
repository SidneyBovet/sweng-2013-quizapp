package sweng.epfl.editquestions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;

import epfl.sweng.R;
import epfl.sweng.servercomm.SwengHttpClientFactory;

import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.LinearLayout;

public class QuizEditExecution extends
		AsyncTask<ArrayList<String>, Void, String> {
	private final static String SERVER_URL = "https://sweng-quiz.appspot.com";

	@Override
	protected String doInBackground(ArrayList<String>... listElem) {

		HttpPost post = new HttpPost(SERVER_URL + "/quizquestions/");

		generatePostentity(post, listElem[0]);
		ResponseHandler<String> handler = new BasicResponseHandler();
		String response;
		try {
			response = SwengHttpClientFactory.getInstance().execute(post,
					handler);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// TODO FUNCTION : CLEAR VIEW OF QUIZ EDITION

		return null;
	}

	private void generatePostentity(HttpPost post, ArrayList<String> listElem) {

		int solutionIndex = 0; // THIS A DUMMY VALUE.
		// We need to split the last element of the ArrayList in order to have
		// the the tags separated
		String questionText = "\"" + listElem.remove(0) + "\"";
		String tagsInOneLine = listElem.remove(listElem.size() - 1);
		String formattedTags = tagsInOneLine.replaceAll("\\W", "\", \"");
		formattedTags = "\"" + formattedTags + "\"";
		// now we need to get the questions and transform them for the JSon
		// Format
		String answerInOneLine = null;
		for (int i = 0; i < listElem.size(); i++) {
			if (listElem.size() == 1) {
				answerInOneLine = listElem.get(i);
			} else {
				answerInOneLine = listElem.get(i) + "\", \"";
			}
		}
		answerInOneLine = "\"" + answerInOneLine + "\"";

		String questionJsonFormat = " \"question\": " + questionText + ",";
		String answersJsonFormat = " \"answers\": [ " + answerInOneLine + " ],";
		String solutionIndexJsonFormat = " \"solutionIndex\": " + solutionIndex
				+ ",";
		String tagsJsonFormat = " \"tags\": [ " + formattedTags + " ]";
		try {
			post.setEntity(new StringEntity("{" + questionJsonFormat
					+ answersJsonFormat + solutionIndexJsonFormat
					+ tagsJsonFormat + " }"));

			post.setHeader("Content-type", "application/json");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
