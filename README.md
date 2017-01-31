
# Taplytics React Natvie

## Getting started

This package requires React Native >0.4

`$ npm install taplytics-react-native --save`

### Mostly automatic installation

`$ react-native link taplytics-react-native`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `taplytics-react-native` and add `RNTaplyticsReact.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNTaplyticsReact.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.TaplyticsReactPackage;` to the imports at the top of the file
  - Add `new TaplyticsReactPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':taplytics-react-native'
  	project(':taplytics-react-native').projectDir = new File(rootProject.projectDir, 	'../node_modules/taplytics-react-native/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':taplytics-react-native')
  	```

### Usage in Javascript
```javascript
...

import Taplytics from 'taplytics-react-native';

...

```

## Taplytics Setup
To use this wrapper, the Taplytics SDK must be setup in your React native application by following the instructions from the [Taplytics Setup Guide](https://taplytics.com/docs/guides/install-sdk)

The SDK must be initialized in the standard way, using 
```javascript
Taplytics.startTaplytics(context, "YOUR SDK KEY");
```
in your application's native code.

## Usage
This module wraps most native SDK methods in Javascript, allowing you to access features such as dynamic variables, code blocks and event logging in the React Native environment.

### Code Variables
You can use both sync and async variables from within Javascript. However, because React Native uses an asynchronous communication link to native code, both these types of variables require a callback to retrieve their value. 

#### Sync Variables
To create a synchronous variable, simply call:
```javascript
Taplytics.newSyncVariable(name, defaultValue)
```

The type of the variable will be inferred from the type of value passed in as the default. This method returns a promise which resolves with the value of the synchronous variable:

```javascript
Taplytics.newSyncVariable("My Variable", "default").then(value => {
  // do something
})
```

IMPORTANT: The value of these variables will be determined immediately, ie. the SDK will not wait for properties to be loaded from the server. Thus, if you want to ensure that the variables have their correct variables based on your experiment segmentation, you must initialize them after the properties have been loaded from the server. This module provides a callback to achieve this:

```javascript
Taplytics.propertiesLoadedCallback().then(() => {
  // do stuff
})
```

#### Async variables
In the Taplytics SDK, an asynchronous variable is one which only takes on a value once the client properties have been loaded from the server. Additionally, its value can change mid-session based on updates to experiments.

To create an async variable:
```javascript
Taplytics.newAsyncVariable(name, defaultValue, variableChangedCallback)
````

The third parameter is a function which will be called any time the value of the variable is changed. It is passed the current value of the variable. 

#### Get all variables
To make it easier to keep track of your variables, this module also provides a method to retrieve an object map of their names and values:
```javascript
Taplytics.getVariables() 
```
which returns the variables in the format:
```javascript
{
  "name1": value1,
  "name2": value2
  ...
}
```
This object will always contain the latest value of every Taplytics variable that has been initialized from Javascript

You can also register a function to be called whenever this object changes:
```javascript
Taplytics.registerVariablesChangedListener(callback)
```
The callback will be passed the same object map as above. Only one callback function can be registered at a time. Calling this method again will overwrite the old callback.

### Code Blocks
You can register Javascript functions as code blocks to be run or not depending on your experiment variation:
```javascript
Taplytics.runCodeBlock(name, someFunction)
```
The code block must be enabled in the dashboard for your current variation before it will be run. 

### Events
There are methods available for logging events:
```javascript
Taplytics.logEvent(name, value)
```
You can also log a revenue event:
```javascript
Taplytics.logRevenue(name, value)
```
In both cases, the value that is passed to the event must be a number.

### Sessions
You can force the creation of a new Taplytics user session if something happens in your app, such as a login or logout action:
```javascript
Taplytics.startNewSession()
```
This method returns a promise that resolves when the session has been created

You can also register a callback to be run when Taplytics creates a new session:
```javascript
Taplytics.setTaplyticsNewSessionListener(callback)
```

If a user logs out of your app, their user attributes become invalid. You should reset their data by calling:
```javascript
  Taplytics.resetAppUser()
```
which returns a promise that resolves when the attributes have been successfully reset.

### User Attributes
To set custom user attributes, call:
```javascript
Taplytics.setUserAttributes(attributes)
```
with an object containing key-value pairs of your attribute names and values. Only certain fields are acceptable to include in the attributes object. Read about them in [the docs](https://taplytics.com/docs/android-sdk/getting-started#3-setting-user-attributes)

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
  android:name="com.reactlibrary.TRNBroadcastReceiver"
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

On iOS, use the built-in React Native push notification module, which can be found [here](https://facebook.github.io/react-native/docs/pushnotificationios.html)






