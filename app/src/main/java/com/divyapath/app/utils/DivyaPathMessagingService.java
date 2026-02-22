package com.divyapath.app.utils;
import androidx.annotation.NonNull; import com.google.firebase.messaging.FirebaseMessagingService; import com.google.firebase.messaging.RemoteMessage;
public class DivyaPathMessagingService extends FirebaseMessagingService {
    @Override public void onMessageReceived(@NonNull RemoteMessage msg){
        String title="DivyaPath",body="";
        if(msg.getNotification()!=null){title=msg.getNotification().getTitle();body=msg.getNotification().getBody();}
        if(msg.getData().size()>0&&"festival".equals(msg.getData().get("type")))NotificationHelper.showFestivalAlert(this,title,body);
        else NotificationHelper.showDailyReminder(this,title,body);
    }
    @Override public void onNewToken(@NonNull String token){super.onNewToken(token);}
}
