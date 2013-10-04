package epfl.sweng.backend;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONObject;

import android.os.AsyncTask;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.testing.TestingTransactions;
import epfl.sweng.testing.TestingTransactions.TTChecks;

/**
 * 
 * @author Merok
 * 
 */
public class QuizEditExecution extends AsyncTask<JSONObject, Void, Void> {
	private final static String SERVER_URL = "https://sweng-quiz.appspot.com";

	@Override
	protected Void doInBackground(JSONObject... jsonObject) {
		// XXX CAN I POST A JSONOBJECT ?
		HttpPost post = new HttpPost(SERVER_URL + "/quizquestions/");
		 //send the quiz
		 ResponseHandler<String> handler = new BasicResponseHandler();
		 try {
		 post.setEntity(new StringEntity(jsonObject[0].toString()));
		 post.setHeader("Content-type", "application/json");
		 SwengHttpClientFactory.getInstance().execute(post,
		 handler);
		 TestingTransactions.check(TTChecks.NEW_QUESTION_SUBMITTED);
		 } catch (UnsupportedEncodingException e) {
		 e.printStackTrace();
		 } catch (IOException e) {
		 e.printStackTrace();
		 }


		// generatePostentity(post, jsonObject[0]); NO USEFULL ANYMORE ?
		return null;
	}

//	private void generatePostentity(HttpPost post, JSONObject jsonObject) {
//		// Get the values and format them
//		int solutionIndex = 0; // THIS A DUMMY VALUE.
//		// We need to split the last element of the ArrayList in order to have
//		// the the tags separated
//		try {
//			String questionText = "\"" + jsonObject.getString("question")
//					+ "\"";
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		// String tagsInOneLine = jsonObject.get("answers");
		// NOT NEED ANYMORE ?
		// String formattedTags = tagsInOneLine.replaceAll("\\W", "\", \"");
		// formattedTags = "\"" + formattedTags + "\"";

		// now we need to get the questions and transform them for the JSon
		// Format
		// String answerInOneLine = listElem.get(0);
		// for (int i = 1; i < listElem.size(); i++) {
		// answerInOneLine = answerInOneLine + "\", \"" + listElem.get(i);
		// }
		// answerInOneLine = "\"" + answerInOneLine + "\"";
		//
		// String questionJsonFormat = " \"question\": " + questionText + ",";
		// String answersJsonFormat = " \"answers\": [ " + answerInOneLine +
		// " ],";
		// String solutionIndexJsonFormat = " \"solutionIndex\": " +
		// solutionIndex
		// + ",";
		// String tagsJsonFormat = " \"tags\": [ " + formattedTags + " ]";
		//
		// //send the quiz
		// ResponseHandler<String> handler = new BasicResponseHandler();
		// try {
		// post.setEntity(new StringEntity("{" + questionJsonFormat
		// + answersJsonFormat + solutionIndexJsonFormat
		// + tagsJsonFormat + " }"));
		// post.setHeader("Content-type", "application/json");
		// SwengHttpClientFactory.getInstance().execute(post,
		// handler);
		// TestingTransactions.check(TTChecks.NEW_QUESTION_SUBMITTED);
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
//	}
}