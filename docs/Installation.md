# Getting Started

Visit the [React Native docs](https://facebook.github.io/react-native/docs/getting-started) to get started with a React Native project.

**Install the `taplytics-react-native` package:**

As of 2.1.0, Taplytics now supports 0.60.0 React Native projects and above! To get started, just do:

```bash
yarn add taplytics-react-native --save
```

**NOTE:** If you previously have installed Taplytics, you may be shown a warning if you haven't unlinked your previously linked projects. To fix this, simply run the commmand:

```bash
yarn unlink taplytics-react-native
```

**NOTE:** For react native versions <0.60, automatic linking is not supported and you will need to run the manual linking command which should link the package on both iOS and android.

```bash
react-native link taplytics-react-native
```

## 2. iOS Setup

- Once the `taplytics-react-native` package has been installed, navigate to your `/ios` folder and execute the command `pod install`.

2. We still require you to [configure Taplytics within the iOS app as mentioned in the iOS SDK docs](https://github.com/taplytics/taplytics-ios-sdk/blob/master/START.md#1-install).

**NOTE:** For react native version <0.6 or if your project is not utilizing Cocoapods a [manual installation of the Taplytics iOS SDK](https://github.com/taplytics/taplytics-ios-sdk/blob/master/START.md#1-install) will be required.

## 3. Android Setup

- We require you to [configure Taplytics within the Android app as mentioned in the Android SDK docs.](https://github.com/taplytics/taplytics-android-sdk/blob/master/START.md#1-installation).

## 4. Start making Experiments and sending Push Notifications to your users.

This module wraps most native SDK methods in Javascript, allowing you to access features such as dynamic variables, code blocks and event logging in the React Native environment.

Be sure to import Taplytics into your react application:

```javascript
import Taplytics from 'taplytics-react-native'
```

Then follow the [Experiment](./Experiments.md) guide and the [Push Notification](./Push.md) guide to get started.
