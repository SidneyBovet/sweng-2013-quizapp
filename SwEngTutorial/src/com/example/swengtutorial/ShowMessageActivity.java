package com.example.swengtutorial;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;
/***
 * 
 * @author Merok
 *
 */
public class ShowMessageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_message);

		// get the Intent that started this Activity
		Intent startingIntent = getIntent();
		
		// get the value of the user string
		String userString = startingIntent.getStringExtra(MainActivity.class.getName());
		
		TextView textview = (TextView) findViewById(R.id.displayed_text);
		textview.setText(userString);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_message, menu);
		return true;
	}

}
