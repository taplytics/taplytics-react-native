
import { NativeModules, DeviceEventEmitter } from 'react-native';
import _ from 'lodash';

const { Taplytics } = NativeModules;

let variableTypes = {}

Taplytics.newSyncVariable = (name, defaultValue) => {
  if (_.isBoolean(defaultValue)) {
    return Taplytics._newBoolSyncVariable(name, defaultValue)
  } else if (_.isString(defaultValue)) {
    return Taplytics._newStringSyncVariable(name, defaultValue)
  } else if (_.isNumber(defaultValue)) {
    return Taplytics._newNumberSyncVariable(name, defaultValue)
  } else if (_.isPlainObject(defaultValue)) {
    defaultValue = JSON.stringify(defaultValue)
    return Taplytics._newObjectSyncVariable(name, defaultValue)
  }
}

Taplytics.newAsyncVariable = (name, defaultValue, callback) => {
  Taplytics._newAsyncVariable(name, defaultValue)
  DeviceEventEmitter.addListener(name, (event) => {
    callback && callback(event.value)
  })
}

export default Taplytics;
