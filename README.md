
# react-native-taplytics-react

## Getting started

`$ npm install react-native-taplytics-react --save`

### Mostly automatic installation

`$ react-native link react-native-taplytics-react`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-taplytics-react` and add `RNTaplyticsReact.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNTaplyticsReact.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNTaplyticsReactPackage;` to the imports at the top of the file
  - Add `new RNTaplyticsReactPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-taplytics-react'
  	project(':react-native-taplytics-react').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-taplytics-react/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-taplytics-react')
  	```

# Usage in Javascript
import RNTaplyticsReact from 'react-native-taplytics-react';

// TODO: What do with the module?
RNTaplyticsReact;
```
  
