package com.example.hire_me_test;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class UserSession {

    private static final String PREFS_NAME = "user_session_prefs";
    private static final String KEY_USER_ID = "user_id";

    public static String getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String userId = prefs.getString(KEY_USER_ID, null);
        if (userId == null) {
            userId = UUID.randomUUID().toString();
            prefs.edit().putString(KEY_USER_ID, userId).apply();
        }
        return userId;
    }
}
