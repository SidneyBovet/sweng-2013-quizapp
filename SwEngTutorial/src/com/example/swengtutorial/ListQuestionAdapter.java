package com.example.swengtutorial;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

/**
 * Basic Adapter of a ListView, extending BaseAdapter and shit.
 * 
 * @author Melody Lucid
 *
 */
public class ListQuestionAdapter extends BaseAdapter {
	
	private ListViewExampleActivity mAssociatedActivity;
	private LayoutInflater mInflater;

	private List<String> mAnswersList;
	private int mCorrectAnswerIndex;
	
	public ListQuestionAdapter(ListViewExampleActivity activity) {
		super();
		this.mAssociatedActivity = activity;
		this.mAnswersList = new ArrayList<String>();

		/*
		 * String[] values = new String[] { "Android", "iPhone", "Blackberry",
		 * "WebOS", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2", "Ubuntu",
		 * "Windows7", "Max OS X", "Linux", "OS/2", "Ubuntu", "Windows7",
		 * "Max OS X", "Linux", "OS/2", "Android", "iPhone" };
		 */
		mAnswersList.add("Answer #1");
		mAnswersList.add("Answer #2");
		mAnswersList.add("Answer #3");
		mAnswersList.add("Answer #4");
		mCorrectAnswerIndex = 0;
		this.mInflater = LayoutInflater.from(this.mAssociatedActivity);
	}
	
	public void add(String oneMoreAnswer) {
		mAnswersList.add(oneMoreAnswer);
		notifyDataSetChanged();
	}
	
	@Override
	public void notifyDataSetChanged() {
		// notify submit button from Associated Activity
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mAnswersList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mAnswersList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		RelativeLayout relatLayout = (RelativeLayout) view;
		if (view == null) {
			relatLayout = (RelativeLayout) this.mInflater.inflate(R.layout.listviewrow, parent, false);
		}
		
		EditText answerField = (EditText) relatLayout.findViewById(R.id.submit_question_answer);
		answerField.setText(mAnswersList.get(position));
		
		Button switchCorrectButton = (Button) relatLayout.findViewById(R.id.submit_question_trueness);
		if (position == mCorrectAnswerIndex) {
			switchCorrectButton.setText(R.string.question_right_answer);
		} else {
			switchCorrectButton.setText(R.string.question_wrong_answer);
		}
		final int pos = position;
		switchCorrectButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (pos == mCorrectAnswerIndex) {
					return;
				}
				mCorrectAnswerIndex = pos;
				notifyDataSetChanged();
			}
		});
		
		Button removeButton = (Button) relatLayout.findViewById(R.id.submit_question_remove_answer);
		removeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mAnswersList.size() <= 2) {
					return;
				}
				mAnswersList.remove(pos);
				if (pos < mCorrectAnswerIndex) {
					mCorrectAnswerIndex--;
				}
				notifyDataSetChanged();
			}
		}
		);
		
		return relatLayout;
	}

}
