package epfl.sweng.showquestions;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import epfl.sweng.R;
import epfl.sweng.backend.Question;
import epfl.sweng.servercomm.SwengHttpClientFactory;

/***
 * Activity used to display a random question to the user.
 * @author born4new
 *
 */
public class ShowQuestionsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_question);
			DownloadJSONFromServer asyncTaskRandomQuestionGetter = new DownloadJSONFromServer();
			asyncTaskRandomQuestionGetter.execute();

			Question firstQuestion = null;
			
			try {
				firstQuestion = Question.createQuestionFromJSON(asyncTaskRandomQuestionGetter.get());
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_question, menu);
		return true;
	}

}
