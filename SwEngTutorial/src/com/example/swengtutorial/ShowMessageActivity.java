package com.example.swengtutorial;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;
<<<<<<< HEAD

=======
/***
 * 
 * @author Merok
 *
 */
>>>>>>> f045a7d1588d1bba44162d724618e77cd32675b6
public class ShowMessageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_message);

<<<<<<< HEAD
	    // get Intent that started this Activity
	    Intent startingIntent = getIntent();

	    // get the value of the user string
	    String userText = startingIntent.getStringExtra(MainActivity.class.getName());

	    // get the TextView on which we are going to show the string, and update
	    // its contents
	    TextView textView = (TextView) findViewById(R.id.displayed_text);
	    textView.setText(userText);
=======
		// get the Intent that started this Activity
		Intent startingIntent = getIntent();
		
		// get the value of the user string
		String userString = startingIntent.getStringExtra(MainActivity.class.getName());
		
		TextView textview = (TextView) findViewById(R.id.displayed_text);
		textview.setText(userString);
>>>>>>> f045a7d1588d1bba44162d724618e77cd32675b6
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_message, menu);
		return true;
	}

}
