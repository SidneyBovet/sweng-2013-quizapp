package sweng.epfl.editquestions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import epfl.sweng.R;
import epfl.sweng.SubmitQuizzActivity;
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
		LinearLayout layout = (LinearLayout) findViewById(R.id.layoutEditQuestion);
		Toast.makeText(this, "Quizz submitted to the server.",
				Toast.LENGTH_SHORT).show();

		// this is done in order to get the content of the EditText elements in
		// the XML
		// WARNING : you MUST follow this structure when you write the EditText 
		//elements : Question1 => Answer1 => ... => indexOfAnswer => tags
		ArrayList<String> listElem = new ArrayList<String>();
		for (int i = 0; i < layout.getChildCount(); i++) {
			if (layout.getChildAt(i) instanceof EditText) {
				EditText currentEditText = (EditText) layout.getChildAt(i);
				String currentArgument = currentEditText.getText().toString();
				listElem.add(currentArgument);
			}
		}
		

		generatePostentity(listElem);
		/*
		 * post.setEntity(new StringEntity("{" +
		 * " \"question\": \"What is the answer to life, the universe and everything?\","
		 * + " \"answers\": [ \"42\", \"24\" ]," + " \"solutionIndex\": 0," +
		 * " \"tags\": [ \"h2g2\", \"trivia\" ]" + " }"));
		 * post.setHeader("Content-type", "application/json");
		 * ResponseHandler<String> handler = new BasicResponseHandler(); String
		 * response = SwengHttpClientFactory.getInstance().execute(post,
		 * handler);
		 */

	}

	private void generatePostentity(ArrayList<String> listElem) {
		HttpPost post = new HttpPost(SERVER_URL + "/quizquestions/");
		// We need to split the last element of the ArrayList in order to have
		// the the tags separated
		// tagA,tab2?tag3-tag5 => \"tagA\",\"tab2\",\"tag3\",\"tag5\" 
		String tagsInOneLine = listElem.get(listElem.size() - 1);
		String formattedTags = tagsInOneLine.replaceAll("\\W", "\", \"");
		formattedTags = "\""+formattedTags+"\"";
//		String formattedTags = tagsInOneLine.replaceAll(regularExpression, replacement)
//		String[] tags = tagsInOneLine.split("\\W");
		
		

		String questionJsonFormat = " \"question\":" + "\""
				+ listElem.remove(0) + "\",";
		String answersJsonFormat = " \"answers\": [ \"42\", \"24\" ],";
	
		try {
			post.setEntity(new StringEntity("{" + questionJsonFormat
					+ " \"answers\": [ \"" + 42 + "\", \"24\" ],"
					+ " \"solutionIndex\": 0,"
					+ " \"tags\": [ \"h2g2\", \"trivia\" ]" + " }"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
