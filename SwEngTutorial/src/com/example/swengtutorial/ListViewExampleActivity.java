package com.example.swengtutorial;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class ListViewExampleActivity extends Activity {

	private ListQuestionAdapter mAdapter;
	private ListView mListview;
	
	public void addMoreElements(View view) {
		mAdapter.add("");
		mAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listviewexampleactivity);

		mListview = (ListView) findViewById(R.id.listview);
		mAdapter = new ListQuestionAdapter(this);

		mListview.setAdapter(mAdapter);
	}
}