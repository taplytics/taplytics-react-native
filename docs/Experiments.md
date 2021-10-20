# Experiments

You can get started with using Taplytics React-Native in minutes. Just follow the steps below:

| #   |                                                                                               |
| --- | --------------------------------------------------------------------------------------------- |
| 1   | [User Attributes](#1-user-attributes)                                                         |
| 2   | [Tracking Events](#2-track-events)                                                            |
| 3   | [Dynamic Variables & Code Blocks](#3-dynamic-variables--code-blocks)                          |
| 4   | [Feature Flags](#4-feature-flags)                                                             |
| 5   | [Currently Running Experiments and Variables](#5-currently-running-experiments-and-variables) |
| 6   | [Sessions](#6-sessions)                                                                       |
| 7   | [Push](#7-push)                                                                               |

## 1. User Attributes

### Set User Attributes

```javascript
Taplytics.setUserAttributes(customUserAttributes)
```

Use the `setUserAttributes` method to send Taplytics a JSON Object of user info. The possible fields for `customUserAttributes` are listed below. You can also add anything else you would like to this JSON Object and it will also be passed to Taplytics.

#### `customAttributes` Type

| PARAMETER    | TYPE     |
| ------------ | -------- |
| `email`      | `string` |
| `user_id`    | `string` |
| `firstname`  | `string` |
| `lastname`   | `string` |
| `name`       | `string` |
| `age`        | `number` |
| `gender`     | `string` |
| `customData` | `object` |

#### Example

```javascript
await Taplytics.setUserAttributes({
  email: 'johnDoe@taplytics.com',
  name: 'John Doe',
  age: 25,
  gender: 'male',
  avatarurl: 'https://someurl.com/someavatar.png',
  customData: {
    someCustomAttribute: 50,
    paidSubscriber: true,
    subscriptionPlan: 'yearly',
  },
})
```

### Resetting user attributes or Logging out a user

```javascript
Taplytics.resetAppUser()
```

Once a user logs out of your app, their user attributes are no longer valid. Use the `resetAppUser` method to reset their attributes.

## 2. Track Events

### Automatic Events

Some events are automatically tracked by Taplytics and will appear on your dashboard. These events are:

- App Start
- App background

No changes are needed in your code for this event tracking to occur.

### Custom Events

```javascript
Taplytics.logEvents(eventName, value, customAttributes)
```

Log custom event's using the `logEvent` method.

#### Parameters

| NAME               | TYPE     | REQUIRED                | DESCRIPTION                                                                                                                                               |
| ------------------ | -------- | ----------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `eventName`        | `string` | `v2.x`: Yes `v3.x`: Yes | The name of the event.                                                                                                                                    |
| `value`            | `number` | `v2.x`: Yes `v3.x`: No  | A numerical value associated with the event. `v3.x`: This is an optional parameter, if no value is passed it is initialized to 0.                         |
| `customAttributes` | `object` | `v2.x`: Yes `v3.x`: No  | A custom object that gets associated with the event. `v3.x`: This is an optional parameter, if no value is passed it is initialized with an empty object. |

#### Examples

```javascript
// For v2.x and v3.x with value and customAttributes params
Taplytics.logEvent('eventName', 5, {
  'custom attribute': 'custom attribute value',
})

// For v2.x without value and customAttributes params
Taplytics.logEvent('eventName', 0, {})

// For v3.x without value and customAttributes params
Taplytics.logEvent('eventName')
```

### Revenue Logging

```javascript
Taplytics.logRevenue(eventName, value, customAttributes)
```

It's also possible to log revenue. Revenue logging works the same way as event logging, except you will need to utilize the `logRevenue` method.

#### Parameters

| NAME               | TYPE     | REQUIRED                | DESCRIPTION                                                                                                                                                       |
| ------------------ | -------- | ----------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `eventName`        | `string` | `v2.x`: Yes `v3.x`: Yes | The name of the revenue event.                                                                                                                                    |
| `value`            | `number` | `v2.x`: Yes `v3.x`: No  | A numerical value associated with the revenue event. `v3.x`: This is an optional parameter, if no value is passed it is initialized to 0.                         |
| `customAttributes` | `object` | `v2.x`: Yes `v3.x`: No  | A custom object that gets associated with the revenue event. `v3.x`: This is an optional parameter, if no value is passed it is initialized with an empty object. |

#### Examples

```javascript
// For v2.x and v3.x with value and customAttributes params
Taplytics.logRevenue('Revenue Name', 100, {
  'custom attribute': 'custom attribute value',
})

// For v2.x without value and customAttributes params
Taplytics.logRevenue('Revenue Name', 0, {})

// For v3.x without value and customAttributes params
Taplytics.logRevenue('Revenue Name')
```

## 3. Dynamic Variables & Code Blocks

Taplytics variables are values in your app that are controlled by experiments. Changing the values can update the content or functionality of your app. Variables are reusable between experiments and operate in one of two modes: synchronous or asynchronous. You can use both sync and async variables from within your app.

### Synchronous Dynamic Variables

```javascript
Taplytics.newSyncVariable(name, defaultValue)
```

> Loading properties prior to retrieving variable value
> The value of these variables will be determined immediately, ie. the SDK will not wait for properties to be loaded from the server. Thus, if you want to ensure that the variables have their correct variables based on your experiment segmentation, you must initialize them after the properties have been loaded from the server.
>
> This module provides the `propertiesLoadedCallback` method to achieve this. The method returns back an event subscriber that can be used to cleanup the event listener using the `remove` function.

Synchronous variables are guaranteed to have the same value for the entire session and will have that value immediately after construction. The type of the variable will be inferred from the type of value passed in as the default. This method returns a promise which resolves with the value of the synchronous variable.

#### Parameters

| NAME           | TYPE                                    | REQUIRED | DESCRIPTION                                            |
| -------------- | --------------------------------------- | -------- | ------------------------------------------------------ |
| `name`         | `string`                                | Yes      | The name of the dynamic variable.                      |
| `defaultValue` | `string`, `boolean`, `number`, `object` | Yes      | Default value to be utilized for the dynamic variable. |

#### Examples

```javascript
const subscriber = Taplytics.propertiesLoadedCallback(async (loaded) => {
  const variableValue = await Taplytics.newSyncVariable('My Variable', 'default')
})

// Clean up subscriber
subscriber.remove()
```

### Asynchronous Dynamic Variables

```javascript
Taplytics.newAsyncVariable(name, defaultValue, callback)
```

> newAsyncVariable Usage
> **v3.x**: The `newAsyncVariable` method returns an event subscriber that can be used to cleanup the event listener using the `remove` function. This should be done to ensure that there is no memory leaks within your app.

Asynchronous variables take care of insuring that the experiments have been loaded before returning a value. This removes any danger of tainting the results of your experiment with bad data. What comes with the insurance of using the correct value is the possibility that the value will not be set immediately. If the variable is used _before_ the experiments are loaded, you won't have the correct value until the experiments have finished loading. If the experiments fail to load, then you will be given the default value, as specified in the methods constructor.

#### Parameters

| NAME           | TYPE                                    | REQUIRED | DESCRIPTION                                                                                                                           |
| -------------- | --------------------------------------- | -------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| `name`         | `string`                                | Yes      | The name of the dynamic variable.                                                                                                     |
| `defaultValue` | `string`, `boolean`, `number`, `object` | Yes      | Default value to be utilized for the dynamic variable.                                                                                |
| `callback`     | `(variableValue) => void`               | Yes      | A function that runs when the dynamic variable has been evaluated or updated, it receives the dynamic variable value as the argument. |

#### Examples

```javascript
// For v2.x
Taplytics.newAsyncVariable('My Variable', 'default value', (variableValue) => {
  // Use variableValue
})

// For v3.x
const subscriber = Taplytics.newAsyncVariable('My Variable', 'default value', (variableValue) => {
  // Use variableValue
})

subscriber.remove()
```

### Code Blocks

```javascript
Taplytics.runCodeBlock(name, codeBlock)
```

Similar to Dynamic Variables, Taplytics has an option for 'Code Blocks'. Code blocks are linked to Experiments through the Taplytics website very much the same way that Dynamic Variables are, and will be executed based on the configuration of the experiment through the Taplytics website. A Code Block is a callback that can be enabled or disabled depending on the variation. If enabled, the code within the callback will be executed. If disabled, the variation will not get the callback.

A Code Block can be used alongside as many other Code Blocks as you would like to determine a combination that yields the best results. You can register functions as code blocks to be run or not depending on your experiment variation. By default, a code block will _not_ run unless enabled on the Taplytics Dashboard. It must be enabled for a Variation before it will run.

#### Parameters

| NAME        | TYPE         | REQUIRED | DESCRIPTION                                                       |
| ----------- | ------------ | -------- | ----------------------------------------------------------------- |
| `name`      | `string`     | Yes      | The name of the code block variable.                              |
| `codeBlock` | `() => void` | Yes      | A function that will run if the code block variable is activated. |

#### Example

```javascript
Taplytics.runCodeBlock('Code Block Variable name', () => {
  // Code block function
})
```

## 4. Feature Flags

Taplytics feature flags operate in synchronous mode.

### Feature Flag Enabled

```javascript
Taplytics.featureFlagEnabled(key)
```

> Loading properties prior to determining feature flag value
> The value of `featureFlagEnabled` will be determined immediately, ie. the SDK will not wait for properties to be loaded from the server. Thus, if you want to ensure that the feature flags have their correct values based on your experiment segmentation, you must initialize them after the properties have been loaded from the server.
>
> This module provides the `propertiesLoadedCallback` method to achieve this. The method returns back an event subscriber that can be used to cleanup the event listener using the `remove` function.

Use this method to determine if a particular feature flag is enabled.

#### Parameters

| NAME  | TYPE     | REQUIRED | DESCRIPTION                  |
| ----- | -------- | -------- | ---------------------------- |
| `key` | `string` | Yes      | The key of the feature flag. |

#### Example

```javascript
const subscriber = Taplytics.propertiesLoadedCallback(async () => {
  const isEnabled = await Taplytics.featureFlagEnabled('featureFlagKey')

  if (isEnabled) {
    // Put feature code here, or launch feature from here
  }
})

// Clean up subscriber
subscriber.remove()
```

## Running Feature Flags

```javascript
Taplytics.getRunningFeatureFlags()
```

This method is used to determine which feature flags that are running on a given device.

#### Example

```javascript
const runningFeatureFlags = await Taplytics.getRunningFeatureFlags()
```

## 5. Currently Running Experiments and Variables

### Get Variables

```javascript
Taplytics.getVariables()
```

This method returns an object with experiment variables' keys and values. **It only keeps track of variables that have been fetched by either `newAsyncVariable` or `newSyncVariable`.**

### Variables Changed Listener

```javascript
Taplytics.registerVariablesChangedListener(callback)
```

You can also register a function to be called whenever the variables from `getVariables` changes. Only one callback function can be registered at a time. Calling this method again will overwrite the previous callback.

#### Parameters

| NAME       | TYPE                  | REQUIRED | DESCRIPTION                                                              |
| ---------- | --------------------- | -------- | ------------------------------------------------------------------------ |
| `callback` | `(variables) => void` | Yes      | A function that is invoked whenever an experiments' variable is updated. |

### Running experiments and variations

```javascript
Taplytics.getRunningExperimentsAndVariations()
```

> Loading properties prior to retrieving experiments
> This function runs asynchronously, as it waits for the updated properties to load from Taplytics' servers before returning the running experiments.
>
> If you want to see when the experiments have been loaded by Taplytics, you can wrap this in the `propertiesLoadedCallback` method.

Use the `getRunningExperimentsAndVariations` method to determine which variations and experiments are running on a given device.

#### Example

```javascript
const subscriber = Taplytics.propertiesLoadedCallback(async () => {
  const runningExperiments = await Taplytics.getRunningExperimentsAndVariations()
})

// Clean up subscriber
subscriber.remove()
```

## 6. Sessions

### Start New Session

```javascript
Taplytics.startNewSession()
```

To manually force a new user session (ex: A user has logged in / out), there exists

#### New Session Listener

```javascript
Taplytics.setTaplyticsNewSessionListener(callback)
```

This listener can be used to register a callback for when a new session is created.

#### Parameters

| NAME       | TYPE                  | REQUIRED | DESCRIPTION                                                  |
| ---------- | --------------------- | -------- | ------------------------------------------------------------ |
| `callback` | `(variables) => void` | Yes      | A function that gets executed when a new session is created. |

### Retrieving Session Info

```javascript
Taplytics.getSessionInfo(callback)
```

This method can be used to retrieve select information about a session at a given time. This method returns the user's Taplytics identifier; `appUser_id`, and current session ID; `session_id`.

#### Example

```javascript
const { appUser_id, session_id } = await Taplytics.getSessionInfo()
```

## 7. Push

[Please view the push docs](https://github.com/taplytics/taplytics-react-native/blob/master/PUSH.md)
