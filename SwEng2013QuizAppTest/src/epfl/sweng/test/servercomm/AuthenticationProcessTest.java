package epfl.sweng.test.servercomm;

import java.util.concurrent.ExecutionException;

import epfl.sweng.authentication.AuthenticationActivity;
import epfl.sweng.servercomm.AuthenticationProcess;
import epfl.sweng.test.activities.GUITest;

public class AuthenticationProcessTest extends GUITest<AuthenticationActivity> {
	
	public AuthenticationProcessTest() {
		super(AuthenticationActivity.class);
	}
	
	@Override
	public void setUp() {
		
	}
	
	@Override
	public void tearDown() {
		
	}
	
	public void testDoInBackground() {
		AuthenticationProcess authProcess = new AuthenticationProcess(new AuthenticationActivity());
		authProcess.execute("dieulivo", "XXXXX");
		try {
			System.out.println(authProcess.get());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
