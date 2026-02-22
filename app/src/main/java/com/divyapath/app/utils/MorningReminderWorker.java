package com.divyapath.app.utils;
import android.app.NotificationManager; import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull; import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker; import androidx.work.WorkerParameters;
import com.divyapath.app.DivyaPathApp; import com.divyapath.app.R;
import java.util.Calendar;
public class MorningReminderWorker extends Worker {
    private static final String[] DEITIES={"Surya Dev","Shiva","Hanuman","Ganesha","Lakshmi","Durga","Shani Dev"};
    public MorningReminderWorker(@NonNull Context c,@NonNull WorkerParameters p){super(c,p);}
    @NonNull @Override public Result doWork(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return Result.success();
        }
        int day=Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String deity=DEITIES[(day-1)%DEITIES.length];
        String sevaText=SevaData.getTodaysSeva().title;
        NotificationCompat.Builder b=new NotificationCompat.Builder(getApplicationContext(),DivyaPathApp.CHANNEL_DAILY_REMINDER)
                .setSmallIcon(R.drawable.ic_om_symbol).setContentTitle("Subh Prabhat! \uD83D\uDE4F")
                .setContentText("Aaj "+deity+" ki aarti karen | Seva: "+sevaText).setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager nm=getApplicationContext().getSystemService(NotificationManager.class);
        if(nm!=null)nm.notify(1001,b.build());
        return Result.success();
    }
}
