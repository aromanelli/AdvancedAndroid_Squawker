package android.example.com.squawker.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.example.com.squawker.MainActivity;
import android.example.com.squawker.R;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkProvider;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TimeUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * https://youtu.be/dXlfzLQUqaU
 * https://medium.com/android-school/firebaseinstanceidservice-is-deprecated-50651f17a148
 * https://squawkerfcmserver.udacity.com/
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    // https://squawkerfcmserver.udacity.com/

    // TODO (1) Make a new Service in the fcm package that extends from FirebaseMessagingService.

    final static private String LOG_TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onNewToken(String newToken) {
        /* https://firebase.google.com/docs/cloud-messaging/android/client
        The registration token may change when:
            * The app deletes Instance ID
            * The app is restored on a new device
            * The user uninstalls/reinstall the app
            * The user clears app data.
         */
        super.onNewToken(newToken);
        Log.d(LOG_TAG, "New Token --> ["+ newToken +"]");
    }

    // TODO (2) As part of the new Service - Override onMessageReceived. This method will
    // be triggered whenever a squawk is received. You can get the data from the squawk
    // message using getData(). When you send a test message, this data will include the
    // following key/value pairs:
    // test: true
    // author: Ex. "TestAccount"
    // authorKey: Ex. "key_test"
    // message: Ex. "Hello world"
    // date: Ex. 1484358455343
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(LOG_TAG, "onMessageReceived() called with: remoteMessage = [" + remoteMessage + "]");
        super.onMessageReceived(remoteMessage);

        // TODO (3) As part of the new Service - If there is message data,
        // get the data using the keys and do two things with it :
        // 1. Display a notification with the first 30 character of the message
        // 2. Use the content provider to insert a new message into the local database
        // Hint: You shouldn't be doing content provider operations on the main thread.
        // If you don't know how to make notifications or interact with a content provider
        // look at the notes in the classroom for help.

        Map<String, String> mapData = remoteMessage.getData();
        if (mapData.size() >= 1) {
            sendNotification(mapData);
            putSquawkInDB(mapData);
        }

    }

    private void sendNotification(Map<String, String> mapData) {

        String message = mapData.get(SquawkContract.COLUMN_MESSAGE);
        // TODO (4) Get the message from that token and print it in a log statement
        Log.d(LOG_TAG, "Notification Message (full): " + message);

        // If the message is longer than the max number of characters we want in our
        // notification, truncate it and add the unicode character for ellipsis
        final int maxChars = 30;
        if (message.length() > maxChars) {
            message = message.substring(0, maxChars) + "\u2026";
            Log.d(LOG_TAG, "Notification Message (truncated): " + message);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        /*
        For when AS complains that getSystemService(...) can return a null ...
        https://developer.android.com/reference/android/content/ContextWrapper.html#getSystemService(java.lang.String)
        Note: Instant apps, for which PackageManager.isInstantApp() returns true, don't
        have access to the following system services: DEVICE_POLICY_SERVICE, FINGERPRINT_SERVICE,
        SHORTCUT_SERVICE, USB_SERVICE, WALLPAPER_SERVICE, WIFI_P2P_SERVICE, WIFI_SERVICE, WIFI_AWARE_SERVICE.
        For these services this method will return null. Generally, if you are running as an instant
        app you should always check whether the result of this method is null.
         */

        final String notifyChannelId = "msg_notify_channel";
        // Create a notification channel for Android O devices ..
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    notifyChannelId,
                    this.getString(R.string.app_name), // TODO AOR Use better name!
                    NotificationManager.IMPORTANCE_HIGH
            );
            //noinspection ConstantConditions
            notificationManager.createNotificationChannel(channel);
        }

        // Intent to wrap in a PendingIntent ...
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, notifyChannelId)
                .setSmallIcon(R.drawable.ic_duck)
                .setContentTitle(
                        String.format(
                                getString(R.string.notification_message),
                                mapData.get(SquawkContract.COLUMN_AUTHOR)
                        ))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(
                        PendingIntent.getActivity(
                                this,
                                0 /* Request code */,
                                intent,
                                PendingIntent.FLAG_ONE_SHOT
                        )
                );

        //noinspection ConstantConditions
        notificationManager.notify(
                getNotifyId(), // 0 /* ID of notification */,
                notificationBuilder.build());
    }

    private int notifyId = 0;
    synchronized private int getNotifyId() {
        if ((notifyId + 1) > Integer.MAX_VALUE) {
            notifyId = 0;
        }
        return ++notifyId;
    }

    private void putSquawkInDB(final Map<String, String> mapData) {
        AsyncTask<Void, Void, Void> insertSquawkTask = new SquawkAsyncTask(getContentResolver(), mapData);
        insertSquawkTask.execute();
    }

    static private class SquawkAsyncTask extends AsyncTask<Void, Void, Void> {
        private final ContentResolver resolver;
        private final Map<String, String> mapData;
        private SquawkAsyncTask(final ContentResolver resolver, final Map<String, String> mapData) {
            this.resolver = resolver;
            this.mapData = mapData;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            ContentValues newMessage = new ContentValues();
            newMessage.put(SquawkContract.COLUMN_AUTHOR, mapData.get(SquawkContract.COLUMN_AUTHOR));
            newMessage.put(SquawkContract.COLUMN_MESSAGE, mapData.get(SquawkContract.COLUMN_MESSAGE).trim());
            newMessage.put(SquawkContract.COLUMN_DATE, mapData.get(SquawkContract.COLUMN_DATE));
            newMessage.put(SquawkContract.COLUMN_AUTHOR_KEY, mapData.get(SquawkContract.COLUMN_AUTHOR_KEY));
            resolver.insert(SquawkProvider.SquawkMessages.CONTENT_URI, newMessage);
            return null;
        }
    }

}
