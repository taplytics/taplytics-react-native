
import { NativeModules, DeviceEventEmitter, Platform } from 'react-native';
import _ from 'lodash';

const { Taplytics } = NativeModules;

let variables = {}
let variableChangedListener = () => {}

Taplytics.registerVariablesChangedListener = (listener) => {
  variableChangedListener = listener;
}

function setVariable(name, value) {
  variables[name] = value;
  variableChangedListener && variableChangedListener(Taplytics.getVariables())
}

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
    return console.error("INVALID TYPE PASSED TO SYNC VARIABLE CONSTRUCTOR")
  }
  return func.then(value => {
    setVariable(name, value)
    return value
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
    return console.error("INVALID TYPE PASSED TO ASYNC VARIABLE CONSTRUCTOR")
  }

  DeviceEventEmitter.addListener(name, (event) => {
    let value = event.value
    if (_.isPlainObject(defaultValue)) {
      value = JSON.parse(event.value)
    }
    setVariable(name, value)
    callback && callback(value)
  })
}

Taplytics.setUserAttributes = (attributes) => {
  Taplytics._setUserAttributes(JSON.stringify(attributes))
}

Taplytics.setTaplyticsExperimentsUpdatedListener = (listener) => {
  Taplytics._setTaplyticsExperimentsUpdatedListener()

  DeviceEventEmitter.addListener("experimentsUpdated", (event) => {
    listener && listener(value)
  })
}


Taplytics.setTaplyticsNewSessionListener = (listener) => {
  Taplytics._setTaplyticsNewSessionListener()

  DeviceEventEmitter.addListener("newSession", (event) => {
    listener && listener(value)
  })
}

let pushOpenedListeners = []
let pushDismissedListeners = []
let pushReceivedListeners = []

Taplytics.registerPushOpenedListener = (listener) => {
  console.log("Registering push open")
  pushOpenedListeners.push(listener)
}

Taplytics.registerPushDismissedListener = (listener) => {
  pushDismissedListeners.push(listener)
}

Taplytics.registerPushReceivedListener = (listener) => {
  pushReceivedListeners.push(listener)
}

DeviceEventEmitter.addListener("pushOpened", (event) => {
  value = JSON.parse(event.value)
  _.each(pushOpenedListeners, listener => _.isFunction(listener) && listener(value))
})

DeviceEventEmitter.addListener("pushDismissed", (event) => {
  value = JSON.parse(event.value)
  _.each(pushDismissedListeners, listener => _.isFunction(listener) && listener(value))
})

DeviceEventEmitter.addListener("pushReceived", (event) => {
  value = JSON.parse(event.value)
  _.each(pushReceivedListeners, listener => _.isFunction(listener) && listener(value))
})

Taplytics.getVariables = () => _.cloneDeep(variables);

export default Taplytics;
