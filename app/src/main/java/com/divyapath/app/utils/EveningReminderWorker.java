package com.divyapath.app.utils;
import android.app.NotificationManager; import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull; import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker; import androidx.work.WorkerParameters;
import com.divyapath.app.DivyaPathApp; import com.divyapath.app.R;
public class EveningReminderWorker extends Worker {
    public EveningReminderWorker(@NonNull Context c,@NonNull WorkerParameters p){super(c,p);}
    @NonNull @Override public Result doWork(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return Result.success();
        }
        NotificationCompat.Builder b=new NotificationCompat.Builder(getApplicationContext(),DivyaPathApp.CHANNEL_DAILY_REMINDER)
                .setSmallIcon(R.drawable.ic_om_symbol).setContentTitle("Sandhya Aarti Ka Samay \uD83D\uDD6F\uFE0F")
                .setContentText("Sandhya aarti karke apna din pavitra karein").setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager nm=getApplicationContext().getSystemService(NotificationManager.class);
        if(nm!=null)nm.notify(1002,b.build());
        return Result.success();
    }
}
