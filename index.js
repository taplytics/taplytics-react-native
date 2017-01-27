
import { NativeModules, DeviceEventEmitter } from 'react-native';
import _ from 'lodash';

const { Taplytics } = NativeModules;

let variables = {}

Taplytics.newSyncVariable = (name, defaultValue) => {
  let func = null;
  if (_.isBoolean(defaultValue)) {
    func = Taplytics._newSyncBool(name, defaultValue)
  } else if (_.isString(defaultValue)) {
    func = Taplytics._newSyncString(name, defaultValue)
  } else if (_.isNumber(defaultValue)) {
    func = Taplytics._newSyncNumber(name, defaultValue)
  } else if (_.isPlainObject(defaultValue)) {
    defaultValue = JSON.stringify(defaultValue)
    func = Taplytics._newSyncObject(name, defaultValue).then(value => JSON.parse(value))
  } else {
    return console.error("INVALID TYPE PASSED TO ASYNC VARIABLE")
  }
  return func.then(value => {
    variables[name] = value
  })
}

Taplytics.newAsyncVariable = (name, defaultValue, callback) => {
  if (_.isBoolean(defaultValue)) {
    Taplytics._newAsyncBool(name, defaultValue)
  } else if (_.isString(defaultValue)) {
    Taplytics._newAsyncString(name, defaultValue)
  } else if (_.isNumber(defaultValue)) {
    Taplytics._newAsyncNumber(name, defaultValue)
  } else if (_.isPlainObject(defaultValue)) {
    value = JSON.stringify(defaultValue)
    Taplytics._newAsyncObject(name, value)
  } else {
    return console.error("INVALID TYPE PASSED TO ASYNC VARIABLE")
  }

  DeviceEventEmitter.addListener(name, (event) => {
    let value = event.value
    if (_.isPlainObject(defaultValue)) {
      value = JSON.parse(event.value)
    }
    variables[name] = value
    callback && callback(value)
  })
}

Taplytics.getVariables = () => _.cloneDeep(variables);

export default Taplytics;
