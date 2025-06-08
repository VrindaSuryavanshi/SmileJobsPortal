package com.example.smilejobportal.javaClass;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.smilejobportal.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            showNotification(title, body);
        }
    }
    //curl -X POST http://127.0.0.1:5001/smilejobportal-8d8c2/us-central1/sendNotification \
//  -H "Content-Type: application/json" \
//  -d '{
//        "token": "fCw_FAgnSriaxQ3QnKLQkI:APA91bFBVhUo_qqDotS2x_JoIWw26H5hGx7Cy8dv0NHmrGGWXTL4IDWBBgUIpd_AqfFPesPzAIr-JNsYxwPd-zTCPF-qHbnYmxUPv8Uh7nGUlhEWBrYaoDI",
//        "title": "New Job Alert!",
//        "body": "A new job has been posted. Check it out now!"
//      }'
    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "job_updates")
                .setSmallIcon(R.drawable.bell)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("job_updates", "Job Updates", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        manager.notify(0, builder.build());
    }
}

