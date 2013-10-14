package epfl.sweng.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import epfl.sweng.testing.TestingTransaction;
import epfl.sweng.testing.TestingTransactions;

public class GUITest<T extends Activity> extends
ActivityInstrumentationTestCase2<T> {

	private Solo solo;
	
	public GUITest(Class<T> activityClass) {
		super(activityClass);
	}
	
	@Override
	protected void setUp() {
		solo = new Solo(getInstrumentation());
	}
	
	private void getActivityAndWaitFor(
			final TestingTransactions.TTChecks expected) {
		TestingTransactions.run(getInstrumentation(), new TestingTransaction() {
			@Override
			public void initiate() {
				getActivity();
			}

			@Override
			public void verify(TestingTransactions.TTChecks notification) {
				assertEquals(String.format(
						"Expected notification %s, but received %s", expected,
						notification), expected, notification);
			}

			@Override
			public String toString() {
				return String.format("getActivityAndWaitFor(%s)", expected);
			}
		});
	}
}
