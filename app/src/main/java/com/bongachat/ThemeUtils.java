package com.bongachat;

import android.content.Context;
import android.content.SharedPreferences;

public class ThemeUtils {

    public static String getSelectedTheme(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
        return sharedPrefs.getString("selectedTheme", "default");
    }
}
