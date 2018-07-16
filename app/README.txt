Udacity Adv. Android Lesson 4.19 Firebase Cloud Messaging, Where to Go for More ...

https://classroom.udacity.com/nanodegrees/nd801/parts/443745fb-4ae4-4918-8ea1-6bf24e356c1d/modules/6cb81da9-d083-4721-a31b-4f435de9fedd/lessons/d5eedaa1-39aa-489b-8538-d1e7ae522a67/concepts/f915493f-22b1-419f-b04b-3aee12d12c0e
https://youtu.be/M417Lbfv_AY

FCM has documentation for both Android and setting up server code:

    The Android documentation is here
    https://firebase.google.com/docs/cloud-messaging/android/client

    Documentation about how messages are sent from FCM to client is here
    https://firebase.google.com/docs/cloud-messaging/concept-options

    Detailed information about setting up an FCM server like my Squawker server is here
    https://squawkerfcmserver.udacity.com/
    https://firebase.google.com/docs/cloud-messaging/server

You can check out
this blog post: https://firebase.googleblog.com/2016/08/sending-notifications-between-android.html
for an example of some Node.js code for setting up an FCM server.
The library that I used for the Squawker server is called fcm-node.
https://www.npmjs.com/package/fcm-node

Finally, as mentioned before, if you're interested in learning more about Android and Firebase,
consider taking Udacity's Firebase in a Weekend course for Android.
https://www.udacity.com/course/firebase-in-a-weekend-by-google-android--ud0352
The class is free and walks you through the creation of a real time chat app with user accounts,
photo sharing and more, using Firebase as a backend.
