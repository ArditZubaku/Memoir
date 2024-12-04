package com.zubaku.memoir.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class Helpers {

  // Display a toast message with application context
  public static void showToast(String message, Context context) {
    if (context == null || TextUtils.isEmpty(message)) return;
    Toast.makeText(context.getApplicationContext(), message.trim(), Toast.LENGTH_SHORT).show();
  }

  // Retrieve and trim text from a TextView
  public static String getStringValue(TextView textView) {
    if (textView == null || textView.getText() == null) return "";
    return textView.getText().toString().trim();
  }

  // Validate a variable number of String parameters
  public static boolean areParamsValid(String... params) {
    return params != null && Arrays.stream(params).noneMatch(TextUtils::isEmpty);
  }

  // Validate a variable number of TextView fields
  public static boolean areTextFieldsValid(TextView... fields) {
    if (fields == null) return false;
    return Arrays.stream(fields).map(Helpers::getStringValue).noneMatch(TextUtils::isEmpty);
  }

  // Build an error message to be shown from an exception
  @NonNull
  public static String buildErrorMessage(Exception exception) {
    if (exception != null && exception.getMessage() != null) {
      return String.join("\n", exception.getMessage().split("(?<=\\.)"));
    }
    return "An unknown error occurred.";
  }
}
