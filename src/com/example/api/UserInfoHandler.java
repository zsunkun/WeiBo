package com.example.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.db.UserHandler;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class UserInfoHandler {

	public static User updateUserInfoFromJson(Context context, String arg0) {
		try {
			JSONObject user_json = new JSONObject(arg0);
			User user = new User();
			user.setUser_id(user_json.getString("idstr"));
			user.setUser_name(user_json.getString("screen_name"));
			user.setUser_gender(user_json.getString("gender"));
			user.setDescription(user_json.getString("description"));
			user.setStatuses_count(user_json.getInt("statuses_count"));
			user.setFollowers_count(user_json.getInt("followers_count"));
			user.setFriends_count(user_json.getInt("friends_count"));

			// 根据json返回的url得到用户头像
			URL url = new URL(user_json.getString("profile_image_url"));

			HttpURLConnection httpconn = (HttpURLConnection) url
					.openConnection();
			InputStream is = httpconn.getInputStream();
			Drawable user_head = Drawable.createFromStream(is, "");

			user.setUser_head(user_head);
			UserHandler userxx = new UserHandler(context);
			userxx.insertUser(user);

			return user;
		} catch (MalformedURLException e) {
		} catch (JSONException e) {
		} catch (IOException e) {
		}
		return null;
	}
}
