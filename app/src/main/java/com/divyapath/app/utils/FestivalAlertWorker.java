package com.divyapath.app.utils;
import android.app.NotificationManager; import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull; import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker; import androidx.work.WorkerParameters;
import com.divyapath.app.DivyaPathApp; import com.divyapath.app.R;
import com.divyapath.app.data.local.DivyaPathDatabase; import com.divyapath.app.data.local.entity.FestivalEntity;
import java.text.SimpleDateFormat; import java.util.Calendar; import java.util.Locale;
public class FestivalAlertWorker extends Worker {
    public FestivalAlertWorker(@NonNull Context c,@NonNull WorkerParameters p){super(c,p);}
    @NonNull @Override public Result doWork(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return Result.success();
        }
        Calendar tomorrow=Calendar.getInstance();tomorrow.add(Calendar.DAY_OF_YEAR,1);
        String tomorrowDate=new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(tomorrow.getTime());
        try{
            DivyaPathDatabase db=DivyaPathDatabase.getDatabase(getApplicationContext());
            // Use a direct query since we're in a worker thread
            FestivalEntity festival=db.festivalDao().getFestivalByDateSync(tomorrowDate);
            if(festival!=null){
                NotificationCompat.Builder b=new NotificationCompat.Builder(getApplicationContext(),DivyaPathApp.CHANNEL_FESTIVAL)
                        .setSmallIcon(R.drawable.ic_om_symbol).setContentTitle("Tomorrow: "+festival.getName()+" \uD83C\uDF1F")
                        .setContentText(festival.getDescription()).setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(festival.getDescription()))
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
                NotificationManager nm=getApplicationContext().getSystemService(NotificationManager.class);
                if(nm!=null)nm.notify(2001,b.build());
            }
        }catch(Exception e){/* DB not ready */}
        return Result.success();
    }
}
