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
	final private Semaphore semaphore = new Semaphore(0);

	public AuditTest() {
		super(EditQuestionActivity.class);
	}

	@Override
	protected void setUp() {
		super.setUp();
		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
		// add stuff we need
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		semaphore.release();
	}

	public void testInvalidQuestionNoCorrectAnswerDisabledButton() {

		getSolo().sleep(500);
		assertTrue("Audit fresh Activity is not zero", getActivity()
				.auditErrors() == 0);

		getSolo().clickOnButton("\\+");
		waitFor(TTChecks.QUESTION_EDITED);

		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "My question");
		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag");
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"Answer 1");
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"Answer 2");

		assertTrue("Audit invalid question is not zero", getActivity()
				.auditErrors() == 0);

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
					+ " != 0", getActivity().auditErrors() == 0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void testMinimalValidQuestionDisabledButton() {

		getSolo().sleep(500);
		assertTrue("Audit fresh Activity is not zero", getActivity()
				.auditErrors() == 0);

		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "My question");
		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag");
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"Answer 1");
		getSolo().clickOnButton("" + (char) 10008);
		waitFor(TTChecks.QUESTION_EDITED);

		getSolo().clickOnButton("\\+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"Answer 2");

		assertTrue("Audit minimal valid question is not zero", getActivity()
				.auditErrors() == 0);

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
	}

	public void testAudit() {

		getSolo().sleep(500);
		assertTrue("Audit fresh Activity is not zero but "
				+ getActivity().auditErrors(), getActivity().auditErrors() == 0);

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

	public void testNoTag() {
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
		final EditQuestionActivity activity = (EditQuestionActivity) getActivity();
		QuizQuestion question = activity.createQuestionFromGui();

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Button button = (Button) activity
						.findViewById(R.id.submit_question_button);
				button.setEnabled(true);
				semaphore.release();
			}
		});

		try {
			semaphore.acquire();
			getSolo().sleep(500);
			assertEquals("my question1",
					question.getStatement());
			assertTrue("Audit questions != 1", question.auditErrors() == 1);
			assertTrue("Number of audit errors = "
					+ getActivity().auditErrors() + " != 1", getActivity()
					.auditErrors() == 1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void testNoAnswers() {

		getSolo().clickOnButton("+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "my question1");

		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag");
		getSolo().clickOnButton("" + (char) 10008);
		waitFor(TTChecks.QUESTION_EDITED);

		final EditQuestionActivity activity = (EditQuestionActivity) getActivity();
		QuizQuestion question = activity.createQuestionFromGui();

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Button button = (Button) activity
						.findViewById(R.id.submit_question_button);
				button.setEnabled(true);
				semaphore.release();
			}
		});

		try {
			semaphore.acquire();
			getSolo().sleep(500);
			assertEquals("my question1",
					question.getStatement());
			assertTrue("Audit questions != 2", question.auditErrors() == 2);
			assertTrue("Number of audit errors = "
					+ getActivity().auditErrors() + " != 1", getActivity()
					.auditErrors() == 1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

//	public void testTooManyTag() {
//		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
//				"answer D");
//		getSolo().clickOnButton("+");
//		waitFor(TTChecks.QUESTION_EDITED);
//		getSolo().enterText(
//				(EditText) getSolo().getText(
//						"Type in the question\'s text body"), "my question1");
//
//		getSolo().enterText(
//				(EditText) getSolo().getText("Type in the question\'s tags"),
//				"1, 2, 3, 4, 5, 6, 7, 8, 9, "
//						+ "10, 11, 12 ,13, 14, 15, 16, 17, 18, 19 ,20, 21");
//
//		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
//				"answer BBBBBB");
//		getSolo().clickOnButton("" + (char) 10008);
//		waitFor(TTChecks.QUESTION_EDITED);
//		final EditQuestionActivity activity = (EditQuestionActivity) getActivity();
//		QuizQuestion question = activity.createQuestionFromGui();
//
//		getActivity().runOnUiThread(new Runnable() {
//
//			@Override
//			public void run() {
//				Button button = (Button) activity
//						.findViewById(R.id.submit_question_button);
//				button.setEnabled(true);
//				semaphore.release();
//			}
//		});
//
//		try {
//			semaphore.acquire();
//			getSolo().sleep(500);
//			assertEquals("my question1",
//					question.getStatement());
//			assertTrue("Size of Tag Set != 21", question.getTags().size() == 21);
//			assertTrue("Audit questions != 1", question.auditErrors() == 1);
//			assertTrue("Number of audit errors = "
//					+ getActivity().auditErrors() + " != 1", getActivity()
//					.auditErrors() == 1);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}

	public void testBlankTagEmptyAnswers() {

		getSolo().sleep(500);
		getSolo().clickOnButton("\\+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "my question1");

		getSolo().clickOnButton("" + (char) 10008);
		waitFor(TTChecks.QUESTION_EDITED);
		final EditQuestionActivity activity = (EditQuestionActivity) getActivity();
		QuizQuestion question = activity.createQuestionFromGui();

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Button button = (Button) activity
						.findViewById(R.id.submit_question_button);
				button.setEnabled(true);
				semaphore.release();
			}
		});

		try {
			semaphore.acquire();
			getSolo().sleep(500);
			assertEquals("my question1",
					question.getStatement());
			assertTrue("Audit questions != 3", question.auditErrors() == 3);
			assertTrue("Number of audit errors = "
					+ getActivity().auditErrors() + " != 1", getActivity()
					.auditErrors() == 1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void testAuditButtonWithLabelError() {
		// question sentence
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "my question1");
		// question tag
		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag");

		getSolo().clickOnButton("\\+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"an1");
		getSolo().clickOnButton("\\+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"an2");
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"an3");

		getSolo().clickOnButton("" + (char) 10008);
		waitFor(TTChecks.QUESTION_EDITED);
		final EditQuestionActivity activity = (EditQuestionActivity) getActivity();

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Button button = (Button) activity
						.findViewById(R.id.submit_question_button);
				Button correctButton1 = (Button) activity
						.findViewById(R.id.submit_question_correct_switch);
				Button correctButton2 = getSolo().getButton("" + (char) 10008);
				Button addbuton = getSolo().getButton("+");
				Button remouvebuton = getSolo().getButton("-");

				button.setText("SSS");
				correctButton1.setText("O");
				correctButton2.setText("X");
				addbuton.setText("?");
				remouvebuton.setText("$");
				Button remouvebuton2 = getSolo().getButton("-");
				remouvebuton2.setText("U");
				Button remouvebuton3 = getSolo().getButton("-");
				remouvebuton3.setText("DO");
				Button correctButton3 = getSolo().getButton("" + (char) 10008);
				correctButton3.setText("**");

				semaphore.release();
			}
		});

		try {
			semaphore.acquire();
			getSolo().clickOnButton("**"); // resets the adapter
			getSolo().sleep(500);
			assertFalse("AuditButton errors: => " + activity.auditErrors(),
					activity.auditErrors() == 8);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void testAuditButtonWithoutErrors() {
		// question sentence
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "my question1");
		// question tag
		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag");

		getSolo().clickOnButton("\\+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"an1");
		getSolo().clickOnButton("\\+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"an2");
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"an3");

		getSolo().clickOnButton("" + (char) 10008);
		waitFor(TTChecks.QUESTION_EDITED);
		final EditQuestionActivity activity = (EditQuestionActivity) getActivity();

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {

				semaphore.release();
			}
		});

		try {
			semaphore.acquire();
			getSolo().sleep(500);

			assertTrue("AuditButton errors: => " + activity.auditErrors(),
					activity.auditErrors() == 0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void testIssueTestMinimalValidQuestionDisabledButton() {
		// // question sentence
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "my question1");
		// question tag
		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag");

		getSolo().clickOnButton("\\+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"an1");
		getSolo().clickOnButton("\\+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"an2");
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"an3");
		getSolo().clickOnButton("" + (char) 10008);
		getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		// HERE IS THE WRONG SET UP
		getSolo().clickOnButton("\\+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().sleep(3000);
		final EditQuestionActivity activity = (EditQuestionActivity) getActivity();

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// HERE WE FORCE THE SUBMIT BUTTON TO BE ENABLED
				getSolo().getButton("Submit").setEnabled(true);
				semaphore.release();
			}
		});

		try {
			semaphore.acquire();

			assertTrue("AuditButton is: " + activity.auditErrors()
					+ "but was expected 1", activity.auditErrors() == 1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void testIssuetestInvalidQuestionNoCorrectAnswerDisabledButton() {
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "my question1");
		// question tag
		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag");

		getSolo().clickOnButton("\\+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"an1");
		getSolo().clickOnButton("\\+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"an2");
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"an3");

		final EditQuestionActivity activity = (EditQuestionActivity) getActivity();

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// HERE WE FORCE THE SUBMIT BUTTON TO BE ENABLED
				getSolo().getButton("Submit").setEnabled(true);
				semaphore.release();
			}
		});

		try {
			semaphore.acquire();

			assertTrue("AuditButton is: " + activity.auditErrors()
					+ "but was expected 1", activity.auditErrors() == 1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}