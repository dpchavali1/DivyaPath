package com.divyapath.app.utils;
import android.content.Context; import android.content.Intent;
public class ShareHelper {
    public static void shareText(Context context, String title, String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, content + "\n\nDownload DivyaPath for more devotional content!\nhttps://play.google.com/store/apps/details?id=com.divyapath.app");
        context.startActivity(Intent.createChooser(intent, "Share via"));
    }
    public static void shareShloka(Context context, String sanskrit, String meaning, String source) {
        String text = sanskrit + "\n\n" + meaning + "\n\n— " + source + "\n\nShared via DivyaPath App";
        shareText(context, "Shloka of the Day", text);
    }
    public static void shareAarti(Context context, String title, String content) {
        String text = title + "\n\n" + content;
        shareText(context, title, text);
    }
    public static void shareApp(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "DivyaPath - Devotional App");
        intent.putExtra(Intent.EXTRA_TEXT, "Download DivyaPath for aarti, chalisa, mantra, panchang and more!\nhttps://play.google.com/store/apps/details?id=com.divyapath.app");
        context.startActivity(Intent.createChooser(intent, "Share App"));
    }
}
