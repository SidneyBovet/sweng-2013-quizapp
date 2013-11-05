package epfl.sweng.test.activities;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import epfl.sweng.authentication.UserPreferences;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestingTransaction;

public class GUITest<T extends Activity> extends
		ActivityInstrumentationTestCase2<T> {
	private Solo solo;

	public GUITest(Class<T> activityClass) {
		super(activityClass);
	}

	@Override
	protected void setUp() {
		solo = new Solo(getInstrumentation());
		UserPreferences.getInstance(getInstrumentation().getContext());
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}

	protected void getActivityAndWaitFor(final TestCoordinator.TTChecks expected) {
		TestCoordinator.run(getInstrumentation(), new TestingTransaction() {
			@Override
			public void initiate() {
				getActivity();
			}

			@Override
			public void verify(TestCoordinator.TTChecks notification) {
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

	protected void waitFor(final TestCoordinator.TTChecks expected) {
		TestCoordinator.run(getInstrumentation(), new TestingTransaction() {
			@Override
			public void initiate() {
				// Nothing to do here...
			}

			@Override
			public void verify(TestCoordinator.TTChecks notification) {
				assertEquals(String.format(
						"Expected notification %s, but received %s", expected,
						notification), expected, notification);
			}

			@Override
			public String toString() {
				return String.format("WaitFor(%s)", expected);
			}
		});
		solo.sleep(500);
	}

	public Solo getSolo() {
		return solo;
	}
}
