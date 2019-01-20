package com.example.eliad.drive4u.notifications;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.eliad.drive4u.chat.ChatMessageActivity;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.Teacher;
import com.example.eliad.drive4u.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.example.eliad.drive4u.chat.ChatMessageActivity.ARG_CURRENT_USER;
import static com.example.eliad.drive4u.chat.ChatMessageActivity.ARG_SECOND_USER;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessaging.class.getName();

    private User currUser;
    private User secUser;


    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        Log.d(TAG,"onMessageReceived");
        super.onMessageReceived(remoteMessage);

        String sented = remoteMessage.getData().get("sented");

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (firebaseUser != null && sented.equals(firebaseUser.getUid())) {
            Log.d(TAG,"sendNotification");
            final String secUserId = remoteMessage.getData().get("user");
            Log.d(TAG,"user " + secUserId);
            final String icon = remoteMessage.getData().get("icon");
            final String title = remoteMessage.getData().get("title");
            Log.d(TAG,"Title " + title);
            final String body = remoteMessage.getData().get("body");
            Log.d(TAG, "Body " + body);

            RemoteMessage.Notification notification = remoteMessage.getNotification();
            final int i = Integer.parseInt(secUserId.replaceAll("[\\D]", ""));
            final Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


            db.collection("Teachers")
                    .document(firebaseUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();
                                if (snapshot != null && snapshot.exists()) {
                                    currUser = (User) snapshot.toObject(Teacher.class);

                                    db.collection("Students")
                                            .document(secUserId)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot snapshot = task.getResult();
                                                        if (snapshot != null && snapshot.exists()) {
                                                            secUser = (User) snapshot.toObject(Student.class);

                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                sendOreoNotification(i, icon, title,
                                                                body, defaultSound);
                                                            } else {
                                                                sendNotification(i, icon, title,
                                                                        body, defaultSound);
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    });

            db.collection("Students")
                    .document(firebaseUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();
                                if (snapshot != null && snapshot.exists()) {
                                    currUser = (User) snapshot.toObject(Student.class);

                                    db.collection("Students")
                                            .document(secUserId)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot snapshot = task.getResult();
                                                        if (snapshot != null && snapshot.exists()) {
                                                            secUser = (User) snapshot.toObject(Student.class);

                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                sendOreoNotification(i, icon, title,
                                                                        body, defaultSound);
                                                            } else {
                                                                sendNotification(i, icon, title,
                                                                        body, defaultSound);
                                                            }
                                                        }
                                                    }
                                                }
                                            });

                                    db.collection("Teachers")
                                            .document(secUserId)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot snapshot = task.getResult();
                                                        if (snapshot != null && snapshot.exists()) {
                                                            secUser = (User) snapshot.toObject(Teacher.class);

                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                sendOreoNotification(i, icon, title,
                                                                        body, defaultSound);
                                                            } else {
                                                                sendNotification(i, icon, title,
                                                                        body, defaultSound);
                                                            }
                                                        }
                                                    }
                                                }
                                            });

                                }
                            }
                        }
                    });


        }
    }

    private void sendOreoNotification(int i, String icon,
                                      String title, String body, Uri defaultSound) {
        Log.d(TAG, "sendOreoNotification");

        Intent intent = new Intent(this, ChatMessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_CURRENT_USER, currUser);
        bundle.putParcelable(ARG_SECOND_USER, secUser);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title, body,
                pendingIntent, defaultSound, icon);

        int k = (i > 0) ? i : 0;

        oreoNotification.getManager().notify(k, builder.build());

    }

    private void sendNotification(int i, String icon,
                                  String title, String body, Uri defaultSound) {
        Log.d(TAG,"sendNotification");

        Intent intent = new Intent(this, ChatMessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_CURRENT_USER, currUser);
        bundle.putParcelable(ARG_SECOND_USER, secUser);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int k = (i > 0) ? i : 0;

        notificationManager.notify(k, builder.build());

    }
}

