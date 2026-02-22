package com.divyapath.app.utils;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Maps exceptions to user-friendly messages in Hindi and English.
 */
public class ErrorHandler {

    public static String getMessage(Throwable t) {
        if (t instanceof UnknownHostException) {
            return "Internet connection nahi hai. Kripya check karein.\nNo internet connection. Please check.";
        } else if (t instanceof SocketTimeoutException) {
            return "Server se response nahi mila. Dubara try karein.\nServer timeout. Please try again.";
        } else if (t instanceof IOException) {
            return "Network error. Kripya dubara try karein.\nNetwork error. Please try again.";
        } else if (t instanceof SecurityException) {
            return "Permission denied. App settings mein permission dijiye.\nPermission denied. Grant permission in settings.";
        } else if (t instanceof OutOfMemoryError) {
            return "Memory full. Kuch apps band karein.\nOut of memory. Close some apps.";
        } else if (t instanceof IllegalStateException) {
            return "Kuch galat ho gaya. App restart karein.\nSomething went wrong. Restart the app.";
        } else {
            return "Kuch galat ho gaya. Dubara try karein.\nSomething went wrong. Please try again.";
        }
    }

    public static void showError(Context context, Throwable t) {
        Toast.makeText(context, getMessage(t), Toast.LENGTH_LONG).show();
    }

    public static String getShortMessage(Throwable t) {
        if (t instanceof UnknownHostException) {
            return "No internet";
        } else if (t instanceof SocketTimeoutException) {
            return "Timeout";
        } else if (t instanceof IOException) {
            return "Network error";
        } else {
            return "Error occurred";
        }
    }
}
