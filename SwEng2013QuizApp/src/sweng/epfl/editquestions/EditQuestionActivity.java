package sweng.epfl.editquestions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import epfl.sweng.R;
import epfl.sweng.SubmitQuizzActivity;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.showquestions.ShowQuestionsActivity;

/**
 * The user can now enter a question that will be saved on a server.
 * 
 * @author born4new
 * @author Merok
 * 
 */
public class EditQuestionActivity extends Activity {
	private final static String SERVER_URL = "https://sweng-quiz.appspot.com/quizquestions";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit_question);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.submit_question, menu);

		return true;
	}

	public void sendEditedQuestion(View view) {
		int a = 3;
		LinearLayout layout = (LinearLayout) findViewById(R.id.layoutEditQuestion);
		HttpPost post = new HttpPost(SERVER_URL + "/quizquestions/");
		Toast.makeText(this, "Quizz submitted to the server.",
				Toast.LENGTH_SHORT).show();

		// this is done in order to get the content of the EditText elements in
		// the XML
		// WARNING : you MUST follow this structure when you write the EditText
		// elements : Question1 => Answer1 => ... => indexOfAnswer => tags
		ArrayList<String> listElem = new ArrayList<String>();
		for (int i = 0; i < layout.getChildCount(); i++) {
			if (layout.getChildAt(i) instanceof EditText) {
				EditText currentEditText = (EditText) layout.getChildAt(i);
				String currentArgument = currentEditText.getText().toString();
				listElem.add(currentArgument);
			}
		}

		generatePostentity(post, listElem);
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
				answerInOneLine = listElem.get(i) + "\"";
			} 
			else {
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
