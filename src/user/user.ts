import { EventSubscription, NativeEventEmitter, NativeModules, Platform } from 'react-native'

import { TaplyticsUserAttributes, TaplyticsSessionInfo } from './user.types'

const { Taplytics } = NativeModules
const TaplyticsEventEmitter = new NativeEventEmitter(Taplytics)

/**
 * Use this function to manually force a new user session.
 *
 */
export const startNewSession = (): Promise<void> => Taplytics._startNewSession()

/**
 * This function is used to retrieve select information about a session at a given time.
 *
 * @returns A promise that resolves to an object of with `appUser_id` and `session_id` properties.
 */
export const getSessionInfo = (): Promise<TaplyticsSessionInfo> => Taplytics._getSessionInfo()

/**
 * Use this listener to ensure that all the properties have been loaded from the server
 * prior to utlizing them. The listener returns back an event subscriber that can be used to
 * cleanup the event listener using the `remove` function.
 *
 * The listener triggers when properties have been loaded, a new session is generated or a user's
 * `email` or `user_id` fields are updating using the `setUserAttribute` method.
 *
 * @param callback A function that gets executed when a new session is created.
 *
 * @returns On `ios` and `android` devices an event subscriber object is returned.
 * Use the `remove` function to clean up the event listener.
 */
export const setTaplyticsNewSessionListener = (callback: (loaded: boolean) => void): EventSubscription | undefined => {
  let subscriber: EventSubscription

  if (Platform.OS === 'android') {
    subscriber = TaplyticsEventEmitter.addListener('newSession', ({ loaded }) => {
      callback && callback(loaded)
    })
    Taplytics._setTaplyticsNewSessionListener()

    return subscriber
  }

  if (Platform.OS === 'ios') {
    subscriber = TaplyticsEventEmitter.addListener('newSessionCallback', ({ loaded }) => {
      callback && callback(loaded)
    })
    Taplytics._newSessionCallback()

    return subscriber
  }
}

/**
 * Use this function to segment your users based on custom user attributes.
 * Ensure that either the `user_id` or `email` is unique to identify the user across multiple device.
 *
 * @param attributes An object of type `TaplyticsUserAttributes`.
 *
 */
export const setUserAttributes = (attributes: TaplyticsUserAttributes): Promise<void> => {
  return Taplytics._setUserAttributes(JSON.stringify(attributes))
}

/**
 * Once a user logs out of your app, their User Attributes are no longer valid.
 * You can reset their data by calling this function.
 * Make sure you do not set any new user attributes until the promise resolves.
 *
 */
export const resetAppUser = (): Promise<void> => Taplytics._resetAppUser()

/**
 * Use this function to log custom events.
 *
 * @param eventName The name of the event.
 * @param value A numerical value associated with the event. This is an optional parameter,
 * if no value is passed it is initalized to 0.
 * @param customAttributes An custom object that gets associated with the event. This is an
 * optional paramter, if no value is passed it is initalized with an empty object.
 */
export const logEvent = (eventName: string, value: number = 0, customAttributes: object = {}): void =>
  Taplytics._logEvent(eventName, value, customAttributes)

/**
 * Use this function to log revenue.
 *
 * @param eventName The name of the revenue event.
 * @param value A numerical value associated with the revenue event. This is an optional parameter,
 * if no value is passed it is initalized to 0.
 * @param customAttributes An custom object that gets associated with the revenue event. This is an
 * optional paramter, if no value is passed it is initalized with an empty object.
 */
export const logRevenue = (eventName: string, value: number = 0, customAttributes: object = {}): void =>
  Taplytics._logRevenue(eventName, value, customAttributes)
