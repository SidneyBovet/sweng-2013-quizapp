package epfl.sweng.entry;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import epfl.sweng.R;

/**
 * This class will take care of the authentication to the
 * Tequila EPFL server. 
 * @author born4new
 *
 */
public class AuthenticationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authentication);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.authentication, menu);
		return true;
	}

}
