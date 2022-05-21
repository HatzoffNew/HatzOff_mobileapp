package com.aliyahatzoff.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class Sharedhelper {
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    public static void putKey(Context context, String Key, String Value) {
        sharedPreferences = context.getSharedPreferences("Hatzoff", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(Key, Value);
        editor.commit();

    }
    public static String getKey(Context contextGetKey, String Key) {
        sharedPreferences = contextGetKey.getSharedPreferences("Hatzoff", Context.MODE_PRIVATE);
        return sharedPreferences.getString(Key, "");

    }

    public static void deleteKey(Context contextGetKey, String Key) {
        sharedPreferences = contextGetKey.getSharedPreferences("Hatzoff", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.remove(Key);
        editor.commit();

    }




    public static Map<String, ?> getAll(Context context) {
        sharedPreferences = context.getSharedPreferences("Hatzoff", Context.MODE_PRIVATE);
        return sharedPreferences.getAll();
    }

    public static void clearSharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("Hatzoff", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
    }




    }
