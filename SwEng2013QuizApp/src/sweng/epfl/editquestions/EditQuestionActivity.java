package sweng.epfl.editquestions;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import epfl.sweng.R;

/**
 * The user can now enter a question that will be saved on a server.
 * @author born4new
 *
 */
public class EditQuestionActivity extends Activity {

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

}
