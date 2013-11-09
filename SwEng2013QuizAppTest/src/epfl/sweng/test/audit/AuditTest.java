package epfl.sweng.test.audit;

import java.util.concurrent.Semaphore;

import android.widget.Button;
import android.widget.EditText;
import epfl.sweng.R;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.test.activities.GUITest;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class AuditTest extends GUITest<EditQuestionActivity> {

	public AuditTest() {
		super(EditQuestionActivity.class);
	}

	@Override
	protected void setUp() {
		super.setUp();
		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
		// add stuff we need
	}

	public void testAudit() {

		final Semaphore semaphore = new Semaphore(0);

		getSolo().sleep(500);
		assertTrue("Audit fresh Activity is not zero", getActivity()
				.auditErrors() == 0);

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				EditQuestionActivity activity = (EditQuestionActivity) getActivity();
				Button button = (Button) activity
						.findViewById(R.id.submit_question_button);
				button.setEnabled(true);
				semaphore.release();
			}
		});

		try {
			semaphore.acquire();
			getSolo().sleep(500);
			assertTrue("# Audit errors = " + getActivity().auditErrors()
					+ " != 1", getActivity().auditErrors() == 1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"answer D");
		getSolo().clickOnButton("+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "my question1");

		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag");

		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"answer BBBBBB");
		getSolo().clickOnButton("" + (char) 10008);
		waitFor(TTChecks.QUESTION_EDITED);

		assertTrue("Number of audit errors = " + getActivity().auditErrors()
				+ " != 0", getActivity().auditErrors() == 0);

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				EditQuestionActivity activity = (EditQuestionActivity) getActivity();
				Button button = (Button) activity
						.findViewById(R.id.submit_question_button);
				button.setEnabled(false);
				semaphore.release();
			}
		});

		try {
			semaphore.acquire();
			getSolo().sleep(500);
			assertTrue("# Audit errors = " + getActivity().auditErrors()
					+ " != 1", getActivity().auditErrors() == 1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				EditQuestionActivity activity = (EditQuestionActivity) getActivity();
				Button button = (Button) activity
						.findViewById(R.id.submit_question_button);
				button.setText("Not submit");
				semaphore.release();
			}
		});

		try {
			semaphore.acquire();
			getSolo().sleep(500);
			assertTrue("# Audit errors = " + getActivity().auditErrors()
					+ " != 2", getActivity().auditErrors() == 2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void testBlankTag() {
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"answer D");
		getSolo().clickOnButton("+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "my question1");

		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"answer BBBBBB");
		getSolo().clickOnButton("" + (char) 10008);
		waitFor(TTChecks.QUESTION_EDITED);

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				EditQuestionActivity activity = (EditQuestionActivity) getActivity();
				Button button = (Button) activity
						.findViewById(R.id.submit_question_button);
				button.setEnabled(true);
				QuizQuestion question = activity.createQuestionFromGui();
				assertEquals("Question: my question1",
						question.getQuestionContent());
				assertTrue("Audit questions == 0", question.auditErrors() == 1);
				assertTrue("Number of audit errors = "
						+ getActivity().auditErrors() + " != 1", getActivity()
						.auditSubmitButton() == 1);
			}
		});
	}
}
