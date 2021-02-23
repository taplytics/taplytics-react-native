| #   | Platform                                    |
| --- | ------------------------------------------- | --- |
| 1   | [iOS Push Notification Setup](#ios)         |     |
| 2   | [Android Push Notification Setup](#android) |

## Setting up Push Notifications

Due to the nature of push notifications and their dependencies on the specific platforms, setup for each individual platform must be performed natively.

### Push Event Listeners

It's possible to attach listeners to the three push notification events handled by the Taplytics SDK. These are push received, push dismissed and push opened. Use the following three methods:

```javascript
Taplytics.registerPushOpenedListener((value) => {
  // Do something
})
Taplytics.registerPushReceivedListener((value) => {
  // Do something
})
//Only available on android
Taplytics.registerPushDismissedListener((value) => {
  // Do something
})
```

You can register multiple callbacks for the same event, and all will be executed.

Before these event listeners trigger, push notifications must be set up fully on each respective platform.

## iOS

To register for push notifications, it is possible to do so by calling:

```
// iOS available only
Taplytics.registerPushNotifications()
```

To set up push notifications on iOS natively, please follow the documentation here:

#### [iOS SDK - Push Notifications](https://taplytics.com/docs/ios-sdk/push-notifications)

#### Other Push Event Listeners

On iOS, its also possible to use the built-in React Native push notification module, which can be found [here](https://facebook.github.io/react-native/docs/pushnotificationios.html)

## Android

If you wish to use Push Notifications on Taplytics, you must add the following permissions (replace `com.yourpackagename` with your app's package name) to your Android Manifest:

```xml
<uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
<permission android:name="com.yourpackagename.permission.C2D_MESSAGE"/>
<uses-permission android:name="com.yourpackagename.permission.C2D_MESSAGE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

And you must add the following receiver and service under your application tag:

```xml
<receiver
    android:name="com.taplytics.react.TRNBroadcastReceiver"
    android:permission="com.google.android.c2dm.permission.SEND" >
    <intent-filter>
        <action android:name="com.google.android.c2dm.intent.RECEIVE" />
    </intent-filter>

    <intent-filter>
         <action android:name="taplytics.push.OPEN" />
         <action android:name="taplytics.push.DISMISS" />
    </intent-filter>
</receiver>
<service android:name="com.taplytics.sdk.TLGcmIntentService" />
```

Then add the following to your build.gradle:

```
compile("com.google.android.gms:play-services-gcm:9.+")
```

### Rich Push, Advanced Usage, and more Android Push Features

To see more push features available to android, please view the Android push documentation.

#### [Android SDK - Push Notifications](https://taplytics.com/docs/android-sdk/push-notifications)
