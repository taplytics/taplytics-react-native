# Taplytics React Native SDK

_Taplytics is a native mobile A/B testing and push notification platform that helps you optimize your React Native app!_

[Commercial License / Terms](http://taplytics.com/terms)

### **Current Version: [2.1.3](#changelog)**

## Documentation

Please see the [SDK documentaiton](https://docs.taplytics.com/docs/react-native-sdk) for full details.

- [Getting Started](https://docs.taplytics.com/docs/react-native-sdk#getting-started)
- [Experiments](https://docs.taplytics.com/docs/react-native-sdk#getting-started)
- [Feature Flags](https://docs.taplytics.com/docs/react-native-sdk)
- [Push Notifications](https://docs.taplytics.com/docs/react-native-sdk#getting-started)

## Changelog

**[2.1.3](https://github.com/taplytics/Taplytics-React-Native/releases/tag/2.1.3)**

1. Fixed `newAsyncVariable` crashing the app when a new session is created on iOS

**[2.1.2](https://github.com/taplytics/Taplytics-React-Native/releases/tag/2.1.2)**

1. Fixed `propertiesLoadedCallback` for Android

**[2.1.1](https://github.com/taplytics/Taplytics-React-Native/releases/tag/2.1.1)**

1. Fixed `propertiesLoadedCallback` crashing the app when called more than once

**[2.1.0](https://github.com/taplytics/Taplytics-React-Native/releases/tag/2.1.0)**

1. Added support for React Native 0.60.0 and autolinking

**[1.4.1](https://github.com/taplytics/Taplytics-React-Native/releases/tag/1.4.1)**

1. Added types

**[1.4.0](https://github.com/taplytics/Taplytics-React-Native/releases/tag/1.4.0)**

1. Added call `registerPushNotifications` for iOS

**[1.3.0](https://github.com/taplytics/Taplytics-React-Native/releases/tag/1.3.0)**

1. Changed `setUserAttributes` to return promise.

**[1.2.0](https://github.com/taplytics/Taplytics-React-Native/releases/tag/1.2.0)**

1. Added getRunningFeatureFlags and featureFlagEnabled methods.

**[1.1.11](https://github.com/taplytics/Taplytics-React-Native/releases/tag/1.1.11)**

1. Add push open listener compatability for cold open after force close on Android.

**[1.1.10](https://github.com/taplytics/Taplytics-React-Native/releases/tag/1.1.10)**

1. Push listener backwards compatibility for react native `<0.50.0`

**[1.1.8](https://github.com/taplytics/Taplytics-React-Native/releases/tag/1.1.8)**

1. Remove erroneous log

**[1.1.7](https://github.com/taplytics/Taplytics-React-Native/releases/tag/1.1.7)**

1. Fix registering for push on iOS

**[1.1.5](https://github.com/taplytics/Taplytics-React-Native/releases/tag/1.1.5)**

1. Allow for push notification hooks in iOS

**[1.1.4](https://github.com/taplytics/Taplytics-React-Native/releases/tag/1.1.4)**

1. Add error states for various async calls

**[1.1.3](https://github.com/taplytics/Taplytics-React-Native/releases/tag/1.1.3)**

1. RN 0.47+ Support

**[1.0.12](https://github.com/taplytics/Taplytics-React-Native/releases/tag/1.0.12)**

1. Update reference to iOS push register method.

**[1.0.11](https://github.com/taplytics/Taplytics-React-Native/releases/tag/1.0.11)**

1. Fixed json attributes passed into logEvent and logRevenue causing crash.

**[1.0.10](https://github.com/taplytics/Taplytics-React-Native/releases/tag/1.0.10)**

1. Fixed search paths for non-cocoapods import

**[1.0.9](https://github.com/taplytics/Taplytics-React-Native/releases/tag/1.0.9)**

1. Fixed getSessionInfo crash

**[1.0.8](https://github.com/taplytics/Taplytics-React-Native/releases/tag/1.0.8)**

1. Added push ID to value sent to BroadcastReceiver

**[1.0.7](https://github.com/taplytics/Taplytics-React-Native/releases/tag/1.0.7)**

1. Added safety in broadcastReceiver

**[1.0.6](https://github.com/taplytics/Taplytics-React-Native/releases/tag/1.0.6)**

1. Fixed broadcastreceiver sending wrong push event types.

**[1.0.5](https://github.com/taplytics/Taplytics-React-Native/releases/tag/1.0.5)**

1. Updated android package name from com.react to com.taplytics.react

**[1.0.4](https://github.com/taplytics/Taplytics-React-Native/releases/tag/1.0.4)**

1. Updated search paths

**[1.0.3](https://github.com/taplytics/Taplytics-React-Native/releases/tag/1.0.3)**

1. Fixed npm references not pointing to the right project.
2. Cleaned up many iOS functions.

**[1.0.1](https://github.com/taplytics/Taplytics-React-Native/releases/tag/1.0.1)**

1. Fixed react native minimum version number.

**[1.0.0](https://github.com/taplytics/Taplytics-React-Native/releases/tag/1.0.0)**

1. Initial version of React Native SDK. Please contact us with any questions or use git issues.
