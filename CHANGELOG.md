# Changelog

## [3.1.0](https://github.com/taplytics/taplytics-react-native/compare/3.0.0-rc.5...3.1.0) (2023-01-11)

## [3.0.0-rc.0](https://github.com/taplytics/taplytics-react-native/compare/2.1.4...3.0.0-rc.0) (2021-04-23)

### Breaking Changes

- We've switched away from default exports and now export each method/hook individually for improved discoverability and autocompletion. Read more about migrating from `v2.x` to `v3.x` [here](https://docs.taplytics.com/docs/react-native-sdk#migrating-from-v2x-to-v3x).

### Features

- We've introduced hooks that can be utilized for easier usage of the core Taplytics React Native SDK. Check out the docs [here](https://docs.taplytics.com/docs/react-native-sdk#hooks).
- The core SDK methods were also completely re-written from the ground up natively in TypeScript.
- Added more comprehensive JSDocs.

## [2.1.4](https://github.com/taplytics/taplytics-react-native/compare/2.1.3...2.1.4)

- Fixed `newAsyncVariable` not returning a `number` variable on Android
- Fixed `newAsyncVariable` not returning a variable of `JSON` type on iOS.
- The callback function within `propertiesLoadedCallback` now provides a boolean type argument indicating if the properties were loaded on Android, similar to iOS
- `propertiesLoadedCallback` now returns a `subscriber` object. `subscriber.remove()` can be called to clean up the event listener when a component is unmounted.

## [2.1.3](https://github.com/taplytics/taplytics-react-native/compare/2.1.2...2.1.3)

- Fixed `newAsyncVariable` crashing the app when a new session is created on iOS

## [2.1.2](https://github.com/taplytics/taplytics-react-native/compare/2.1.1...2.1.2)

- Fixed `propertiesLoadedCallback` for Android

## [2.1.1](https://github.com/taplytics/taplytics-react-native/compare/2.1.0...2.1.1)

- Fixed `propertiesLoadedCallback` crashing the app when called more than once

## [2.1.0](https://github.com/taplytics/taplytics-react-native/compare/1.4.1...2.1.0)

- Added support for React Native 0.60.0 and autolinking

## [1.4.1](https://github.com/taplytics/taplytics-react-native/compare/1.4.0...1.4.1)

- Added types

## [1.4.0](https://github.com/taplytics/taplytics-react-native/compare/1.3.0...1.4.1)

- Added call `registerPushNotifications` for iOS

## [1.3.0](https://github.com/taplytics/taplytics-react-native/compare/1.2.0...1.3.0)

- Changed `setUserAttributes` to return promise.

## [1.2.0](https://github.com/taplytics/taplytics-react-native/compare/1.1.11...1.2.0)

- Added getRunningFeatureFlags and featureFlagEnabled methods.

## [1.1.11](https://github.com/taplytics/taplytics-react-native/compare/1.1.10...1.1.11)

- Add push open listener compatability for cold open after force close on Android.

## [1.1.10](https://github.com/taplytics/taplytics-react-native/compare/1.1.8...1.1.10)

- Push listener backwards compatibility for react native `<0.50.0`

## [1.1.8](https://github.com/taplytics/taplytics-react-native/compare/1.1.7...1.1.8)

- Remove erroneous log

## [1.1.7](https://github.com/taplytics/taplytics-react-native/compare/1.1.5...1.1.7)

- Fix registering for push on iOS

## [1.1.5](https://github.com/taplytics/taplytics-react-native/compare/1.1.4...1.1.5)

- Allow for push notification hooks in iOS

## [1.1.4](https://github.com/taplytics/taplytics-react-native/compare/1.1.3...1.1.4)

- Add error states for various async calls

## [1.1.3](https://github.com/taplytics/taplytics-react-native/compare/1.0.12...1.1.3)

- RN 0.47+ Support

## [1.0.12](https://github.com/taplytics/taplytics-react-native/compare/1.0.11...1.0.12)

- Update reference to iOS push register method.

## [1.0.11](https://github.com/taplytics/taplytics-react-native/compare/1.0.10...1.0.11)

- Fixed json attributes passed into logEvent and logRevenue causing crash.

## [1.0.10](https://github.com/taplytics/taplytics-react-native/compare/1.0.9...1.0.10)

- Fixed search paths for non-cocoapods import

## [1.0.9](https://github.com/taplytics/taplytics-react-native/compare/1.0.8...1.0.9)

- Fixed getSessionInfo crash

## [1.0.8](https://github.com/taplytics/taplytics-react-native/compare/1.0.7...1.0.8)

- Added push ID to value sent to BroadcastReceiver

## [1.0.7](https://github.com/taplytics/taplytics-react-native/compare/1.0.6...1.0.7)

- Added safety in broadcastReceiver

## [1.0.6](https://github.com/taplytics/taplytics-react-native/compare/1.0.5...1.0.6)

- Fixed broadcastreceiver sending wrong push event types.

## [1.0.5](https://github.com/taplytics/taplytics-react-native/compare/1.0.4...1.0.5)

- Updated android package name from com.react to com.taplytics.react

## [1.0.4](https://github.com/taplytics/taplytics-react-native/compare/1.0.3...1.0.4)

- Updated search paths

## [1.0.3](https://github.com/taplytics/taplytics-react-native/compare/1.0.1...1.0.3)

- Fixed npm references not pointing to the right project.
- Cleaned up many iOS functions.

## [1.0.1](https://github.com/taplytics/taplytics-react-native/compare/1.0.0...1.0.1)

- Fixed react native minimum version number.

## 1.0.0

- Initial version of React Native SDK. Please contact us with any questions or use git issues.
