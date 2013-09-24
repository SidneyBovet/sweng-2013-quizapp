package epfl.sweng;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

/***
 * Activity used to display a random question to the user.
 * @author born4new
 *
 */
public class DisplayQuestionActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_question);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_question, menu);
		return true;
	}

}
