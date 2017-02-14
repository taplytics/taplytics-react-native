You can get started with using Taplytics React-Native in minutes. Just follow the steps below:

|#  |                                                                       |
|---|---                                                                        |
|1  | [Setting User Attributes](#1-setting-user-attributes)                         
|2  | [Tracking Events](#2-track-events)                                     |
|3  | [Dynamic Variables & Code Blocks](#3-dynamic-variables--code-blocks)|         
|4  | [Currently Running Experiments and Vars](#4-currently-running-experiments-and-variables)                         |      
|4  | [Sessions](#5-sessions)                          |
|5  | [Push](#5-Push)                          |


## 1. Setting User Attributes

It's possible to send custom user attributes to Taplytics using a JSONObject of user info.

The possible fields are:

|Parameter  |Type         |
|---      |---          |
|email      | String    |
|user_id    | String    |
|firstname  | String    |
|lastname   | String    |
|name     | String    |
|age      | Number    |
|gender     | String    |

You can also add anything else you would like to this JSONObject and it will also be passed to Taplytics.

An example with custom data:

```javascript
attributes = {"email":"johnDoe@taplytics.com",
				 "name":"John Doe",
				 "age":25,
				 "gender":"male",
				 "avatarurl":"https://someurl.com/someavatar.png",
				 "someCustomAttribute":50,
				 "paidSubscriber":true,
				 "subscriptionPlan":"yearly",
				}
				
Taplytics.setUserAttributes(attributes)
```
#### User Attributes on First Launch

User Attributes set before `startTaplytics` is called will be used for experiment segmentation on the first session of your app. Any attributes that are set after `startTaplytics` is called will not be used for experiment segmentation until the next session of your app.

### Resetting user attributes or Logging out a user 

Once a user logs out of your app, their User Attributes are no longer valid. You can reset their data by calling `resetAppUser`, make sure you do not set any new user attributes until you receive the callback.

```javascript
  Taplytics.resetAppUser()
```
### Retrieving Session Info

Taplytics also offers a method to retrieve select information of what you know about a session at a given time. This method returns the user's Taplytics identifier (`appUser_id`) and current session id (`session_id`)

```javascript
Taplytics.getSessionInfo().then((results) => {
    // use results map
})
```

## 2. Track Events

####Automatic Events

Some events are automatically tracked by Taplytics and will appear on your dashboard. These events are:

* App Start
* App background

No changes are needed in your code for this event tracking to occur.

Currently the react SDK does **not** support automatic tracking of page changes. *This is in the works*.

####Custom Events

To log your own events, simply call `Taplytics.logEvent`.

Due to the nature of React Native, you must supply all three parameters of Name, value, and custom attributes. 

If you do not wish to pass these in, simply do the following:

```javascript
Taplytics.logEvent("event name", 0, {});
```

Otherwise

```javascript
num = 5
attributes = {"custom attribute":"something"}
Taplytics.logEvent("eventName", num, attributes)
```

####Revenue Logging

It's also possible to log revenue.

Revenue logging is the same as event logging, only call `logRevenue`:

```javascript
someRevenue = 10000000;  
Taplytics.logRevenue("Revenue Name", someRevenue, {});
```

And similarly, with custom object data:

```javascript
someRevenue = 10000000;
customInfo = {"custom attribute":"something"}
Taplytics.logRevenue("Revenue Name", someRevenue, customInfo);
```

## 3. Dynamic Variables & Code Blocks

**To see and modify these variables or blocks on the dashboard, the app must be launched and this code containing the variable or block must be navigated to at least once.**

The code below is used to send the information of the variable or block to Taplytics, so it will appear on the dashboard.

### Dynamic Variables

Taplytics variables are values in your app that are controlled by experiments. Changing the values can update the content or functionality of your app. Variables are reusable between experiments and operate in one of two modes: synchronous or asynchronous.

You can use both sync and async variables from within Javascript. 

**However, because React Native uses an asynchronous communication link to native code, both these types of variables require a callback to retrieve their value.**

#### Synchronous

Synchronous variables are guaranteed to have the same value for the entire session and will have that value immediately after construction.

Synchronous variables take two parameters in its constructor:

1. Variable name
2. Default Value
3. Promise

The type of the variable will be inferred from the type of value passed in as the default. This method returns a promise which resolves with the value of the synchronous variable:

```javascript
Taplytics.newSyncVariable("My Variable", "default").then(value => {
  // do something
})
```

IMPORTANT: The value of these variables will be determined immediately, ie. the SDK will not wait for properties to be loaded from the server. Thus, if you want to ensure that the variables have their correct variables based on your experiment segmentation, you must initialize them after the properties have been loaded from the server. This module provides a callback to achieve this:

```javascript
Taplytics.propertiesLoadedCallback(() => {
  // load variables here
})
```

#### Asynchronous

Asynchronous variables take care of insuring that the experiments have been loaded before returning a value. This removes any danger of tainting the results of your experiment with bad data. What comes with the insurance of using the correct value is the possibility that the value will not be set immediately. If the variable is constructed *before* the experiments are loaded, you won't have the correct value until the experiments have finished loading. If the experiments fail to load, then you will be given the default value, as specified in the variables constructor.

Asynchronous variables take three parameters in its constructor:

1. Variable name
2. Default Value
3. Variable Callback


To create an async variable:

```javascript
Taplytics.newAsyncVariable(name, defaultValue, variableChangedCallback)
```

The third parameter is a function which will be called any time the value of the variable is changed. It is passed the current value of the variable.  When the variable's value has been updated, the listener will be called with that updated value. You can specify what you want to do with the variable inside the callback.

### Code Blocks

Similar to Dynamic Variables, Taplytics has an option for 'Code Blocks'. Code blocks are linked to Experiments through the Taplytics website very much the same way that Dynamic Variables are, and will be executed based on the configuration of the experiment through the Taplytics website. A Code Block is a callback that can be enabled or disabled depending on the variation. If enabled, the code within the callback will be executed. If disabled, the variation will not get the callback.

A Code Block can be used alongside as many other Code Blocks as you would like to determine a combination that yields the best results. Perhaps there are three different Code Blocks on one activity. This means there could be 8 different combinations of Code Blocks being enabled / disabled on that activity if you'd like.

You can register Javascript functions as code blocks to be run or not depending on your experiment variation:

```javascript
Taplytics.runCodeBlock(name, someFunction)
```

By default, a code block will _not_ run unless enabled on the Taplytics Dashboard. It must be enabled for a Variation before it will run.


## 4. Currently Running Experiments and Variables

#### Variables

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


### Running experiments and variations

If you would like to see which variations and experiments are running on a given device, there exists a `getRunningExperimentsAndVariations` function which provides a callback with a map of the current experiments and their running variation. An example:

```javascript
Taplytics.getRunningExperimentsAndVariations().then((results) => {
    // use results map
})
```

NOTE: This function runs asynchronously, as it waits for the updated properties to load from Taplytics' servers before returning the running experiments.

If you want to see when the experiments have been loaded by Taplytics, you can wrap this in a `propertiesLoadedCallback`

```javascript
Taplytics.propertiesLoadedCallback(() => {
	Taplytics.getRunningExperimentsAndVariations().then((results) => {
    // use results map
})})
```

## 5. Sessions

### StartNewSession

To manually force a new user session (ex: A user has logged in / out), there exists 

```javascript
Taplytics.startNewSession()
```

This method returns a promise that resolves when the session has been created

You can also register a callback to be run when Taplytics creates a new session:

```javascript
Taplytics.setTaplyticsNewSessionListener(callback)
```

### Retrieving Session Info

Taplytics also offers a method to retrieve select information of what you know about a session at a given time. This method returns the user's Taplytics identifier (`appUser_id`) and current session id (`session_id`)

```javascript
Taplytics.getSessionInfo().then((results) => {
    // use results map
})
```

## 5. Push

[Please view the push docs](https://github.com/taplytics/taplytics-teact-tative/blob/master/PUSH.md)




