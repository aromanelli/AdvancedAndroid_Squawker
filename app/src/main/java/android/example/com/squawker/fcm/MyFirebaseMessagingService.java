package android.example.com.squawker.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * https://youtu.be/dXlfzLQUqaU
 * https://medium.com/android-school/firebaseinstanceidservice-is-deprecated-50651f17a148
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    // TODO (1) Make a new package for your FCM service classes called "fcm"

    // TODO (2) Create a new Service class that extends FirebaseInstanceIdService.
    //   You'll need to implement the onTokenRefresh method.
    //   Simply have it print out the new token.

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

    // TODO (4) Get the message from that token and print it in a log statement
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(LOG_TAG, "onMessageReceived() called with: remoteMessage = [" + remoteMessage + "]");
        super.onMessageReceived(remoteMessage);
    }

}
