package com.example.eliad.drive4u.notifications;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    public static final String NEW_MESSAGE_TITLE = "New Message";
    public static final String NEW_STUDENT_TITLE = "New Student Request";
    private FirebaseFirestore db;
    private User currUser;
    private User secUser;


    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        Log.d(TAG,"onMessageReceived");
        super.onMessageReceived(remoteMessage);

        String receiverId = remoteMessage.getData().get("receiverId");
        String sourceId = remoteMessage.getData().get("sourceId");

        SharedPreferences preferences = getSharedPreferences(ChatMessageActivity.PREFS, MODE_PRIVATE);
        String currentUser = preferences.getString(ChatMessageActivity.CURRENT_USER, ChatMessageActivity.NO_CURRENT_USER);


        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (firebaseUser == null) {
            Log.d(TAG, "firebase sourceId is null");
            return;
        }

        if(!receiverId.equals(firebaseUser.getUid())) {
            Log.d(TAG, "This notification was not meant for this user");
            return;
        }

        if (currentUser.equals(sourceId)) {
            // in case A sent a message to B. if both are in chat activity there is no need to
            // display a notification for B.
            // when B enters the chat activity with A, he sets A in the shared memory. (and removes
            // him when he is done or paused.
            Log.d(TAG, "we don't display notifications from this sourceId");
            return;
        }

        Log.d(TAG,"sendNotification");
        final String secUserId = remoteMessage.getData().get("sourceId");
        Log.d(TAG,"sourceId " + secUserId);
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
                                if (title.equals(NEW_MESSAGE_TITLE)) {
                                    manageMessageNotification(i, icon, title, body, defaultSound,
                                            secUserId, "Students", Student.class);
                                }
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
                                if (title.equals(NEW_MESSAGE_TITLE)) {
                                    manageMessageNotification(i, icon, title, body, defaultSound,
                                            secUserId, "Students", Student.class);
                                    manageMessageNotification(i, icon, title, body, defaultSound,
                                            secUserId, "Teachers", Teacher.class);
                                }
                            }
                        }
                    }
                });

    }

    private void manageMessageNotification(final int i, final String icon, final String title, final String body,
                                            final Uri defaultSound, String secUserId,
                                            String collectionPath, final Class cls) {

        Log.d(TAG, "manageMessageNotification");
        db.collection(collectionPath)
                .document(secUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot != null && snapshot.exists()) {
                                secUser = (User) snapshot.toObject(cls);

                                Intent intent = new Intent(MyFirebaseMessaging.this, ChatMessageActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(ARG_CURRENT_USER, currUser);
                                bundle.putParcelable(ARG_SECOND_USER, secUser);
                                intent.putExtras(bundle);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    sendOreoNotification(i, icon, title,
                                            body, defaultSound, intent);
                                } else {
                                    sendNotification(i, icon, title,
                                            body, defaultSound, intent);
                                }
                            }
                        }
                    }
                });

    }

    private void sendOreoNotification(int i, String icon,
                                      String title, String body, Uri defaultSound, Intent intent) {
        Log.d(TAG, "sendOreoNotification");

        final PendingIntent pendingIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title, body,
                pendingIntent, defaultSound, icon);

        int k = (i > 0) ? i : 0;

        oreoNotification.getManager().notify(k, builder.build());

    }

    private void sendNotification(int i, String icon,
                                  String title, String body, Uri defaultSound, Intent intent) {
        Log.d(TAG,"sendNotification");

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

