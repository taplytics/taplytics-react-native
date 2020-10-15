
import { NativeEventEmitter, NativeModules, DeviceEventEmitter, Platform } from 'react-native';
import _ from 'lodash';

const { Taplytics } = NativeModules;

Taplytics.nativeEventEmitter = new NativeEventEmitter(Taplytics);

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
  const cb = (err, value) => {
    if (err) {
      console.log('Error getting async variable ', err)
      return callback && callback(defaultValue)
    }
    if (_.isPlainObject(value)) {
      value = JSON.parse(value)
    }
    setVariable(name, value)
    callback && callback(value)
  }

  let args = [name, defaultValue]
  if (Platform.OS === 'ios') {
    args.push(cb)
  }

  if (_.isBoolean(defaultValue)) {
    Taplytics._newAsyncBool(...args)
  } else if (_.isString(defaultValue)) {
    Taplytics._newAsyncString(...args)
  } else if (_.isNumber(defaultValue)) {
    Taplytics._newAsyncNumber(...args)
  } else if (_.isPlainObject(defaultValue)) {
    value = JSON.stringify(defaultValue)
    args[1] = value
    Taplytics._newAsyncObject(...args)
  } else {
    return console.error("INVALID TYPE PASSED TO ASYNC VARIABLE CONSTRUCTOR")
  }

  DeviceEventEmitter.addListener(name, (event) => {
    let value = event.value
    cb(null, value)
  })
}

Taplytics.featureFlagEnabled = (key) => {
  if (_.isString(key)) {
    return Taplytics._featureFlagEnabled(key);
  } else {
    return console.error("INVALID KEY. FEATURE FLAG KEY MUST BE A STRING")
  }
}

Taplytics.setUserAttributes = (attributes) => {
  return Taplytics._setUserAttributes(JSON.stringify(attributes))
}

Taplytics.setTaplyticsExperimentsUpdatedListener = (listener) => {
  Taplytics._setTaplyticsExperimentsUpdatedListener()

  DeviceEventEmitter.addListener("experimentsUpdated", (event) => {
    listener && listener(event.value)
  })
}

Taplytics.setTaplyticsNewSessionListener = (listener) => {
  Taplytics._setTaplyticsNewSessionListener()

  DeviceEventEmitter.addListener("newSession", (event) => {
    listener && listener(event.value)
  })
}

let pushOpenedListeners = []
let pushDismissedListeners = []
let pushReceivedListeners = []

Taplytics.registerPushNotifications = () => {
  if (Platform.OS == 'ios') {
    Taplytics._registerPushNotifications()
  }
}

Taplytics.registerPushOpenedListener = (listener) => {
  console.log("Registering push open")
  pushOpenedListeners.push(listener)
  if(Platform.OS == 'ios'){
    Taplytics._registerPushOpenedListener()
  }
}

Taplytics.registerPushDismissedListener = (listener) => {
  pushDismissedListeners.push(listener)
}

Taplytics.registerPushReceivedListener = (listener) => {
  pushReceivedListeners.push(listener)
  if(Platform.OS == 'ios'){
    Taplytics._registerPushReceivedListener()
  }
}

Taplytics.propertiesLoadedCallback = (callback) => {
  if (Platform.OS == 'ios') {
    Taplytics.nativeEventEmitter.addListener('propertiesLoadedCallback', (loaded) => {
      callback(loaded)
    })
    Taplytics._propertiesLoadedCallback()
  } else {
    Taplytics._propertiesLoadedCallback(callback)
  }
}

if (Platform.OS == 'ios') {
  try {
    Taplytics.nativeEventEmitter.addListener("pushOpened", (event) => {
      _.each(pushOpenedListeners, listener => _.isFunction(listener) && listener(event))
    }) 
  }
  catch(err) {
    //Fallback for old react native versions
    DeviceEventEmitter.addListener("pushOpened", (event) => {
      _.each(pushOpenedListeners, listener => _.isFunction(listener) && listener(event))
    })
  }
} else {
  DeviceEventEmitter.addListener("pushOpened", (event) => {
    value = JSON.parse(event.value)
    _.each(pushOpenedListeners, listener => _.isFunction(listener) && listener(value))
  })
}

if (Platform.OS == 'ios') {
  try {
    Taplytics.nativeEventEmitter.addListener("pushReceived", (event) => {
      _.each(pushReceivedListeners, listener => _.isFunction(listener) && listener(event))
    }) 
  } catch (err) {
    //Fallback for old react native versions
    DeviceEventEmitter.addListener("pushReceived", (event) => {
        _.each(pushReceivedListeners, listener => _.isFunction(listener) && listener(event))
    })
  }
} else {
  DeviceEventEmitter.addListener("pushReceived", (event) => {
    value = JSON.parse(event.value)
    _.each(pushReceivedListeners, listener => _.isFunction(listener) && listener(value))
  })
}

DeviceEventEmitter.addListener("pushDismissed", (event) => {
  value = JSON.parse(event.value)
  _.each(pushDismissedListeners, listener => _.isFunction(listener) && listener(value))
})

if (Platform.OS == 'android') {
  Taplytics.logEvent = (name, value, params) => {
    if (_.isPlainObject(params)) params = JSON.stringify(params);
    Taplytics._logEvent(name, value, params);
  }

  Taplytics.logRevenue = (name, value, params) => {
    if (_.isPlainObject(params)) params = JSON.stringify(params);
    Taplytics._logRevenue(name, value, params);
  }
}

Taplytics.getVariables = () => _.cloneDeep(variables);

export default Taplytics;