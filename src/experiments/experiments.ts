import { EventSubscription, NativeEventEmitter, NativeModules } from 'react-native'
import cloneDeep from 'lodash.clonedeep'

import { CodeBlockCallback, PropertiesLoadedCallback, TaplyticsFeatureFlags, TaplyticsExperiments, TaplyticsVariableMap } from './experiments.types'

const { Taplytics } = NativeModules
const TaplyticsEventEmitter = new NativeEventEmitter(Taplytics)

/**
 * `runCodeBlock` takes a callback that can be enabled or disabled depending on the variation.
 * If enabled, the code within the callback will be executed.
 * If disabled, the variation will not get the callback.
 *
 * By default, a code block will not run unless enabled on the Taplytics Dashboard.
 * It must be enabled for a Variation before it will run.
 *
 * @param name The name of the code block variable.
 * @param codeBlock A function that will run if the code block variable is activated.
 */
export const runCodeBlock = (name: string, codeBlock: CodeBlockCallback): void => Taplytics._runCodeBlock(name, codeBlock)

/**
 * @deprecated Use the `setTaplyticsNewSessionListner` instead.
 *
 * Use this method to ensure that all the feature flag and experiment variables
 * have been loaded from the server prior to utlizing them. The callback
 * returns back an event subscriber that can be used to cleanup the event
 * listener using the `remove` function.
 *
 * On iOS the callback is also triggered when a new session is generated or
 * if the user's `email` or `user_id` attributes have been updated using the
 * `setUserAttributes` method.
 *
 * @param callback A function that runs once properties have been loaded.
 *
 * @returns An event subscriber object is returned. Use the `remove` function to clean up the event listener.
 */
export const propertiesLoadedCallback = (callback: PropertiesLoadedCallback): EventSubscription => {
  const eventSubscriber = TaplyticsEventEmitter.addListener('propertiesLoadedCallback', ({ loaded }) => {
    callback(loaded)
  })
  Taplytics._propertiesLoadedCallback()

  return eventSubscriber
}

/**
 * Called to determine which feature flags that are running on a given device.
 *
 * @returns A promise that resolves to an object with running feature flags' names and their associated key.
 */
export const getRunningFeatureFlags = (): Promise<TaplyticsFeatureFlags> => Taplytics._getRunningFeatureFlags()

/**
 * Determine if a particular feature flag is enabled.
 *
 * NOTE: The value of featureFlagEnabled will be determined immediately, ie. the SDK
 * will not wait for properties to be loaded from the server. Thus, if you want to ensure
 * that the variables have their correct values based on your experiment segmentation,
 * you must initialize them after the properties have been loaded from the server through
 * the `propertiesLoadedCallback` method.
 *
 * @param key The key of the feature flag
 *
 * @returns A promise that resolves to a boolean.
 */
export const featureFlagEnabled = (key: string): Promise<boolean> => {
  return Taplytics._featureFlagEnabled(key)
}

/**
 * Called to determine which variations and experiments are running on a given device.
 *
 * @returns A promise that resolves to an object of current experiments and their running variation.
 */
export const getRunningExperimentsAndVariations = (): Promise<TaplyticsExperiments> => Taplytics._getRunningExperimentsAndVariations()

/**
 * The method that gets invoked whenever a experiment variables' value is updated.
 *
 */
let variablesChangedListener: (variables: TaplyticsVariableMap) => void | Promise<void>

/**
 * Use this function to register a listener for whenever an experiments' variable value is changed.
 *
 * @param listener The method that is invoked whenever an experiments' variable is updated.
 */
export const registerVariablesChangedListener = (listener: (variables: TaplyticsVariableMap) => void | Promise<void>) => {
  variablesChangedListener = listener
}

/**
 * An object that holds the keys and values of dynamic varibales fetched by `newSyncVariable`
 * and `newAsyncVariable`. It is then utilized by the listener passed into `registerVariablesChangedListener`
 * and by `getVariables`.
 */
const dynamicVariables: TaplyticsVariableMap = {}

/**
 * This method updates the `dynamicVariable` object, as well as invokes the method passed into
 * the `registerVariablesChangedListener` method.
 *
 * This method gets called inside of `newSyncVariable` and `newAsyncVariable`.
 *
 * @param name The name of the dynamic variable.
 * @param value The value of the dynamic variable.
 */
const updateDynamicVariables = (name: string, value: string | boolean | object | number) => {
  // Update variables
  dynamicVariables[name] = value

  // Trigger listener
  variablesChangedListener && variablesChangedListener(cloneDeep<TaplyticsVariableMap>(dynamicVariables))
}

/**
 * This method returns an object with experiment variables' keys and values. It only keeps track of
 * variables that have been fetched by either `newAsyncVariable` or `newSyncVariable`.
 *
 * @returns An object that holds the key's and values of the experiment variables.
 */
export const getVariables = (): TaplyticsVariableMap => cloneDeep<TaplyticsVariableMap>(dynamicVariables)

/**
 * A ID that is used to track each invokation of the `newAsyncVariable` method.
 * This ID is passed to the native layer, and eventually passed back whenever the
 * `asyncVariable` event is triggered.
 */
let asyncVariableCallbackID = 0

/**
 * This function ensures that the experiments have been loaded before returning a value.
 * This removes any danger of tainting the results of experiments with bad data.
 * However, this does mean that the value will not be set immediately. In the case that
 * the experiments fail to load, then the default value will be returned.
 *
 * @param name The name of the dynamic variable.
 * @param defaultValue Default value to be utilized for the dynamic variable.
 * @param callback A function that runs when the dynamic variable has been evaluated,
 * it recieves the dyanimc variable value as the argument.
 *
 * @returns An event subscriber object is returned. Use the `remove` function to clean up the event listener.
 */
export const newAsyncVariable = <T>(name: string, defaultValue: T, callback: (variable: T) => void) => {
  // Increment the ID
  asyncVariableCallbackID++

  /**
   * Store a locally scoped reference to the ID that the listener
   * utilizes to check against it's argument's ID. This is done to ensure
   * that only the intended callback is triggered.
   */
  const localScopeID = asyncVariableCallbackID

  const subscriber = TaplyticsEventEmitter.addListener('asyncVariable', ({ id, value }) => {
    if (localScopeID !== id) return

    // Trigger the callback
    callback(value)

    // Update the dynamic variables used by `getVariables`
    updateDynamicVariables(name, value)
  })

  const nativeModuleArgs = [name, defaultValue, asyncVariableCallbackID]

  switch (typeof defaultValue) {
    case 'boolean':
      Taplytics._newAsyncBool(...nativeModuleArgs)
      break
    case 'number':
      Taplytics._newAsyncNumber(...nativeModuleArgs)
      break
    case 'string':
      Taplytics._newAsyncString(...nativeModuleArgs)
      break
    case 'object':
      Taplytics._newAsyncObject(...nativeModuleArgs)
      break
    default:
      console.error('INVALID TYPE PASSED TO ASYNC VARIABLE CONSTRUCTOR')
      break
  }

  return subscriber
}

/**
 * This function is a synchronous means of retrieving a dynamic variables' value.
 * It guarantees to have the same value for the entire session and will have that
 * value immediately after construction. Ensure that the experiments have been loaded
 * in before utilizing this method via the `propertiesLoadedCallback` or `setTaplyticsNewSessionListner`
 * method, otherwise it will return the default value.
 *
 * @param name The name of the dynamic variable.
 * @param defaultValue Default value to be utilized for the dynamic variable.
 *
 * @returns A promise that resolves to the dynamic variable.
 */
export const newSyncVariable = async <T>(name: string, defaultValue: T): Promise<T> => {
  let syncVariable: Promise<T>

  const nativeModuleArgs = [name, defaultValue]

  switch (typeof defaultValue) {
    case 'boolean':
      syncVariable = await Taplytics._newSyncBool(...nativeModuleArgs)
      break
    case 'number':
      syncVariable = await Taplytics._newSyncNumber(...nativeModuleArgs)
      break
    case 'string':
      syncVariable = await Taplytics._newSyncString(...nativeModuleArgs)
      break
    case 'object':
      syncVariable = await Taplytics._newSyncObject(...nativeModuleArgs)
      break
    default:
      console.error('INVALID TYPE PASSED TO ASYNC VARIABLE CONSTRUCTOR')
      syncVariable = Promise.reject()
      break
  }

  // Update the dynamic variables used by `getVariables`
  updateDynamicVariables(name, syncVariable)

  return syncVariable
}
