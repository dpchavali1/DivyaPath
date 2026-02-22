package com.divyapath.app.utils;
import android.app.PendingIntent; import android.content.Context; import android.content.Intent;
import androidx.core.app.NotificationCompat; import androidx.core.app.NotificationManagerCompat;
import com.divyapath.app.DivyaPathApp; import com.divyapath.app.R; import com.divyapath.app.ui.splash.SplashActivity;
public class NotificationHelper {
    public static void showDailyReminder(Context ctx,String title,String msg){
        Intent i=new Intent(ctx,SplashActivity.class);i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi=PendingIntent.getActivity(ctx,0,i,PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder b=new NotificationCompat.Builder(ctx,DivyaPathApp.CHANNEL_DAILY_REMINDER).setSmallIcon(R.drawable.ic_om_symbol).setContentTitle(title).setContentText(msg).setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(pi).setAutoCancel(true);
        try{NotificationManagerCompat.from(ctx).notify(1001,b.build());}catch(SecurityException ignored){}
    }
    public static void showFestivalAlert(Context ctx,String name,String msg){
        Intent i=new Intent(ctx,SplashActivity.class);i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi=PendingIntent.getActivity(ctx,0,i,PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder b=new NotificationCompat.Builder(ctx,DivyaPathApp.CHANNEL_FESTIVAL).setSmallIcon(R.drawable.ic_om_symbol).setContentTitle(name).setContentText(msg).setPriority(NotificationCompat.PRIORITY_HIGH).setContentIntent(pi).setAutoCancel(true);
        try{NotificationManagerCompat.from(ctx).notify(2001,b.build());}catch(SecurityException ignored){}
    }
}
