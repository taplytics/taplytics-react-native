# Taplytics React Native Hooks

> The Taplytics React Native Hooks are functions that allow you to hook into the Taplytics React Native SDK. The hooks manage the state and lifecycle updates so that you can focus on building their app rather than instrumenting the SDK.

## Installation

At the moment hooks are only available within version `3.0.0-rc.x` of the `taplytics-react-native` package. To install the release candidate version follow the command below.

```bash
yarn add taplytics-react-native@next
```

## Migrating from v2.x to v3.x

The package has switched away from default exports and now exports each of it's methods individually for improved discoverability and autocompletion.

If you're migrating over from version `2.x` of the package and want to make the transition as easy as possible, you will need to make sure that the import statements have been changed as shown below.

```jsx
import * as Taplytics from 'taplytics-react-native'

// OR

import { useFeatureFlag, logEvent } from 'taplytics-react-native'
```

## Using Hooks

Taplytics provides 4 hooks that can be used within React's functional components for easier usage of the core Taplytics React Native SDK.

### `TaplyticsProvider` (Required)

A `<TaplyticsProvider />` component is included which needs to be wrapped around the whole app.

```jsx
import 'react-native-gesture-handler'
import * as React from 'react'
import { TaplyticsProvider } from 'taplytics-react-native'

export default function App() {
  return <TaplyticsProvider>{/* Rest of your app code */}</TaplyticsProvider>
}
```

### `useFeatureFlag`

`useFeatureFlag` is a hook which can be used to determine whether a certain feature flag is enabled. If a parameter is not provided to the `useFeatureFlag` hook, it will return an object with all running feature flag's names and their associated key.

The `useFeatureFlag` returns an array which can be destructed as shown in the example below. The first property in the array is the value of a feature flag or an object of all running feature flags, the second property is a meta data object which contains an `error` and `loading` property.

```jsx
const SomeComponent = () => {
  // Returns a boolean dependent if exampleFeatureFlag is enabled.
  // Also returns the loading and error context values.
  const [flagValue, { loading, error }] = useFeatureFlag('exampleFeatureFlag')

  // Returns an object of all running feature flags.
  // The object key is the name and the value is the key of the feature flag
  // Example: { "Example Feature Flag" : "exampleFeatureFlag" }
  const [allFeatures] = useFeatureFlag()

  return (
    <View>
      <Text>Feature flag is {flagValue ? `active` : `inactive`} </Text>
      <Text>{JSON.stringify(allFeatures)} </Text>
    </View>
  )
}
```

### `useVariable`

`useVariable` is a hook that can be utilized to fetch the value of a dynamic variable. The hook will also run whenever the value of the variable is updated. The hook takes in a name parameter as well as a default value. This default value is returned in a situation where there is an error loading the variable.

The hook also returns an array that can be destructed to retrieve the value of the dynamic variable and a meta data object. The meta data object has an `error` and `loading` property, which are completely independent to each `useVariable` hook.

```jsx
const SomeComponent = () => {
  // Returns the value of the 'workHours' dynamic variable.
  // It also returns a loading and error property.
  const [workHoursValue, { loading, error }] = useVariable('workHours', 8)

  return (
    <View>
      <Text>{`The standard work hours are ${workHoursValue}`} </Text>
    </View>
  )
}
```

### `useCodeBlock`

The `useCodeBlock` hook can be used to run a code block function if the code block variable is activated. The hook takes in a name, callback parameter and a dependency array parameter (similar to the `useEffect` second parameter). The callback is only triggered if the code block variable's value is true. It will also be triggered any time that a dependency updates, if client requires that the hook only be run once, an empty array can be passed through to the final parameter.

_This example shows the `useCodeBlock` hook only being triggered once._

```jsx
const HomeScreen = ({ showModal }) => {
  // The callback will only run once due to the empty array []
  useCodeBlock(
    'showQuestionnaire',
    () => {
      showModal(<Questionnaire />)
    },
    [],
  )

  return <Home />
}
```

_This is an example of the `useCodeBlock` hook being utilized to trigger the callback whenever the orderCount variable updates._

```jsx
const OrderScreen = ({ triggerConfetti }) => {
  const [orderCount, setOrderCount] = useState(0)

  // This codeblock will be triggered whenever the `orderCount`
  // state variable updates
  useCodeBlock('showConfetti', triggerConfetti, [orderCount])

  return <Order />
}
```

### `useRunningExperiments`

This hook can be utilized to get the names of the running experiments and which variation they are running at. The hook can act as a replacement for the `getRunningExperimentsAndVariations` core SDK method.

The `useRunningExperiments` hook returns an array that can be destructed to retrieve the running experiments as well as a meta data object. The meta data object contains an `error` and `loading` property.

```jsx
const DebugScreen = ({ showModal }) => {
  // Returns an object that contains the experiments running as well as which
  // variation they are running on.
  // Example: { "Example Experiment" : "baseline" }
  const [runningExperiments, { loading, error }] = useRunningExperiments()

  return <DebugList experiments={runningExperiments} />
}
```
