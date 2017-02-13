
|#  |Platform                                                                       |
|---|---                                                                        |
|1  | [iOS Push Notification Setup](#ios)           |                                 |
|2  | [Android Push Notification Setup](#android)           |

## Setting up Push Notifications

Due to the nature of push notifications and their dependencies on the specific platforms, setup for each individual platform must be performed natively.

## iOS

To set up push notifications on iOS, please follow the documentation here:

#### [iOS SDK - Push Notifications](https://taplytics.com/docs/ios-sdk/push-notifications)

### Push Event Listeners

On iOS, use the built-in React Native push notification module, which can be found [here](https://facebook.github.io/react-native/docs/pushnotificationios.html)

## Android

To set up push notifications on Android, please follow the documentation here:

#### [Android SDK - Push Notifications](https://taplytics.com/docs/android-sdk/push-notifications)

### Push Event Listeners

On Android, it's possible to attach listeners to the three push notification events handled by the Taplytics SDK. These are push received, push dismissed and push opened. Use the following three methods:
```javascript
Taplytics.registerPushOpenedListener(callback)
Taplytics.registerPushReceivedListener(callback)
Taplytics.registerPushDismissedListener(callback)
```
You can register multiple callbacks for the same event, and all will be executed.
IMPORTANT: For this functionality to work, you must register the Broadcast receiver in your Android manifest, inside the application tag:
```xml
<receiver 
  android:name="com.your.package.name.here"
    android:permission="com.google.android.c2dm.permission.SEND">
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
