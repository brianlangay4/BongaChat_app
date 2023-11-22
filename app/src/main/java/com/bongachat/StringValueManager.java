package com.bongachat;

import android.content.Context;
import android.content.SharedPreferences;

public class StringValueManager {

    private static final String SHARED_PREFS_KEY = "string_value_prefs";
    private static final String STRING_VALUE_KEY = "current_string_value";

    private Context context;
    private String cachedValue;

    public StringValueManager(Context context) {
        this.context = context;
        cachedValue = retrieveCachedValue();
    }

    public void setValue(String newValue) {
        // Update the cached value and store it in SharedPreferences
        cachedValue = newValue;
        saveCachedValue(newValue);
    }

    public String getValue() {
        if (cachedValue == null) {
            // If cached value is not available, retrieve from SharedPreferences
            cachedValue = retrieveCachedValue();
        }
        return cachedValue;
    }

    private void saveCachedValue(String value) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(STRING_VALUE_KEY, value);
        editor.apply();
    }

    private String retrieveCachedValue() {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        return prefs.getString(STRING_VALUE_KEY, null);
    }
}
