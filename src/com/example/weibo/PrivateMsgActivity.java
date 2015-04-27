package com.example.weibo;

import org.json.JSONArray;

import android.app.Activity;

import com.example.adapter.MyAdapter;

public class PrivateMsgActivity extends BaseActivity {

	@Override
	protected void getData(boolean isFormoreData) {
	}

	@Override
	protected MyAdapter getAdapter(JSONArray weibo_array) {
		return null;
	}

	@Override
	protected Activity getActivity() {
		return this;
	}

	@Override
	protected String getJSONName() {
		return null;
	}

}
